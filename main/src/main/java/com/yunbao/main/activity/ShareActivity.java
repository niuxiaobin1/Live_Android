package com.yunbao.main.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.bean.UserItemBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.HttpClient;
import com.yunbao.common.mob.MobCallback;
import com.yunbao.common.mob.MobShareUtil;
import com.yunbao.common.utils.DateFormatUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.DownloadUtil;
import com.yunbao.common.utils.ProcessResultUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.views.SharePopupWindow;
import com.yunbao.video.utils.VideoLocalUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

public class ShareActivity extends AbsActivity {

    private RoundedImageView avatar;
    private TextView invite_code_rv;
    private ImageView Qrcode_image;
    private TextView shareTv;
    UserBean bean;

    private MobShareUtil mShareUtil;
    private SharePopupWindow sharePopupWindow;
    private ProcessResultUtil mProcessResultUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_share;
    }

    @Override
    protected void main() {
        super.main();
        mShareUtil = new MobShareUtil();
        mProcessResultUtil = new ProcessResultUtil(this);
        avatar = findViewById(R.id.avatar);
        invite_code_rv = findViewById(R.id.invite_code_rv);
        Qrcode_image = findViewById(R.id.Qrcode_image);
        shareTv = findViewById(R.id.shareTv);

        bean = CommonAppConfig.getInstance().getUserBean();
        ImgLoader.displayAvatar(mContext, bean.getAvatar(), avatar);

        invite_code_rv.setText(bean.getInvite_code());
        getQrcode();

        shareTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharePopupWindow == null) {
                    sharePopupWindow = new SharePopupWindow(ShareActivity.this);
                }
                sharePopupWindow.setOnShareAction(new SharePopupWindow.OnShareAction() {
                    @Override
                    public void share(String type) {
                        sharePopupWindow.dismiss();
                      checkPermission(type);
                    }
                });
                sharePopupWindow.showPopupWindow();
            }
        });
    }

    private void checkPermission(final String type) {
        mProcessResultUtil.requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        }, new Runnable() {
            @Override
            public void run() {
                new SaveImageTask(type)
                        .execute(getBitmapBg((RelativeLayout) findViewById(R.id.rootView)));
            }
        });
    }


    private void getQrcode() {
        HttpClient.getInstance().get("Agent.GetCode", MainHttpConsts.GET_BASE_INFO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            ImgLoader.displayAvatar(mContext, obj.getString("qr"), Qrcode_image);
                        }
                    }

                    @Override
                    public void onError() {
                    }
                });
    }


    /**
     * 生成分享图片
     */
    private Bitmap resultBitmap;
    private Canvas canvasTemp;
    private Bitmap bitmap;

    private Bitmap getBitmapBg(RelativeLayout pop_share) {
        pop_share.setDrawingCacheEnabled(true);
        pop_share.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        pop_share.layout(0, 0, pop_share.getMeasuredWidth(), pop_share.getMeasuredHeight());
        pop_share.setGravity(Gravity.CENTER_HORIZONTAL);
        //bitmap = getRoundedCornerBitmap(Bitmap.createBitmap(share_ll.getDrawingCache()), 10);
        if (resultBitmap != null && !resultBitmap.isRecycled()) {
            //resultBitmap.recycle();
            resultBitmap = null;
        }
        resultBitmap = Bitmap.createBitmap(pop_share.getMeasuredWidth(), pop_share.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        canvasTemp = new Canvas(resultBitmap);
        canvasTemp.drawColor(Color.WHITE);
        bitmap = Bitmap.createBitmap(pop_share.getDrawingCache());
        canvasTemp.drawBitmap(bitmap, 0, 0, new Paint());
        pop_share.setDrawingCacheEnabled(false);

        return resultBitmap;
    }


    private class SaveImageTask extends AsyncTask<Bitmap, Void, String> {
        private String shareType;

        public SaveImageTask(String shareType) {
            this.shareType = shareType;
        }

        @Override
        protected String doInBackground(Bitmap... params) {
            File imageFile = null;
            try {
                String sdcard = Environment.getExternalStorageDirectory().toString();

                File file = new File(sdcard + "/Download");
                if (!file.exists()) {
                    file.mkdirs();
                }

                imageFile = new File(file.getAbsolutePath(), new Date().getTime() + ".jpg");
                FileOutputStream outStream = null;
                outStream = new FileOutputStream(imageFile);
                Bitmap image = params[0];
                image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();


                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(imageFile);
                intent.setData(uri);
                mContext.sendBroadcast(intent);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦


            } catch (Exception e) {
                return "";
            }
            if (imageFile != null) {
                return imageFile.getPath();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("nxb", result);
            mShareUtil.shareImage(ShareActivity.this, shareType,
                    result, new MobCallback() {
                        @Override
                        public void onSuccess(Object data) {

                        }

                        @Override
                        public void onError() {

                        }

                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onFinish() {

                        }
                    });

        }
    }
}
