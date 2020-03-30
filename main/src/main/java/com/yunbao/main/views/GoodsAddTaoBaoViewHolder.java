package com.yunbao.main.views;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.ImageResultCallback;
import com.yunbao.common.upload.UploadBean;
import com.yunbao.common.upload.UploadCallback;
import com.yunbao.common.upload.UploadQnImpl;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.ProcessImageUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.live.views.AbsCommonViewHolder;
import com.yunbao.main.R;
import com.yunbao.common.bean.GoodsBean;
import com.yunbao.main.http.MainHttpUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.yunbao.main.http.MainHttpConsts.SET_GOODS;

/**
 * Created by cxf on 2019/8/29.
 * 添加 淘宝商品
 */

public class GoodsAddTaoBaoViewHolder extends AbsCommonViewHolder implements View.OnClickListener {

    private EditText mLink;
    private EditText mName;
    private EditText mPriceOrigin;
    private EditText mPriceNow;
    private EditText mDes;
    private View mBtnImgDel;
    private ImageView mImg;
    private ProcessImageUtil mImageUtil;
    private File mImgFile;
    private AbsActivity mActivity;
    private View mBtnConfirm;
    private UploadQnImpl mUploadStrategy;


    public GoodsAddTaoBaoViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_goods_add_taobao;
    }

    @Override
    public void init() {
        mActivity = (AbsActivity) mContext;
        mLink = (EditText) findViewById(R.id.link);
        mName = (EditText) findViewById(R.id.name);
        mPriceOrigin = (EditText) findViewById(R.id.price_origin);
        mPriceNow = (EditText) findViewById(R.id.price_now);
        mDes = (EditText) findViewById(R.id.des);
        mImg = (ImageView) findViewById(R.id.img);
        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(this);
        findViewById(R.id.btn_img_add).setOnClickListener(this);
        mBtnImgDel = findViewById(R.id.btn_img_del);
        mBtnImgDel.setOnClickListener(this);
        mImageUtil = new ProcessImageUtil(mActivity);
        mImageUtil.setImageResultCallback(new ImageResultCallback() {
            @Override
            public void beforeCamera() {

            }

            @Override
            public void onSuccess(File file) {
                if (file != null) {
                    mImgFile = file;
                    if (mImg != null) {
                        ImgLoader.display(mContext, file, mImg);
                    }
                    /*if (mBtnImgDel != null && mBtnImgDel.getVisibility() != View.VISIBLE) {
                        mBtnImgDel.setVisibility(View.VISIBLE);
                    }*/
                    setSubmitEnable();
                }
            }


            @Override
            public void onFailure() {
            }
        });
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setSubmitEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mLink.addTextChangedListener(textWatcher);
        mName.addTextChangedListener(textWatcher);
        mPriceOrigin.addTextChangedListener(textWatcher);
        mPriceNow.addTextChangedListener(textWatcher);
        mDes.addTextChangedListener(textWatcher);
    }


    /**
     * 添加图片
     */
    private void addImage() {
        DialogUitl.showStringArrayDialog(mContext, new Integer[]{R.string.camera, R.string.alumb}, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.camera) {
                    mImageUtil.getImageByCamera(false);
                } else if (tag == R.string.alumb) {
                    mImageUtil.getImageByAlumb(false);
                }
            }
        });
    }

    /**
     * 删除图片
     */
    private void deleteImage() {
        if (mImg != null) {
            mImg.setImageDrawable(null);
        }
        mImgFile = null;
        if (mBtnImgDel != null && mBtnImgDel.getVisibility() == View.VISIBLE) {
            mBtnImgDel.setVisibility(View.INVISIBLE);
        }
        setSubmitEnable();
    }


    private void setSubmitEnable() {
        if (mBtnConfirm != null) {
            String link = mLink.getText().toString().trim();
            String name = mName.getText().toString().trim();
            String priceOrigin = mPriceOrigin.getText().toString().trim();
            String priceNow = mPriceNow.getText().toString().trim();
            String des = mDes.getText().toString().trim();
            mBtnConfirm.setEnabled(!TextUtils.isEmpty(link)
                    && !TextUtils.isEmpty(name)
                   // && !TextUtils.isEmpty(priceOrigin)
                    && !TextUtils.isEmpty(priceNow)
                    && !TextUtils.isEmpty(des)
                    && mImgFile != null
            );
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_img_add) {
            addImage();
        } else if (i == R.id.btn_img_del) {
            deleteImage();
        } else if (i == R.id.btn_confirm) {
            confirm(v);
        }
    }

    private void confirm(final View view) {
        String link = mLink.getText().toString().trim();
        String name = mName.getText().toString().trim();
        String priceOrigin = mPriceOrigin.getText().toString().trim();
        String priceNow = mPriceNow.getText().toString().trim();
        String des = mDes.getText().toString().trim();
        final GoodsBean goodsBean = new GoodsBean();
        goodsBean.setLink(link);
        goodsBean.setName(name);
        goodsBean.setPriceOrigin(priceOrigin);
        goodsBean.setPriceNow(priceNow);
        goodsBean.setDes(des);
        goodsBean.setLocalPath(mImgFile.getAbsolutePath());
        view.setEnabled(false);

        if (mUploadStrategy == null) {
            mUploadStrategy = new UploadQnImpl(mContext);
        }
        List<UploadBean> list = new ArrayList<>();
        list.add(new UploadBean(mImgFile));

        mUploadStrategy.upload(list, false, new UploadCallback() {
            @Override
            public void onFinish(List<UploadBean> list, boolean success) {
                if (success) {
                    if (list != null && list.size() > 0) {
                        String remoteFileName = list.get(0).getRemoteFileName();
                        goodsBean.setThumb(remoteFileName);
                        setGoods(view,goodsBean);
                    }
                }else{
                        view.setEnabled(true);
                }
            }
        });
    }


    private void setGoods(final View view,GoodsBean goodsBean) {
        MainHttpUtil.setGoods(0, goodsBean, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        ToastUtil.show(msg);
                        if(code==0){
                            finishAcitivty();
                        }
                    }
            @Override
            public void onError() {
                super.onError();
                view.setEnabled(true);
            }
              }
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImageUtil != null) {
            mImageUtil.release();
        }

        mImageUtil = null;
    }
}
