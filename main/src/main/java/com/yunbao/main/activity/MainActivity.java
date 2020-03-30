package com.yunbao.main.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.ViewPagerAdapter;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.custom.TabButtonGroup;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.LocationUtil;
import com.yunbao.common.utils.ProcessResultUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.VersionUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.activity.ChatActivity;
import com.yunbao.im.event.ImUnReadCountEvent;
import com.yunbao.im.utils.ImMessageUtil;
import com.yunbao.im.utils.ImPushUtil;
import com.yunbao.live.LiveConfig;
import com.yunbao.live.activity.LiveAnchorActivity;
import com.yunbao.live.bean.LiveBean;
import com.yunbao.live.bean.LiveKsyConfigBean;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.live.utils.LiveStorge;
import com.yunbao.main.R;
import com.yunbao.main.bean.BonusBean;
import com.yunbao.main.dialog.MainStartDialogFragment;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.main.interfaces.MainAppBarLayoutListener;
import com.yunbao.main.interfaces.MainStartChooseCallback;
import com.yunbao.main.presenter.CheckLivePresenter;
import com.yunbao.main.views.AbsMainViewHolder;
import com.yunbao.main.views.BonusViewHolder;
import com.yunbao.main.views.MainHomeViewHolder;
import com.yunbao.main.views.MainListViewHolder;
import com.yunbao.main.views.MainMeViewHolder;
import com.yunbao.main.views.MainNearViewHolder;
import com.yunbao.video.activity.VideoRecordActivity;
import com.yunbao.video.utils.VideoStorge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AbsActivity implements MainAppBarLayoutListener {

    private ViewGroup mRootView;
    private TabButtonGroup mTabButtonGroup;
    private ViewPager mViewPager;
    private List<FrameLayout> mViewList;
    private MainHomeViewHolder mHomeViewHolder;
    private MainNearViewHolder mNearViewHolder;
    private MainListViewHolder mListViewHolder;
    private MainMeViewHolder mMeViewHolder;
    private AbsMainViewHolder[] mViewHolders;
    private View mBottom;
    private int mDp70;
    private ObjectAnimator mUpAnimator;//向上动画
    private ObjectAnimator mDownAnimator;//向下动画
    private boolean mAnimating;
    private boolean mShowed = true;
    private boolean mHided;
    private ProcessResultUtil mProcessResultUtil;
    private CheckLivePresenter mCheckLivePresenter;
    private boolean mFristLoad;
    private long mLastClickBackTime;//上次点击back键的时间
    private HttpCallback mGetLiveSdkCallback;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    protected void main() {
        boolean showInvite = getIntent().getBooleanExtra(Constants.SHOW_INVITE, false);
        mRootView = (ViewGroup) findViewById(R.id.rootView);
        mTabButtonGroup = (TabButtonGroup) findViewById(R.id.tab_group);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(4);
        mViewList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mViewList.add(frameLayout);
        }
        mViewPager.setAdapter(new ViewPagerAdapter(mViewList));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                loadPageData(position, true);
                if (mViewHolders != null) {
                    for (int i = 0, length = mViewHolders.length; i < length; i++) {
                        AbsMainViewHolder vh = mViewHolders[i];
                        if (vh != null) {
                            vh.setShowed(position == i);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTabButtonGroup.setViewPager(mViewPager);
        mViewHolders = new AbsMainViewHolder[4];
        mDp70 = DpUtil.dp2px(70);
        mBottom = findViewById(R.id.bottom);
        mUpAnimator = ObjectAnimator.ofFloat(mBottom, "translationY", mDp70, 0);
        mDownAnimator = ObjectAnimator.ofFloat(mBottom, "translationY", 0, mDp70);
        mUpAnimator.setDuration(250);
        mDownAnimator.setDuration(250);
        mUpAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimating = false;
                mShowed = true;
                mHided = false;
            }
        });
        mDownAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimating = false;
                mShowed = false;
                mHided = true;
            }
        });
        mProcessResultUtil = new ProcessResultUtil(this);
        EventBus.getDefault().register(this);
        checkVersion();
        if (showInvite) {
            showInvitationCode();
        }
        requestBonus();
        loginIM();
        ImPushUtil.getInstance().resumePush();
        CommonAppConfig.getInstance().setLaunched(true);
        mFristLoad = true;
    }

    public void mainClick(View v) {
        if (!canClick()) {
            return;
        }
        int i = v.getId();
        if (i == R.id.btn_start) {
            showStartDialog();
        } else if (i == R.id.btn_search) {
            SearchActivity.forward(mContext);
        } else if (i == R.id.btn_msg) {
            ChatActivity.forward(mContext);
        }
    }

    private void showStartDialog() {
        MainStartDialogFragment dialogFragment = new MainStartDialogFragment();
        dialogFragment.setMainStartChooseCallback(mMainStartChooseCallback);
        dialogFragment.show(getSupportFragmentManager(), "MainStartDialogFragment");
    }

    private MainStartChooseCallback mMainStartChooseCallback = new MainStartChooseCallback() {
        @Override
        public void onLiveClick() {
            mProcessResultUtil.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
            }, mStartLiveRunnable);
        }

        @Override
        public void onVideoClick() {
            mProcessResultUtil.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
            }, mStartVideoRunnable);
        }
    };

    private Runnable mStartLiveRunnable = new Runnable() {
        @Override
        public void run() {
            if (CommonAppConfig.LIVE_SDK_CHANGED) {
                if (mGetLiveSdkCallback == null) {
                    mGetLiveSdkCallback = new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0 && info.length > 0) {
                                try {
                                    JSONObject obj = JSON.parseObject(info[0]);
                                    int haveStore=obj.getIntValue("isshop");
                                    LiveAnchorActivity.forward(mContext, obj.getIntValue("live_sdk"), JSON.parseObject(obj.getString("android"), LiveKsyConfigBean.class),haveStore);
                                } catch (Exception e) {
                                    LiveAnchorActivity.forward(mContext, CommonAppConfig.LIVE_SDK_USED, LiveConfig.getDefaultKsyConfig(),0);
                                }
                            }
                        }
                    };
                }
                LiveHttpUtil.getLiveSdk(mGetLiveSdkCallback);
            } else {
                LiveAnchorActivity.forward(mContext, CommonAppConfig.LIVE_SDK_USED, LiveConfig.getDefaultKsyConfig(),0);
            }
        }
    };


    private Runnable mStartVideoRunnable = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(mContext, VideoRecordActivity.class));
        }
    };

    /**
     * 检查版本更新
     */
    private void checkVersion() {
        CommonAppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                if (configBean != null) {
                    if (configBean.getMaintainSwitch() == 1) {//开启维护
                        DialogUitl.showSimpleTipDialog(mContext, WordUtil.getString(R.string.main_maintain_notice), configBean.getMaintainTips());
                    }
                    if (!VersionUtil.isLatest(configBean.getVersion())) {
                        VersionUtil.showDialog(mContext, configBean, configBean.getDownloadApkUrl());
                    }
                }
            }
        });
    }

    /**
     * 填写邀请码
     */
    private void showInvitationCode() {
        DialogUitl.showSimpleInputDialog(mContext, WordUtil.getString(R.string.main_input_invatation_code), new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(final Dialog dialog, final String content) {
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(R.string.main_input_invatation_code);
                    return;
                }
                MainHttpUtil.setDistribut(content, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
                            dialog.dismiss();
                        } else {
                            ToastUtil.show(msg);
                        }
                    }
                });
            }
        });
    }

    /**
     * 签到奖励
     */
    private void requestBonus() {
        MainHttpUtil.requestBonus(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    if (obj.getIntValue("bonus_switch") == 0) {
                        return;
                    }
                    int day = obj.getIntValue("bonus_day");
                    if (day <= 0) {
                        return;
                    }
                    List<BonusBean> list = JSON.parseArray(obj.getString("bonus_list"), BonusBean.class);
                    BonusViewHolder bonusViewHolder = new BonusViewHolder(mContext, mRootView);
                    bonusViewHolder.setData(list, day, obj.getString("count_day"));
                    bonusViewHolder.show();
                }
            }
        });
    }

    /**
     * 登录IM
     */
    private void loginIM() {
        String uid = CommonAppConfig.getInstance().getUid();
        ImMessageUtil.getInstance().loginImClient(uid);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFristLoad) {
            mFristLoad = false;
            getLocation();
            loadPageData(0, false);
            if (mHomeViewHolder != null) {
                mHomeViewHolder.setShowed(true);
            }
            if (ImPushUtil.getInstance().isClickNotification()) {//MainActivity是点击通知打开的
                ImPushUtil.getInstance().setClickNotification(false);
                int notificationType = ImPushUtil.getInstance().getNotificationType();
                if (notificationType == Constants.JPUSH_TYPE_LIVE) {
                    if (mHomeViewHolder != null) {
                        mHomeViewHolder.setCurrentPage(0);
                    }
                } else if (notificationType == Constants.JPUSH_TYPE_MESSAGE) {
                    if (mHomeViewHolder != null) {
                        mHomeViewHolder.setCurrentPage(1);
                    }
                }
                ImPushUtil.getInstance().setNotificationType(Constants.JPUSH_TYPE_NONE);
            } else {
                if (mHomeViewHolder != null) {
                    mHomeViewHolder.setCurrentPage(1);
                }
            }
        }
    }

    /**
     * 获取所在位置
     */
    private void getLocation() {
        mProcessResultUtil.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, new Runnable() {
            @Override
            public void run() {
                LocationUtil.getInstance().startLocation();
            }
        });
    }


    @Override
    protected void onDestroy() {
        if (mTabButtonGroup != null) {
            mTabButtonGroup.cancelAnim();
        }
        EventBus.getDefault().unregister(this);
        LiveHttpUtil.cancel(LiveHttpConsts.GET_LIVE_SDK);
        MainHttpUtil.cancel(CommonHttpConsts.GET_CONFIG);
        MainHttpUtil.cancel(MainHttpConsts.REQUEST_BONUS);
        MainHttpUtil.cancel(MainHttpConsts.GET_BONUS);
        MainHttpUtil.cancel(MainHttpConsts.SET_DISTRIBUT);
        if (mCheckLivePresenter != null) {
            mCheckLivePresenter.cancel();
        }
        LocationUtil.getInstance().stopLocation();
        if (mProcessResultUtil != null) {
            mProcessResultUtil.release();
        }
        CommonAppConfig.getInstance().setGiftListJson(null);
        CommonAppConfig.getInstance().setLaunched(false);
        LiveStorge.getInstance().clear();
        VideoStorge.getInstance().clear();
        super.onDestroy();
    }

    public static void forward(Context context) {
        forward(context, false);
    }

    public static void forward(Context context, boolean showInvite) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.SHOW_INVITE, showInvite);
        context.startActivity(intent);
    }

    /**
     * 观看直播
     */
    public void watchLive(LiveBean liveBean, String key, int position) {
        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new CheckLivePresenter(mContext);
        }
        if (CommonAppConfig.LIVE_ROOM_SCROLL) {
            mCheckLivePresenter.watchLive(liveBean, key, position);
        } else {
            mCheckLivePresenter.watchLive(liveBean);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImUnReadCountEvent(ImUnReadCountEvent e) {
        String unReadCount = e.getUnReadCount();
        if (!TextUtils.isEmpty(unReadCount)) {
            if (mHomeViewHolder != null) {
                mHomeViewHolder.setUnReadCount(unReadCount);
            }
            if (mNearViewHolder != null) {
                mNearViewHolder.setUnReadCount(unReadCount);
            }
        }
    }

    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        if (curTime - mLastClickBackTime > 2000) {
            mLastClickBackTime = curTime;
            ToastUtil.show(R.string.main_click_next_exit);
            return;
        }
        super.onBackPressed();
    }


    private void loadPageData(int position, boolean needlLoadData) {
        if (mViewHolders == null) {
            return;
        }
        AbsMainViewHolder vh = mViewHolders[position];
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }
                if (position == 0) {
                    mHomeViewHolder = new MainHomeViewHolder(mContext, parent);
                    mHomeViewHolder.setAppBarLayoutListener(this);
                    vh = mHomeViewHolder;
                } else if (position == 1) {
                    mNearViewHolder = new MainNearViewHolder(mContext, parent);
                    mNearViewHolder.setAppBarLayoutListener(this);
                    vh = mNearViewHolder;
                } else if (position == 2) {
                    mListViewHolder = new MainListViewHolder(mContext, parent);
                    vh = mListViewHolder;
                } else if (position == 3) {
                    mMeViewHolder = new MainMeViewHolder(mContext, parent);
                    vh = mMeViewHolder;
                }
                if (vh == null) {
                    return;
                }
                mViewHolders[position] = vh;
                vh.addToParent();
                vh.subscribeActivityLifeCycle();
            }
        }
        if (needlLoadData && vh != null) {
            vh.loadData();
        }
    }

    @Override
    public void onOffsetChanged(boolean up) {
        if (!mAnimating) {
            if (up) {
                if (mShowed && mDownAnimator != null) {
                    mDownAnimator.start();
                }
            } else {
                if (mHided && mUpAnimator != null) {
                    mUpAnimator.start();
                }
            }
        }
    }
}
