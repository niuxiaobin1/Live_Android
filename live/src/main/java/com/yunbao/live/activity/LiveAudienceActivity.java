package com.yunbao.live.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.opensource.svgaplayer.SVGAImageView;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.custom.MyViewPager;
import com.yunbao.common.dialog.LiveChargeDialogFragment;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.pay.PayCallback;
import com.yunbao.common.pay.PayPresenter;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.RandomUtil;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;

import com.yunbao.live.R;
import com.yunbao.live.adapter.LiveRoomScrollAdapter;
import com.yunbao.live.bean.LiveBean;
import com.yunbao.live.bean.LiveGuardInfo;
import com.yunbao.live.bean.LiveUserGiftBean;
import com.yunbao.live.dialog.LiveGiftDialogFragment;
import com.yunbao.live.dialog.LiveGoodsDialogFragment;
import com.yunbao.live.event.LinkMicTxAccEvent;
import com.yunbao.live.event.LiveRoomChangeEvent;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.live.presenter.LiveLinkMicAnchorPresenter;
import com.yunbao.live.presenter.LiveLinkMicPkPresenter;
import com.yunbao.live.presenter.LiveLinkMicPresenter;
import com.yunbao.live.presenter.LiveRoomCheckLivePresenter;
import com.yunbao.live.socket.SocketChatUtil;
import com.yunbao.live.socket.SocketClient;
import com.yunbao.live.utils.LiveStorge;
import com.yunbao.live.views.LiveAudienceViewHolder;
import com.yunbao.live.views.LiveEndViewHolder;
import com.yunbao.live.views.LivePlayKsyViewHolder;
import com.yunbao.live.views.LivePlayTxViewHolder;
import com.yunbao.live.views.LiveRoomPlayViewHolder;
import com.yunbao.live.views.LiveRoomViewHolder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by cxf on 2018/10/10.
 */

public class LiveAudienceActivity extends LiveActivity {

    private static final String TAG = "LiveAudienceActivity";

    public static void forward(Context context, LiveBean liveBean, int liveType, int liveTypeVal, String key, int position, int liveSdk) {
        Intent intent = new Intent(context, LiveAudienceActivity.class);
        intent.putExtra(Constants.LIVE_BEAN, liveBean);
        intent.putExtra(Constants.LIVE_TYPE, liveType);
        intent.putExtra(Constants.LIVE_TYPE_VAL, liveTypeVal);
        intent.putExtra(Constants.LIVE_KEY, key);
        intent.putExtra(Constants.LIVE_POSITION, position);
        intent.putExtra(Constants.LIVE_SDK, liveSdk);
        context.startActivity(intent);
    }

    private String mKey;
    private int mPosition;
    private RecyclerView mRecyclerView;
    private LiveRoomScrollAdapter mRoomScrollAdapter;
    private View mMainContentView;
    private MyViewPager mViewPager;
    private ViewGroup mSecondPage;//默认显示第二页
    private FrameLayout mContainerWrap;
    private LiveRoomPlayViewHolder mLivePlayViewHolder;
    private LiveAudienceViewHolder mLiveAudienceViewHolder;
    private boolean mEnd;
    private boolean mCoinNotEnough;//余额不足
    private LiveRoomCheckLivePresenter mCheckLivePresenter;
    private boolean mLighted;
    private PayPresenter mPayPresenter;

    @Override
    public <T extends View> T findViewById(@IdRes int id) {
        if (CommonAppConfig.LIVE_ROOM_SCROLL) {
            if (mMainContentView != null) {
                return mMainContentView.findViewById(id);
            }
        }
        return super.findViewById(id);
    }

    @Override
    protected int getLayoutId() {
        if (CommonAppConfig.LIVE_ROOM_SCROLL) {
            return R.layout.activity_live_audience_2;
        }
        return R.layout.activity_live_audience;
    }

    public void setScrollFrozen(boolean frozen) {
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutFrozen(frozen);
        }
    }

    @Override
    protected void main() {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (CommonAppConfig.LIVE_ROOM_SCROLL) {
            mRecyclerView = super.findViewById(R.id.recyclerView);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            mMainContentView = LayoutInflater.from(mContext).inflate(R.layout.activity_live_audience, null, false);
        }
        super.main();
        Intent intent = getIntent();
        mLiveSDK = intent.getIntExtra(Constants.LIVE_SDK, Constants.LIVE_SDK_KSY);
        L.e(TAG, "直播sdk----->" + (mLiveSDK == Constants.LIVE_SDK_KSY ? "金山云" : "腾讯云"));
        if (mLiveSDK == Constants.LIVE_SDK_TX || CommonAppConfig.LIVE_ROOM_SCROLL) {
            //腾讯视频播放器
            mLivePlayViewHolder = new LivePlayTxViewHolder(mContext, (ViewGroup) findViewById(R.id.play_container));
        } else {
            //金山云播放器
            mLivePlayViewHolder = new LivePlayKsyViewHolder(mContext, (ViewGroup) findViewById(R.id.play_container));
        }
        mLivePlayViewHolder.addToParent();
        mLivePlayViewHolder.subscribeActivityLifeCycle();
        mViewPager = (MyViewPager) findViewById(R.id.viewPager);
        mSecondPage = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.view_audience_page, mViewPager, false);
        mContainerWrap = mSecondPage.findViewById(R.id.container_wrap);
        mContainer = mSecondPage.findViewById(R.id.container);
        mLiveRoomViewHolder = new LiveRoomViewHolder(mContext, mContainer, (GifImageView) mSecondPage.findViewById(R.id.gift_gif), (SVGAImageView) mSecondPage.findViewById(R.id.gift_svga), mContainerWrap);
        mLiveRoomViewHolder.addToParent();
        mLiveRoomViewHolder.subscribeActivityLifeCycle();
        mLiveAudienceViewHolder = new LiveAudienceViewHolder(mContext, mContainer);
        mLiveAudienceViewHolder.addToParent();
        mLiveAudienceViewHolder.setUnReadCount(getImUnReadCount());
        mLiveBottomViewHolder = mLiveAudienceViewHolder;
        mViewPager.setAdapter(new PagerAdapter() {

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                if (position == 0) {
                    View view = new View(mContext);
                    view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    container.addView(view);
                    return view;
                } else {
                    container.addView(mSecondPage);
                    return mSecondPage;
                }
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            }
        });
        mViewPager.setCurrentItem(1);
        mLiveLinkMicPresenter = new LiveLinkMicPresenter(mContext, mLivePlayViewHolder, false, mLiveSDK, mLiveAudienceViewHolder.getContentView());
        mLiveLinkMicAnchorPresenter = new LiveLinkMicAnchorPresenter(mContext, mLivePlayViewHolder, false, mLiveSDK, null);
        mLiveLinkMicPkPresenter = new LiveLinkMicPkPresenter(mContext, mLivePlayViewHolder, false, null);
        if (CommonAppConfig.LIVE_ROOM_SCROLL) {
            mKey = intent.getStringExtra(Constants.LIVE_KEY);
            mPosition = intent.getIntExtra(Constants.LIVE_POSITION, 0);
            List<LiveBean> list = LiveStorge.getInstance().get(mKey);
            mRoomScrollAdapter = new LiveRoomScrollAdapter(mContext, list, mPosition);
            mRoomScrollAdapter.setActionListener(new LiveRoomScrollAdapter.ActionListener() {
                @Override
                public void onPageSelected(LiveBean liveBean, ViewGroup container, boolean first) {
                    L.e(TAG, "onPageSelected----->" + liveBean);
                    if (mMainContentView != null && container != null) {
                        ViewParent parent = mMainContentView.getParent();
                        if (parent != null) {
                            ViewGroup viewGroup = (ViewGroup) parent;
                            if (viewGroup != container) {
                                viewGroup.removeView(mMainContentView);
                                container.addView(mMainContentView);
                            }
                        } else {
                            container.addView(mMainContentView);
                        }
                    }
                    if (!first) {
                        checkLive(liveBean);
                    }
                }

                @Override
                public void onPageOutWindow(String liveUid) {
                    L.e(TAG, "onPageOutWindow----->" + liveUid);
                    if (TextUtils.isEmpty(mLiveUid) || mLiveUid.equals(liveUid)) {
                        LiveHttpUtil.cancel(LiveHttpConsts.CHECK_LIVE);
                        LiveHttpUtil.cancel(LiveHttpConsts.ENTER_ROOM);
                        LiveHttpUtil.cancel(LiveHttpConsts.ROOM_CHARGE);
                        clearRoomData();
                    }
                }
            });
            mRecyclerView.setAdapter(mRoomScrollAdapter);
        }
        mLiveType = intent.getIntExtra(Constants.LIVE_TYPE, Constants.LIVE_TYPE_NORMAL);
        mLiveTypeVal = intent.getIntExtra(Constants.LIVE_TYPE_VAL, 0);
        LiveBean liveBean = intent.getParcelableExtra(Constants.LIVE_BEAN);
        setLiveRoomData(liveBean);
        enterRoom();
    }


    private void setLiveRoomData(LiveBean liveBean) {
        mLiveBean = liveBean;
        mLiveUid = liveBean.getUid();
        mStream = liveBean.getStream();
        mLivePlayViewHolder.setCover(liveBean.getThumb());
        mLivePlayViewHolder.play(liveBean.getPull());
        mLiveAudienceViewHolder.setLiveInfo(mLiveUid, mStream);
        mLiveRoomViewHolder.setAvatar(liveBean.getAvatar());
        mLiveRoomViewHolder.setAnchorLevel(liveBean.getLevelAnchor());
        mLiveRoomViewHolder.setName(liveBean.getUserNiceName());
        mLiveRoomViewHolder.setRoomNum(liveBean.getLiangNameTip());
        mLiveRoomViewHolder.setTitle(liveBean.getTitle());
        mLiveLinkMicPkPresenter.setLiveUid(mLiveUid);
        mLiveLinkMicPresenter.setLiveUid(mLiveUid);

        if(liveBean.getIsshop()==1){
           mLiveAudienceViewHolder.setShopOpen(true);
        }else{
            mLiveAudienceViewHolder.setShopOpen(false);
        }
    }

    private void clearRoomData() {
        if (mSocketClient != null) {
            mSocketClient.disConnect();
        }
        mSocketClient = null;
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.stopPlay();
        }
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.clearData();
        }

        if (mLiveEndViewHolder != null) {
            mLiveEndViewHolder.removeFromParent();
        }
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.clearData();
        }
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.clearData();
        }
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.clearData();
        }
    }

    private void checkLive(LiveBean bean) {
        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new LiveRoomCheckLivePresenter(mContext, new LiveRoomCheckLivePresenter.ActionListener() {
                @Override
                public void onLiveRoomChanged(LiveBean liveBean, int liveType, int liveTypeVal, int liveSdk) {
                    if (liveBean == null) {
                        return;
                    }
                    setLiveRoomData(liveBean);
                    mLiveType = liveType;
                    mLiveTypeVal = liveTypeVal;
                    if (mRoomScrollAdapter != null) {
                        mRoomScrollAdapter.hideCover();
                    }
                    enterRoom();
                }
            });
        }
        mCheckLivePresenter.checkLive(bean);
    }


    private void enterRoom() {
        LiveHttpUtil.enterRoom(mLiveUid, mStream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    mDanmuPrice = obj.getString("barrage_fee");
                    mSocketUserType = obj.getIntValue("usertype");
                    mChatLevel = obj.getIntValue("speak_limit");
                    mDanMuLevel = obj.getIntValue("barrage_limit");
                    //连接socket
                    mSocketClient = new SocketClient(obj.getString("chatserver"), LiveAudienceActivity.this);
                    if (mLiveLinkMicPresenter != null) {
                        mLiveLinkMicPresenter.setSocketClient(mSocketClient);
                    }
                    mSocketClient.connect(mLiveUid, mStream);
                    if (mLiveRoomViewHolder != null) {
                        mLiveRoomViewHolder.setLiveInfo(mLiveUid, mStream, obj.getIntValue("userlist_time") * 1000);
                        mLiveRoomViewHolder.setVotes(obj.getString("votestotal"));
                        mLiveRoomViewHolder.setAttention(obj.getIntValue("isattention"));
                        List<LiveUserGiftBean> list = JSON.parseArray(obj.getString("userlists"), LiveUserGiftBean.class);
                        mLiveRoomViewHolder.setUserList(list);
                        mLiveRoomViewHolder.startRefreshUserList();
                        if (mLiveType == Constants.LIVE_TYPE_TIME) {//计时收费
                            mLiveRoomViewHolder.startRequestTimeCharge();
                        }
                    }
                    //判断是否有连麦，要显示连麦窗口
                    String linkMicUid = obj.getString("linkmic_uid");
                    String linkMicPull = obj.getString("linkmic_pull");
                    if (!TextUtils.isEmpty(linkMicUid) && !"0".equals(linkMicUid) && !TextUtils.isEmpty(linkMicPull)) {
                        if (mLiveSDK != Constants.LIVE_SDK_TX && mLiveLinkMicPresenter != null) {
                            mLiveLinkMicPresenter.onLinkMicPlay(linkMicUid, linkMicPull);
                        }
                    }
                    //判断是否有主播连麦
                    JSONObject pkInfo = JSON.parseObject(obj.getString("pkinfo"));
                    if (pkInfo != null) {
                        String pkUid = pkInfo.getString("pkuid");
                        if (!TextUtils.isEmpty(pkUid) && !"0".equals(pkUid)) {
                            if (mLiveSDK != Constants.LIVE_SDK_TX) {
                                String pkPull = pkInfo.getString("pkpull");
                                if (!TextUtils.isEmpty(pkPull) && mLiveLinkMicAnchorPresenter != null) {
                                    mLiveLinkMicAnchorPresenter.onLinkMicAnchorPlayUrl(pkUid, pkPull);
                                }
                            } else {
                                if (mLivePlayViewHolder instanceof LivePlayTxViewHolder) {
                                    ((LivePlayTxViewHolder) mLivePlayViewHolder).setAnchorLinkMic(true, 0);
                                }
                            }
                        }
                        if (pkInfo.getIntValue("ifpk") == 1 && mLiveLinkMicPkPresenter != null) {//pk开始了
                            mLiveLinkMicPkPresenter.onEnterRoomPkStart(pkUid, pkInfo.getLongValue("pk_gift_liveuid"), pkInfo.getLongValue("pk_gift_pkuid"), pkInfo.getIntValue("pk_time"));
                        }
                    }

                    //守护相关
                    mLiveGuardInfo = new LiveGuardInfo();
                    int guardNum = obj.getIntValue("guard_nums");
                    mLiveGuardInfo.setGuardNum(guardNum);
                    JSONObject guardObj = obj.getJSONObject("guard");
                    if (guardObj != null) {
                        mLiveGuardInfo.setMyGuardType(guardObj.getIntValue("type"));
                        mLiveGuardInfo.setMyGuardEndTime(guardObj.getString("endtime"));
                    }
                    if (mLiveRoomViewHolder != null) {
                        mLiveRoomViewHolder.setGuardNum(guardNum);
                        //红包相关
                        mLiveRoomViewHolder.setRedPackBtnVisible(obj.getIntValue("isred") == 1);
                    }
                    //奖池等级
                    int giftPrizePoolLevel = obj.getIntValue("jackpot_level");
                    if (giftPrizePoolLevel >= 0) {
                        if (mLiveRoomViewHolder != null) {
                            mLiveRoomViewHolder.showPrizePoolLevel(String.valueOf(giftPrizePoolLevel));
                        }
                    }

                    //游戏相关
                    if (CommonAppConfig.GAME_ENABLE && mLiveRoomViewHolder != null) {

                    }
                }
            }
        });
    }

    /**
     * 打开礼物窗口
     */
    public void openGiftWindow() {
        if (TextUtils.isEmpty(mLiveUid) || TextUtils.isEmpty(mStream)) {
            return;
        }
        LiveGiftDialogFragment fragment = new LiveGiftDialogFragment();
        fragment.setLifeCycleListener(this);
        fragment.setLiveGuardInfo(mLiveGuardInfo);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_UID, mLiveUid);
        bundle.putString(Constants.LIVE_STREAM, mStream);
        fragment.setArguments(bundle);
        fragment.show(((LiveAudienceActivity) mContext).getSupportFragmentManager(), "LiveGiftDialogFragment");
    }

    /**
     * 结束观看
     */
    private void endPlay() {
        if (mEnd) {
            return;
        }
        mEnd = true;
        //断开socket
        if (mSocketClient != null) {
            mSocketClient.disConnect();
        }
        mSocketClient = null;
        //结束播放
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.release();
        }
        mLivePlayViewHolder = null;
        release();
    }

    @Override
    protected void release() {
        if (mPayPresenter != null) {
            mPayPresenter.release();
        }
        mPayPresenter = null;
        LiveHttpUtil.cancel(LiveHttpConsts.CHECK_LIVE);
        LiveHttpUtil.cancel(LiveHttpConsts.ENTER_ROOM);
        LiveHttpUtil.cancel(LiveHttpConsts.ROOM_CHARGE);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_BALANCE);
        super.release();
        if (mRoomScrollAdapter != null) {
            mRoomScrollAdapter.setActionListener(null);
        }
        mRoomScrollAdapter = null;
    }

    /**
     * 观众收到直播结束消息
     */
    @Override
    public void onLiveEnd() {
        super.onLiveEnd();
        if (!CommonAppConfig.LIVE_ROOM_SCROLL) {
            if (mViewPager != null) {
                if (mViewPager.getCurrentItem() != 1) {
                    mViewPager.setCurrentItem(1, false);
                }
                mViewPager.setCanScroll(false);
            }
            endPlay();
        } else {
            if (mLivePlayViewHolder != null) {
                mLivePlayViewHolder.stopPlay2();
            }
        }
        if (mLiveEndViewHolder == null) {
            mLiveEndViewHolder = new LiveEndViewHolder(mContext, mSecondPage);
            mLiveEndViewHolder.subscribeActivityLifeCycle();
            mLiveEndViewHolder.addToParent();
        }
        mLiveEndViewHolder.showData(mLiveBean, mStream);
    }


    /**
     * 观众收到踢人消息
     */
    @Override
    public void onKick(String touid) {
        if (!TextUtils.isEmpty(touid) && touid.equals(CommonAppConfig.getInstance().getUid())) {//被踢的是自己
            exitLiveRoom();
            ToastUtil.show(WordUtil.getString(R.string.live_kicked_2));
        }
    }

    /**
     * 观众收到禁言消息
     */
    @Override
    public void onShutUp(String touid, String content) {
        if (!TextUtils.isEmpty(touid) && touid.equals(CommonAppConfig.getInstance().getUid())) {
            DialogUitl.showSimpleTipDialog(mContext, content);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mEnd && !canBackPressed()) {
            return;
        }
        exitLiveRoom();
    }

    /**
     * 退出直播间
     */
    public void exitLiveRoom() {
        endPlay();
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        if (mLiveAudienceViewHolder != null) {
            mLiveAudienceViewHolder.clearAnim();
        }
        super.onDestroy();
        L.e("LiveAudienceActivity-------onDestroy------->");
    }

    /**
     * 点亮
     */
    public void light() {
        if (!mLighted) {
            mLighted = true;
            int guardType = mLiveGuardInfo != null ? mLiveGuardInfo.getMyGuardType() : Constants.GUARD_TYPE_NONE;
            SocketChatUtil.sendLightMessage(mSocketClient, 1 + RandomUtil.nextInt(6), guardType);
        }
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.playLightAnim();
        }
    }


    /**
     * 计时收费更新主播映票数
     */
    public void roomChargeUpdateVotes() {
        sendUpdateVotesMessage(mLiveTypeVal);
    }

    /**
     * 暂停播放
     */
    public void pausePlay() {
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.pausePlay();
        }
    }

    /**
     * 恢复播放
     */
    public void resumePlay() {
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.resumePlay();
        }
    }

    /**
     * 充值成功
     */
    public void onChargeSuccess() {
        if (mLiveType == Constants.LIVE_TYPE_TIME) {
            if (mCoinNotEnough) {
                mCoinNotEnough = false;
                LiveHttpUtil.roomCharge(mLiveUid, mStream, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            roomChargeUpdateVotes();
                            if (mLiveRoomViewHolder != null) {
                                resumePlay();
                                mLiveRoomViewHolder.startRequestTimeCharge();
                            }
                        } else {
                            if (code == 1008) {//余额不足
                                mCoinNotEnough = true;
                                DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.live_coin_not_enough), false,
                                        new DialogUitl.SimpleCallback2() {
                                            @Override
                                            public void onConfirmClick(Dialog dialog, String content) {
                                                RouteUtil.forwardMyCoin(mContext);
                                            }

                                            @Override
                                            public void onCancelClick() {
                                                exitLiveRoom();
                                            }
                                        });
                            }
                        }
                    }
                });
            }
        }
    }

    public void setCoinNotEnough(boolean coinNotEnough) {
        mCoinNotEnough = coinNotEnough;
    }



    /**
     * 腾讯sdk连麦时候切换低延时流
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLinkMicTxAccEvent(LinkMicTxAccEvent e) {
        if (mLivePlayViewHolder != null && mLivePlayViewHolder instanceof LivePlayTxViewHolder) {
            ((LivePlayTxViewHolder) mLivePlayViewHolder).onLinkMicTxAccEvent(e.isLinkMic());
        }
    }

    /**
     * 腾讯sdk时候主播连麦回调
     *
     * @param linkMic true开始连麦 false断开连麦
     */
    public void onLinkMicTxAnchor(boolean linkMic) {
        if (mLivePlayViewHolder != null && mLivePlayViewHolder instanceof LivePlayTxViewHolder) {
            ((LivePlayTxViewHolder) mLivePlayViewHolder).setAnchorLinkMic(linkMic, 5000);
        }
    }


    /**
     * 打开充值窗口
     */
    public void openChargeWindow() {
        if (mPayPresenter == null) {
            mPayPresenter = new PayPresenter(this);
            mPayPresenter.setServiceNameAli(Constants.PAY_BUY_COIN_ALI);
            mPayPresenter.setServiceNameWx(Constants.PAY_BUY_COIN_WX);
            mPayPresenter.setAliCallbackUrl(HtmlConfig.ALI_PAY_COIN_URL);
            mPayPresenter.setPayCallback(new PayCallback() {
                @Override
                public void onSuccess() {
                    if (mPayPresenter != null) {
                        mPayPresenter.checkPayResult();
                    }
                }

                @Override
                public void onFailed() {

                }
            });
        }
        LiveChargeDialogFragment fragment = new LiveChargeDialogFragment();
        fragment.setLifeCycleListener(this);
        fragment.setPayPresenter(mPayPresenter);
        fragment.show(getSupportFragmentManager(), "ChatChargeDialogFragment");
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveRoomChangeEvent(LiveRoomChangeEvent e) {
        LiveBean liveBean = e.getLiveBean();
        if (liveBean != null) {
            LiveHttpUtil.cancel(LiveHttpConsts.CHECK_LIVE);
            LiveHttpUtil.cancel(LiveHttpConsts.ENTER_ROOM);
            LiveHttpUtil.cancel(LiveHttpConsts.ROOM_CHARGE);
            clearRoomData();

            setLiveRoomData(liveBean);
            mLiveType = e.getLiveType();
            mLiveTypeVal = e.getLiveTypeVal();
            enterRoom();
        }
    }

    /**
     * 打开商品窗口
     */
    public void openGoodsWindow() {
        LiveGoodsDialogFragment fragment = new LiveGoodsDialogFragment();
        fragment.setLifeCycleListener(this);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_UID, mLiveUid);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveGoodsDialogFragment");
    }

}
