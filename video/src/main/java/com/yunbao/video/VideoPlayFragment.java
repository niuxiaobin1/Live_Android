package com.yunbao.video;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.BaseLazyLoad;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.adapter.ImChatFacePagerAdapter;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.OnFaceClickListener;
import com.yunbao.common.mob.MobCallback;
import com.yunbao.common.mob.MobShareUtil;
import com.yunbao.common.mob.ShareData;
import com.yunbao.common.utils.DateFormatUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.DownloadUtil;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.JsonUtil;
import com.yunbao.common.utils.ProcessResultUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.video.activity.AbsVideoPlayActivity;
import com.yunbao.video.bean.VideoBean;
import com.yunbao.video.bean.VideoCommentBean;
import com.yunbao.video.dialog.VideoInputDialogFragment;
import com.yunbao.video.event.VideoDeleteEvent;
import com.yunbao.video.event.VideoShareEvent;
import com.yunbao.video.http.VideoHttpConsts;
import com.yunbao.video.http.VideoHttpUtil;
import com.yunbao.video.utils.VideoLocalUtil;
import com.yunbao.video.utils.VideoStorge;
import com.yunbao.video.views.VideoCommentViewHolder;
import com.yunbao.video.views.VideoScrollViewHolder;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class VideoPlayFragment extends BaseLazyLoad implements View.OnClickListener, OnFaceClickListener {

    protected ProcessResultUtil mProcessResultUtil;
    protected VideoCommentViewHolder mVideoCommentViewHolder;
    protected VideoInputDialogFragment mVideoInputDialogFragment;
    private View mFaceView;//表情面板
    private int mFaceHeight;//表情面板高度
    private View rootView;

    protected VideoScrollViewHolder mVideoScrollViewHolder;
    private Dialog mDownloadVideoDialog;
    private ClipboardManager mClipboardManager;
    private MobCallback mMobCallback;
    private MobShareUtil mMobShareUtil;
    private DownloadUtil mDownloadUtil;
    private ConfigBean mConfigBean;
    private VideoBean mShareVideoBean;
    protected String mVideoKey=Constants.VIDEO_HOME;
    private boolean mPaused;

    private MagicIndicator mIndicator;

    @Override
    protected void initViews() {
        super.initViews();
        mContext.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        CommonAppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean bean) {
                mConfigBean = bean;
            }
        });
        mProcessResultUtil = new ProcessResultUtil(mContext);
        mIndicator = (MagicIndicator) rootView.findViewById(R.id.indicator);
        final String[] titles =new String[]{"关注","推荐","商城"};
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.gray1));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, R.color.white));
                simplePagerTitleView.setText(titles[index]);
                simplePagerTitleView.setTextSize(18);
                simplePagerTitleView.getPaint().setFakeBoldText(true);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                linePagerIndicator.setXOffset(DpUtil.dp2px(5));
                linePagerIndicator.setYOffset(DpUtil.dp2px(0));
                linePagerIndicator.setRoundRadius(DpUtil.dp2px(2));
                linePagerIndicator.setColors(ContextCompat.getColor(mContext, R.color.white));
                return linePagerIndicator;
            }
        });
        mIndicator.setNavigator(commonNavigator);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = getLayoutInflater().inflate(R.layout.main_home_parent_view2, container, false);
        return rootView;
    }


    @Override
    protected void lazyLoad() {
        VideoHttpUtil.getHomeVideoList(1, mRefreshCallback);
    }


    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPaused = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }


    /**
     * 复制视频链接
     */
    public void copyLink(VideoBean videoBean) {
        if (videoBean == null) {
            return;
        }
        if (mClipboardManager == null) {
            mClipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        }
        ClipData clipData = ClipData.newPlainText("text", videoBean.getHref());
        mClipboardManager.setPrimaryClip(clipData);
        ToastUtil.show(WordUtil.getString(com.yunbao.video.R.string.copy_success));
    }

    /**
     * 分享页面链接
     */
    public void shareVideoPage(String type, VideoBean videoBean) {
        if (videoBean == null || mConfigBean == null) {
            return;
        }
        if (mMobCallback == null) {
            mMobCallback = new MobCallback() {

                @Override
                public void onSuccess(Object data) {
                    if (mShareVideoBean == null) {
                        return;
                    }
                    VideoHttpUtil.setVideoShare(mShareVideoBean.getId(), new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0 && info.length > 0 && mShareVideoBean != null) {
                                JSONObject obj = JSON.parseObject(info[0]);
                                EventBus.getDefault().post(new VideoShareEvent(mShareVideoBean.getId(), obj.getString("shares")));
                            } else {
                                ToastUtil.show(msg);
                            }
                        }
                    });
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
            };
        }
        mShareVideoBean = videoBean;
        ShareData data = new ShareData();
        data.setTitle(mConfigBean.getVideoShareTitle());
        data.setDes(mConfigBean.getVideoShareDes());
        data.setImgUrl(videoBean.getThumbs());
        String webUrl = HtmlConfig.SHARE_VIDEO + videoBean.getId();
        data.setWebUrl(webUrl);
        if (mMobShareUtil == null) {
            mMobShareUtil = new MobShareUtil();
        }
        mMobShareUtil.execute(type, data, mMobCallback);
    }


    /**
     * 下载视频
     */
    public void downloadVideo(final VideoBean videoBean) {
        if (mProcessResultUtil == null || videoBean == null || TextUtils.isEmpty(videoBean.getHref())) {
            return;
        }
        mProcessResultUtil.requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        }, new Runnable() {
            @Override
            public void run() {
                mDownloadVideoDialog = DialogUitl.loadingDialog(mContext);
                mDownloadVideoDialog.show();
                if (mDownloadUtil == null) {
                    mDownloadUtil = new DownloadUtil();
                }
                String fileName = "YB_VIDEO_" + videoBean.getTitle() + "_" + DateFormatUtil.getCurTimeString() + ".mp4";
                mDownloadUtil.download(videoBean.getTag(), CommonAppConfig.VIDEO_PATH, fileName, videoBean.getHref(), new DownloadUtil.Callback() {
                    @Override
                    public void onSuccess(File file) {
                        ToastUtil.show(com.yunbao.video.R.string.video_download_success);
                        if (mDownloadVideoDialog != null && mDownloadVideoDialog.isShowing()) {
                            mDownloadVideoDialog.dismiss();
                        }
                        mDownloadVideoDialog = null;
                        String path = file.getAbsolutePath();
                        try {
                            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                            mmr.setDataSource(path);
                            String d = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                            if (StringUtil.isInt(d)) {
                                long duration = Long.parseLong(d);
                                VideoLocalUtil.saveVideoInfo(mContext, path, duration);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onProgress(int progress) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.show(com.yunbao.video.R.string.video_download_failed);
                        if (mDownloadVideoDialog != null && mDownloadVideoDialog.isShowing()) {
                            mDownloadVideoDialog.dismiss();
                        }
                        mDownloadVideoDialog = null;
                    }
                });
            }
        });
    }

    /**
     * 删除视频
     */
    public void deleteVideo(final VideoBean videoBean) {
        VideoHttpUtil.videoDelete(videoBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (mVideoScrollViewHolder != null) {
                        EventBus.getDefault().post(new VideoDeleteEvent(videoBean.getId()));
                        mVideoScrollViewHolder.deleteVideo(videoBean);
                    }
                }
            }
        });
    }


    public boolean isPaused() {
        return mPaused;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == com.yunbao.video.R.id.btn_send) {
            if (mVideoInputDialogFragment != null) {
                mVideoInputDialogFragment.sendComment();
            }
        }
    }

    /**
     * 打开评论输入框
     */
    public void openCommentInputWindow(boolean openFace, String videoId, String videoUid, VideoCommentBean bean) {
        if (mFaceView == null) {
            mFaceView = initFaceView();
        }
        VideoInputDialogFragment fragment = new VideoInputDialogFragment();
        fragment.attachFragment(VideoPlayFragment.this);
        fragment.setVideoInfo(videoId, videoUid);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.VIDEO_FACE_OPEN, openFace);
        bundle.putInt(Constants.VIDEO_FACE_HEIGHT, mFaceHeight);
        bundle.putParcelable(Constants.VIDEO_COMMENT_BEAN, bean);
        fragment.setArguments(bundle);
        mVideoInputDialogFragment = fragment;
        fragment.show(getChildFragmentManager(), "VideoInputDialogFragment");
    }

    public View getFaceView() {
        if (mFaceView == null) {
            mFaceView = initFaceView();
        }
        return mFaceView;
    }

    /**
     * 初始化表情控件
     */
    private View initFaceView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(com.yunbao.video.R.layout.view_chat_face, null);
        v.measure(0, 0);
        mFaceHeight = v.getMeasuredHeight();
        v.findViewById(com.yunbao.video.R.id.btn_send).setOnClickListener(this);
        final RadioGroup radioGroup = (RadioGroup) v.findViewById(com.yunbao.video.R.id.radio_group);
        ViewPager viewPager = (ViewPager) v.findViewById(com.yunbao.video.R.id.viewPager);
        viewPager.setOffscreenPageLimit(10);
        ImChatFacePagerAdapter adapter = new ImChatFacePagerAdapter(mContext, this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((RadioButton) radioGroup.getChildAt(position)).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for (int i = 0, pageCount = adapter.getCount(); i < pageCount; i++) {
            RadioButton radioButton = (RadioButton) inflater.inflate(com.yunbao.video.R.layout.view_chat_indicator, radioGroup, false);
            radioButton.setId(i + 10000);
            if (i == 0) {
                radioButton.setChecked(true);
            }
            radioGroup.addView(radioButton);
        }
        return v;
    }

    /**
     * 显示评论
     */
    public void openCommentWindow(String videoId, String videoUid) {
        if (mVideoCommentViewHolder == null) {
            if (mContext instanceof AbsVideoPlayActivity){
                mVideoCommentViewHolder = new VideoCommentViewHolder(mContext, (ViewGroup) rootView.findViewById(com.yunbao.video.R.id.root));
            }else{
                mVideoCommentViewHolder = new VideoCommentViewHolder(VideoPlayFragment.this, (ViewGroup) rootView.findViewById(com.yunbao.video.R.id.root));
            }
            mVideoCommentViewHolder.addToParent();
        }
        mVideoCommentViewHolder.setVideoInfo(videoId, videoUid);
        mVideoCommentViewHolder.showBottom();
    }

    /**
     * 隐藏评论
     */
    public void hideCommentWindow(boolean commentSuccess) {
        if (mVideoCommentViewHolder != null) {
            mVideoCommentViewHolder.hideBottom();
            if (commentSuccess) {
                mVideoCommentViewHolder.needRefresh();
            }
        }
        mVideoInputDialogFragment = null;
    }

    @Override
    public void onFaceClick(String str, int faceImageRes) {
        if (mVideoInputDialogFragment != null) {
            mVideoInputDialogFragment.onFaceClick(str, faceImageRes);
        }
    }

    @Override
    public void onFaceDeleteClick() {
        if (mVideoInputDialogFragment != null) {
            mVideoInputDialogFragment.onFaceDeleteClick();
        }
    }

    public void release() {
        if (mVideoCommentViewHolder != null) {
            mVideoCommentViewHolder.release();
        }
        if (mProcessResultUtil != null) {
            mProcessResultUtil.release();
        }
        mVideoCommentViewHolder = null;
        mVideoInputDialogFragment = null;
        mProcessResultUtil = null;

        VideoHttpUtil.cancel(VideoHttpConsts.SET_VIDEO_SHARE);
        VideoHttpUtil.cancel(VideoHttpConsts.VIDEO_DELETE);
        if (mDownloadVideoDialog != null && mDownloadVideoDialog.isShowing()) {
            mDownloadVideoDialog.dismiss();
        }
        if (mVideoScrollViewHolder != null) {
            mVideoScrollViewHolder.release();
        }
        if (mMobShareUtil != null) {
            mMobShareUtil.release();
        }
        VideoStorge.getInstance().removeDataHelper(mVideoKey);
        mDownloadVideoDialog = null;
        mVideoScrollViewHolder = null;
        mMobShareUtil = null;
    }

    public void releaseVideoInputDialog() {
        mVideoInputDialogFragment = null;
    }

    public void setVideoScrollViewHolder(VideoScrollViewHolder videoScrollViewHolder) {
        mVideoScrollViewHolder = videoScrollViewHolder;
    }


    private HttpCallback mRefreshCallback = new HttpCallback() {

        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (info != null) {
                List list = JsonUtil.getJsonToList(Arrays.toString(info), VideoBean.class);
                VideoStorge.getInstance().put(Constants.VIDEO_HOME, list);
                mVideoScrollViewHolder = new VideoScrollViewHolder(VideoPlayFragment.this,
                        (ViewGroup) rootView.findViewById(com.yunbao.video.R.id.container), 0, mVideoKey, 1);
                mVideoScrollViewHolder.addToParent();
                mVideoScrollViewHolder.subscribeFragmentLifeCycle(VideoPlayFragment.this);
            }
        }


        @Override
        public void onError() {

        }

        @Override
        public void onFinish() {

        }
    };
}
