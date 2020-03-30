package com.yunbao.live.dialog;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.adapter.ViewPagerAdapter;
import com.yunbao.common.bean.LiveGiftBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.ScreenDimenUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveActivity;
import com.yunbao.live.activity.LiveAudienceActivity;
import com.yunbao.live.bean.BackPackGiftBean;
import com.yunbao.live.bean.LiveGuardInfo;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.live.views.AbsLiveGiftViewHolder;
import com.yunbao.live.views.LiveGiftGiftViewHolder;
import com.yunbao.live.views.LiveGiftPackageViewHolder;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/12.
 * 送礼物的弹窗
 */

public class LiveGiftDialogFragment extends AbsDialogFragment implements View.OnClickListener, AbsLiveGiftViewHolder.ActionListener {

    private static final int PAGE_COUNT = 2;
    private AbsLiveGiftViewHolder[] mViewHolders;
    private LiveGiftGiftViewHolder mLiveGiftGiftViewHolder;
    private LiveGiftPackageViewHolder mLiveGiftPackageViewHolder;
    private List<FrameLayout> mViewList;
    private ViewPager mViewPager;
    private View mBtnSendLian;
    private LiveGiftBean mLiveGiftBean;
    private String mCount = "1";
    private String mLiveUid;
    private String mStream;
    private Handler mHandler;
    private int mLianCountDownCount;//连送倒计时的数字
    private TextView mLianText;
    private static final int WHAT_LIAN = 100;
    private boolean mShowLianBtn;//是否显示了连送按钮
    private LiveGuardInfo mLiveGuardInfo;
    private int mCurrentPage;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_gift;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    public void setLiveGuardInfo(LiveGuardInfo liveGuardInfo) {
        mLiveGuardInfo = liveGuardInfo;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mViewPager.getLayoutParams();
        params.height = ScreenDimenUtil.getInstance().getScreenWdith() / 2 + DpUtil.dp2px(65);
        mViewPager.requestLayout();
        if (PAGE_COUNT > 1) {
            mViewPager.setOffscreenPageLimit(PAGE_COUNT - 1);
        }
        mViewHolders = new AbsLiveGiftViewHolder[2];
        mViewList = new ArrayList<>();
        for (int i = 0; i < PAGE_COUNT; i++) {
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
                loadPageData(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        MagicIndicator indicator = (MagicIndicator) findViewById(R.id.indicator);
        final String[] titles = new String[]{
                WordUtil.getString(R.string.live_send_gift),
                WordUtil.getString(R.string.live_send_gift_4)};
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.textColor2));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, R.color.white));
                simplePagerTitleView.setText(titles[index]);
                simplePagerTitleView.setTextSize(13);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mViewPager != null) {
                            mViewPager.setCurrentItem(index);
                        }
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                linePagerIndicator.setXOffset(DpUtil.dp2px(5));
                linePagerIndicator.setRoundRadius(DpUtil.dp2px(2));
                linePagerIndicator.setColors(ContextCompat.getColor(mContext, R.color.white));
                return linePagerIndicator;
            }

        });
        indicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(indicator, mViewPager);

        mBtnSendLian = mRootView.findViewById(R.id.btn_send_lian);
        mBtnSendLian.setOnClickListener(this);
        mLianText = (TextView) mRootView.findViewById(R.id.lian_text);
        mRootView.findViewById(R.id.btn_luck_gift_tip).setOnClickListener(this);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mLianCountDownCount--;
                if (mLianCountDownCount == 0) {
                    hideLianBtn();
                } else {
                    if (mLianText != null) {
                        mLianText.setText(mLianCountDownCount + "s");
                        if (mHandler != null) {
                            mHandler.sendEmptyMessageDelayed(WHAT_LIAN, 1000);
                        }
                    }
                }
            }
        };
        Bundle bundle = getArguments();
        if (bundle != null) {
            mLiveUid = bundle.getString(Constants.LIVE_UID);
            mStream = bundle.getString(Constants.LIVE_STREAM);
        }
        loadPageData(0);
    }

    private void loadPageData(int position) {
        this.mCurrentPage=position;
        if (mViewHolders == null) {
            return;
        }
        AbsLiveGiftViewHolder vh = mViewHolders[position];
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }
                if (position == 0) {
                    mLiveGiftGiftViewHolder = new LiveGiftGiftViewHolder(mContext, parent, mLiveUid, mStream);
                    mLiveGiftGiftViewHolder.setActionListener(LiveGiftDialogFragment.this);
                    vh = mLiveGiftGiftViewHolder;
                }
                if (position == 1) {
                    mLiveGiftPackageViewHolder = new LiveGiftPackageViewHolder(mContext, parent, mLiveUid, mStream);
                    mLiveGiftPackageViewHolder.setActionListener(LiveGiftDialogFragment.this);
                    vh = mLiveGiftPackageViewHolder;
                }
                if (vh == null) {
                    return;
                }
                mViewHolders[position] = vh;
                vh.addToParent();
            }
        }

        if (vh != null) {
            vh.loadData();
        }
    }


    @Override
    public void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        LiveHttpUtil.cancel(LiveHttpConsts.GET_GIFT_LIST);
        LiveHttpUtil.cancel(LiveHttpConsts.GET_COIN);
        LiveHttpUtil.cancel(LiveHttpConsts.SEND_GIFT);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_send_lian) {
            sendGift();
        } else if (i == R.id.btn_luck_gift_tip) {
            dismiss();
            ((LiveActivity) mContext).openLuckGiftTip();
        }
    }

    /**
     * 跳转到我的钻石
     */
    private void forwardMyCoin() {
        dismiss();
        //RouteUtil.forwardMyCoin(mContext);
        ((LiveAudienceActivity) mContext).openChargeWindow();
    }


    /**
     * 赠送礼物
     */
    public void sendGift() {
        if (TextUtils.isEmpty(mLiveUid) || TextUtils.isEmpty(mStream) || mLiveGiftBean == null) {
            return;
        }
        if (mLiveGuardInfo != null) {
            if (mLiveGiftBean.getMark() == LiveGiftBean.MARK_GUARD && mLiveGuardInfo.getMyGuardType() != Constants.GUARD_TYPE_YEAR) {
                ToastUtil.show(R.string.guard_gift_tip);
                return;
            }
        }
        SendGiftCallback callback = new SendGiftCallback(mLiveGiftBean);
        LiveHttpUtil.sendGift(mLiveUid, mStream, mLiveGiftBean.getId(), mCount, callback,mLiveGiftBean instanceof BackPackGiftBean);
    }

    /**
     * 隐藏连送按钮
     */
    private void hideLianBtn() {
        mShowLianBtn = false;
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_LIAN);
        }
        if (mBtnSendLian != null && mBtnSendLian.getVisibility() == View.VISIBLE) {
            mBtnSendLian.setVisibility(View.INVISIBLE);
        }
        if (mViewPager != null) {
            AbsLiveGiftViewHolder vh = mViewHolders[mViewPager.getCurrentItem()];
            if (vh != null) {
                vh.setVisibleSendGroup(true);
            }
        }
    }

    /**
     * 显示连送按钮
     */
    private void showLianBtn() {
        if (mLianText != null) {
            mLianText.setText("5s");
        }
        mLianCountDownCount = 5;
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_LIAN);
            mHandler.sendEmptyMessageDelayed(WHAT_LIAN, 1000);
        }
        if (mShowLianBtn) {
            return;
        }
        mShowLianBtn = true;
        if (mViewPager != null) {
            AbsLiveGiftViewHolder vh = mViewHolders[mViewPager.getCurrentItem()];
            if (vh != null) {
                vh.setVisibleSendGroup(false);
            }
        }
        if (mBtnSendLian != null && mBtnSendLian.getVisibility() != View.VISIBLE) {
            mBtnSendLian.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCountChanged(String count) {
        mCount = count;
    }

    @Override
    public void onGiftChanged(LiveGiftBean bean) {
        mLiveGiftBean = bean;
        hideLianBtn();
    }

    @Override
    public void onSendClick() {
        sendGift();
    }

    @Override
    public void onCoinClick() {
        forwardMyCoin();
    }


    private class SendGiftCallback extends HttpCallback {

        private LiveGiftBean mGiftBean;

        public SendGiftCallback(LiveGiftBean giftBean) {
            mGiftBean = giftBean;
        }

        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                if (info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String coin = obj.getString("coin");
                    UserBean u = CommonAppConfig.getInstance().getUserBean();
                    if (u != null) {
                        u.setLevel(obj.getIntValue("level"));
                        u.setCoin(coin);
                    }
                    if (mLiveGiftGiftViewHolder != null) {
                        mLiveGiftGiftViewHolder.setCoinString(coin);
                    }

                    if (mContext != null && mGiftBean != null) {
                        ((LiveActivity) mContext).onCoinChanged(coin);
                        ((LiveActivity) mContext).sendGiftMessage(mGiftBean, obj.getString("gifttoken"));
                    }
                    if (mLiveGiftBean.getType() == LiveGiftBean.TYPE_NORMAL) {
                        showLianBtn();
                    }

                    mViewHolders[mCurrentPage].loadData();
                }
            } else {
                hideLianBtn();
                ToastUtil.show(msg);
            }
        }
    }


}
