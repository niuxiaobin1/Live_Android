package com.yunbao.main.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
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
import com.yunbao.common.adapter.ViewPagerAdapter2;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.custom.TabButtonGroup;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.LocationUtil;
import com.yunbao.common.utils.ProcessResultUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.VersionUtil;
import com.yunbao.common.utils.WordUtil;
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
import com.yunbao.main.MessageFragment;
import com.yunbao.main.MineFragment;
import com.yunbao.main.NearFragment;
import com.yunbao.main.R;
import com.yunbao.main.bean.BonusBean;
import com.yunbao.main.dialog.MainStartDialogFragment;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.main.interfaces.MainStartChooseCallback;
import com.yunbao.main.presenter.CheckLivePresenter;
import com.yunbao.main.views.AbsMainViewHolder;
import com.yunbao.main.views.BonusViewHolder;
import com.yunbao.video.VideoPlayFragment;
import com.yunbao.video.activity.VideoRecordActivity;
import com.yunbao.video.utils.VideoStorge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AbsActivity {
    private ViewPager mViewPager;
    private FrameLayout bottom;
    private ViewGroup mRootView;
    private TabButtonGroup mTabButtonGroup;
    private View mBottom;
    private List<Fragment> mViewList;
    private AbsMainViewHolder[] mViewHolders;
    private ProcessResultUtil mProcessResultUtil;
    private boolean mFristLoad;
    private HttpCallback mGetLiveSdkCallback;
    private long mLastClickBackTime;//上次点击back键的时间
    private CheckLivePresenter mCheckLivePresenter;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main2;
    }

    @Override
    protected void main() {
        super.main();
        boolean showInvite = getIntent().getBooleanExtra(Constants.SHOW_INVITE, false);
        mRootView = (ViewGroup) findViewById(R.id.rootView);
        mTabButtonGroup = (TabButtonGroup) findViewById(R.id.tab_group);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(4);
        mViewList = new ArrayList<>();
        mViewList.add(new VideoPlayFragment());
        mViewList.add(new NearFragment());
        mViewList.add(new MessageFragment());
        mViewList.add(new MineFragment());
        mViewPager.setAdapter(new ViewPagerAdapter2(getSupportFragmentManager(), mViewList));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
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
        mBottom = findViewById(R.id.bottom);
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
        } else if (i == R.id.btn_live) {
            LiveVideoListActivity.forward(mContext);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFristLoad) {
            mFristLoad = false;
            getLocation();
//            loadPageData(0, false);
//            if (mHomeViewHolder != null) {
//                mHomeViewHolder.setShowed(true);
//            }
//            if (ImPushUtil.getInstance().isClickNotification()) {//MainActivity是点击通知打开的
//                ImPushUtil.getInstance().setClickNotification(false);
//                int notificationType = ImPushUtil.getInstance().getNotificationType();
//                if (notificationType == Constants.JPUSH_TYPE_LIVE) {
//                    if (mHomeViewHolder != null) {
//                        mHomeViewHolder.setCurrentPage(0);
//                    }
//                } else if (notificationType == Constants.JPUSH_TYPE_MESSAGE) {
//                    if (mHomeViewHolder != null) {
//                        mHomeViewHolder.setCurrentPage(1);
//                    }
//                }
//                ImPushUtil.getInstance().setNotificationType(Constants.JPUSH_TYPE_NONE);
//            } else {
//                if (mHomeViewHolder != null) {
//                    mHomeViewHolder.setCurrentPage(1);
//                }
//            }
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
                                    int haveStore = obj.getIntValue("isshop");
                                    LiveAnchorActivity.forward(mContext, obj.getIntValue("live_sdk"), JSON.parseObject(obj.getString("android"), LiveKsyConfigBean.class), haveStore);
                                } catch (Exception e) {
                                    LiveAnchorActivity.forward(mContext, CommonAppConfig.LIVE_SDK_USED, LiveConfig.getDefaultKsyConfig(), 0);
                                }
                            }
                        }
                    };
                }
                LiveHttpUtil.getLiveSdk(mGetLiveSdkCallback);
            } else {
                LiveAnchorActivity.forward(mContext, CommonAppConfig.LIVE_SDK_USED, LiveConfig.getDefaultKsyConfig(), 0);
            }
        }
    };


    private Runnable mStartVideoRunnable = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(mContext, VideoRecordActivity.class));
        }
    };



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public static void forward(Context context) {
        forward(context, false);
    }

    public static void forward(Context context, boolean showInvite) {
        Intent intent = new Intent(context, Main2Activity.class);
        intent.putExtra(Constants.SHOW_INVITE, showInvite);
        context.startActivity(intent);
    }

    /**
     * 登录IM
     */
    private void loginIM() {
        String uid = CommonAppConfig.getInstance().getUid();
        ImMessageUtil.getInstance().loginImClient(uid);
    }

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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImUnReadCountEvent(ImUnReadCountEvent e) {
//        String unReadCount = e.getUnReadCount();
//        if (!TextUtils.isEmpty(unReadCount)) {
//            if (mHomeViewHolder != null) {
//                mHomeViewHolder.setUnReadCount(unReadCount);
//            }
//            if (mNearViewHolder != null) {
//                mNearViewHolder.setUnReadCount(unReadCount);
//            }
//        }
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

}
