package com.yunbao.main.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.ViewPagerAdapter;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.event.FollowEvent;
import com.yunbao.common.event.UpdateFieldEvent;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.JsonUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.activity.ChatRoomActivity;
import com.yunbao.live.activity.LiveAudienceActivity;
import com.yunbao.live.bean.LiveBean;
import com.yunbao.live.bean.SearchUserBean;
import com.yunbao.live.dialog.LiveShareDialogFragment;
import com.yunbao.live.event.LiveRoomChangeEvent;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.live.presenter.LiveRoomCheckLivePresenter;
import com.yunbao.live.presenter.UserHomeSharePresenter;
import com.yunbao.live.views.AbsLivePageViewHolder;
import com.yunbao.live.views.AbsUserHomeViewHolder;
import com.yunbao.live.views.LiveRecordViewHolder;
import com.yunbao.main.R;
import com.yunbao.main.activity.EditProfileActivity;
import com.yunbao.main.activity.FansActivity;
import com.yunbao.main.activity.FollowActivity;
import com.yunbao.main.activity.ShopActivity;
import com.yunbao.main.activity.UserHomeActivity;
import com.yunbao.main.bean.StoreBean;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.main.presenter.CheckLivePresenter;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by cxf on 2018/10/18.
 * 用户个人主页
 */

public class UserHomeViewHolder2 extends AbsLivePageViewHolder implements LiveShareDialogFragment.ActionListener {

    private static final int PAGE_COUNT = 3;
    private UserHomeDetailViewHolder mDetailViewHolder;
    private VideoHomeViewHolder mVideoHomeViewHolder;
    private LiveRecordViewHolder mLiveRecordViewHolder;
    private AbsUserHomeViewHolder[] mViewHolders;
    private List<FrameLayout> mViewList;
    private ImageView mAvatarBg;
    private ImageView mAvatar;
    private TextView mName;
    private ImageView mSex;
    private ImageView mLevelAnchor;
    private ImageView mLevel;
    private TextView mID;
    private View mBtnLive;
    private TextView mBtnFans;
    private TextView mBtnFollow;
    private TextView mFollowText;
    private ImageView mFollowImage;
    private TextView mBlackText;
    private Drawable mFollowDrawable;
    private Drawable mUnFollowDrawable;
    private ViewPager mViewPager;
    private MagicIndicator mIndicator;
    private TextView mVideoCountTextView;
    private TextView mLiveCountTextView;


    private ViewGroup mVpStore;
    private TextView mTvStoreName;
    private TextView mTvStoreSale;
    private ImageView mImgStoreThumb;
    private View mVStoreLine;

    private String mToUid;
    private boolean mFromLiveRoom;
    private String mFromLiveUid;
    private boolean mSelf;
    private UserHomeSharePresenter mUserHomeSharePresenter;
    private SearchUserBean mSearchUserBean;
    private String mVideoString;
    private String mLiveString;
    private int mVideoCount;
    private boolean mIsUpdateField;
    private boolean mPaused;
    private LiveRoomCheckLivePresenter mCheckLivePresenter;


    public UserHomeViewHolder2(Context context, ViewGroup parentView, String toUid, boolean fromLiveRoom, String fromLiveUid) {
        super(context, parentView, toUid, fromLiveRoom, fromLiveUid);
    }

    @Override
    protected void processArguments(Object... args) {
        mToUid = (String) args[0];
        if (args.length > 1) {
            mFromLiveRoom = (boolean) args[1];
        }
        if (args.length > 2) {
            mFromLiveUid = (String) args[2];
        }
        if (!TextUtils.isEmpty(mToUid)) {
            mSelf = mToUid.equals(CommonAppConfig.getInstance().getUid());
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_user_home_2;
    }

    @Override
    public void init() {
        super.init();
        View bottom = findViewById(R.id.bottom);
        if (mSelf) {
            if (bottom.getVisibility() == View.VISIBLE) {
                bottom.setVisibility(View.GONE);
            }
            View btnEdit = findViewById(R.id.btn_edit);
            btnEdit.setVisibility(View.VISIBLE);
            btnEdit.setOnClickListener(this);
        } else {
            if (bottom.getVisibility() != View.VISIBLE) {
                bottom.setVisibility(View.VISIBLE);
            }
        }
        mAvatarBg = (ImageView) findViewById(R.id.bg_avatar);
        mAvatar = (ImageView) findViewById(R.id.avatar);
        mName = (TextView) findViewById(R.id.name);
        mSex = (ImageView) findViewById(R.id.sex);
        mLevelAnchor = (ImageView) findViewById(R.id.level_anchor);
        mLevel = (ImageView) findViewById(R.id.level);
        mID = (TextView) findViewById(R.id.id_val);
        mBtnFans = (TextView) findViewById(R.id.btn_fans);
        mBtnFollow = (TextView) findViewById(R.id.btn_follow);
        mBtnLive = findViewById(R.id.btn_live);
        mFollowText = (TextView) findViewById(R.id.follow_text);
        mFollowImage = (ImageView) findViewById(R.id.follow_img);
        mBlackText = (TextView) findViewById(R.id.black_text);

        mVpStore= findViewById(R.id.ll_store);
        mTvStoreName= findViewById(R.id.tv_store_name);
        mTvStoreSale= findViewById(R.id.tv_store_sale);
        mImgStoreThumb= findViewById(R.id.img_store_thumb);
        mVStoreLine=findViewById(R.id.v_store_line);
        mVpStore.setOnClickListener(this);

        mFollowDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_user_home_follow_1);
        mUnFollowDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_user_home_follow_0);


        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        if (PAGE_COUNT > 1) {
            mViewPager.setOffscreenPageLimit(PAGE_COUNT - 1);
        }
        mViewHolders = new AbsUserHomeViewHolder[PAGE_COUNT];
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

        mVideoString = WordUtil.getString(R.string.video);
        mLiveString = WordUtil.getString(R.string.live);
        mIndicator = (MagicIndicator) findViewById(R.id.indicator);
        final String[] titles = new String[]{WordUtil.getString(R.string.live_user_home_detail), mVideoString, mLiveString};
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
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, R.color.textColor));
                simplePagerTitleView.setText(titles[index]);
                simplePagerTitleView.setTextSize(14);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mViewPager != null) {
                            mViewPager.setCurrentItem(index);
                        }
                    }
                });
                if (index == 1) {
                    mVideoCountTextView = simplePagerTitleView;
                } else if (index == 2) {
                    mLiveCountTextView = simplePagerTitleView;
                }
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                linePagerIndicator.setLineWidth(DpUtil.dp2px(20));
                linePagerIndicator.setLineHeight(DpUtil.dp2px(2));
                linePagerIndicator.setRoundRadius(DpUtil.dp2px(1));
                linePagerIndicator.setColors(ContextCompat.getColor(mContext, R.color.global));
                return linePagerIndicator;
            }

        });
        mIndicator.setNavigator(commonNavigator);
        LinearLayout titleContainer = commonNavigator.getTitleContainer();
        titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        titleContainer.setDividerDrawable(new ColorDrawable() {
            @Override
            public int getIntrinsicWidth() {
                return DpUtil.dp2px(45);
            }
        });
        ViewPagerHelper.bind(mIndicator, mViewPager);

        mBtnFans.setOnClickListener(this);
        mBtnFollow.setOnClickListener(this);
        mBtnLive.setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_share).setOnClickListener(this);
        findViewById(R.id.btn_follow_2).setOnClickListener(this);
        findViewById(R.id.btn_black).setOnClickListener(this);
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean != null && configBean.getPriMsgSwitch() == 1) {
            findViewById(R.id.btn_pri_msg).setOnClickListener(this);
        } else {
            findViewById(R.id.btn_pri_msg).setVisibility(View.GONE);
        }
        mUserHomeSharePresenter = new UserHomeSharePresenter(mContext);
        EventBus.getDefault().register(this);
    }


    private void loadPageData(int position) {
        if (mViewHolders == null) {
            return;
        }
        AbsUserHomeViewHolder vh = mViewHolders[position];
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }
                if (position == 0) {
                    mDetailViewHolder = new UserHomeDetailViewHolder(mContext, parent, mToUid, mSelf);
                    vh = mDetailViewHolder;
                } else if (position == 1) {
                    mVideoHomeViewHolder = new VideoHomeViewHolder(mContext, parent, mToUid);
                    mVideoHomeViewHolder.setActionListener(new VideoHomeViewHolder.ActionListener() {
                        @Override
                        public void onVideoDelete(int deleteCount) {
                            mVideoCount -= deleteCount;
                            if (mVideoCount < 0) {
                                mVideoCount = 0;
                            }
                            if (mVideoCountTextView != null) {
                                mVideoCountTextView.setText(mVideoString + " " + mVideoCount);
                            }
                        }
                    });
                    vh = mVideoHomeViewHolder;
                } else if (position == 2) {
                    mLiveRecordViewHolder = new LiveRecordViewHolder(mContext, parent, mToUid);
                    mLiveRecordViewHolder.setActionListener(new LiveRecordViewHolder.ActionListener() {
                        @Override
                        public UserBean getUserBean() {
                            return mSearchUserBean;
                        }
                    });
                    vh = mLiveRecordViewHolder;
                }
                if (vh == null) {
                    return;
                }
                mViewHolders[position] = vh;
                vh.addToParent();
                vh.subscribeActivityLifeCycle();
            }
        }
        if (vh != null) {
            vh.loadData();
        }
    }

    @Override
    public void loadData() {
        if (TextUtils.isEmpty(mToUid)) {
            return;
        }
        loadPageData(0);
        MainHttpUtil.getUserHome(mToUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    try {
                        JSONObject obj = JSON.parseObject(info[0]);
                        SearchUserBean userBean = JSON.toJavaObject(obj, SearchUserBean.class);
                        mSearchUserBean = userBean;
                        String avatar = userBean.getAvatar();
                        ImgLoader.displayBlur(mContext, avatar, mAvatarBg);
                        ImgLoader.displayAvatar(mContext, avatar, mAvatar);
                        String toName = userBean.getUserNiceName();
                        mName.setText(toName);
                        mSex.setImageResource(CommonIconUtil.getSexIcon(userBean.getSex()));
                        CommonAppConfig appConfig = CommonAppConfig.getInstance();
                        LevelBean levelAnchor = appConfig.getAnchorLevel(userBean.getLevelAnchor());
                        ImgLoader.display(mContext, levelAnchor.getThumb(), mLevelAnchor);
                        LevelBean level = appConfig.getLevel(userBean.getLevel());
                        ImgLoader.display(mContext, level.getThumb(), mLevel);
                        mID.setText(userBean.getLiangNameTip());
                        String fansNum = StringUtil.toWan(userBean.getFans());
                        mBtnFans.setText(fansNum + " " + WordUtil.getString(R.string.fans));
                        mBtnFollow.setText(StringUtil.toWan(userBean.getFollows()) + " " + WordUtil.getString(R.string.follow));

                        if (obj.getIntValue("isattention") == 1) {
                            if (mFollowImage != null) {
                                mFollowImage.setImageDrawable(mFollowDrawable);
                            }
                            if (mFollowText != null) {
                                mFollowText.setText(R.string.following);
                            }
                        } else {
                            if (mFollowImage != null) {
                                mFollowImage.setImageDrawable(mUnFollowDrawable);
                            }
                            if (mFollowText != null) {
                                mFollowText.setText(R.string.follow);
                            }
                        }
                        if (mBlackText != null) {
                            mBlackText.setText(obj.getIntValue("isblack") == 1 ? R.string.black_ing : R.string.black);
                        }

                        mVideoCount = obj.getIntValue("videonums");
                        if (mVideoCountTextView != null) {
                            mVideoCountTextView.setText(mVideoString + " " + mVideoCount);
                        }
                        if (mLiveCountTextView != null) {
                            mLiveCountTextView.setText(mLiveString + " " + obj.getString("livenums"));
                        }
                        mUserHomeSharePresenter.setToUid(mToUid).setToName(toName).setAvatarThumb(userBean.getAvatarThumb()).setFansNum(fansNum);

                        if (mDetailViewHolder != null) {
                            mDetailViewHolder.showData(userBean, obj);
                        }
                        if (mBtnLive != null) {
                            if (obj.getIntValue("islive") == 1) {
                                if (mBtnLive.getVisibility() != View.VISIBLE) {
                                    mBtnLive.setVisibility(View.VISIBLE);
                                }
                            } else {
                                if (mBtnLive.getVisibility() == View.VISIBLE) {
                                    mBtnLive.setVisibility(View.INVISIBLE);
                                }
                            }
                        }

                        int isShop=obj.getIntValue("isshop");
                        if(isShop==1){
                            mVpStore.setVisibility(View.VISIBLE);
                            String storeStr=obj.getString("shop");
                            StoreBean storeBean= JsonUtil.getJsonToBean(storeStr,StoreBean.class);
                            mTvStoreName.setText(storeBean.getName());
                            mTvStoreSale.setText(WordUtil.getString(R.string.goods_tip_26)+storeBean.getNums());
                            ImgLoader.display(mContext,storeBean.getThumb(),mImgStoreThumb);
                            mVStoreLine.setVisibility(View.VISIBLE);
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }


    /**
     * 刷新印象
     */
    public void refreshImpress() {
        MainHttpUtil.getUserHome(mToUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    if (mDetailViewHolder != null) {
                        mDetailViewHolder.showImpress(obj.getString("label"));
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_back) {
            back();

        } else if (i == R.id.btn_share) {
            share();

        } else if (i == R.id.btn_fans) {
            forwardFans();

        } else if (i == R.id.btn_follow) {
            forwardFollow();

        } else if (i == R.id.btn_follow_2) {
            follow();

        } else if (i == R.id.btn_pri_msg) {
            forwardMsg();

        } else if (i == R.id.btn_black) {
            setBlack();

        } else if (i == R.id.btn_edit) {
            if (mContext != null) {
                mContext.startActivity(new Intent(mContext, EditProfileActivity.class));
            }
        } else if (i == R.id.btn_live) {
            forwardLiveRoom();
        }
        else if (i == R.id.ll_store) {
            forwardShopActivity();
        }
    }

    private void forwardShopActivity() {
        ShopActivity.forward(mContext,mToUid);
    }

    private void back() {
        if (mContext instanceof UserHomeActivity) {
            ((UserHomeActivity) mContext).onBackPressed();
        }
    }

    /**
     * 关注
     */
    private void follow() {
        CommonHttpUtil.setAttention(mToUid, null);
    }

    /**
     * 私信
     */
    private void forwardMsg() {
        if (mSearchUserBean != null) {
            ChatRoomActivity.forward(mContext, mSearchUserBean, mSearchUserBean.getAttention() == 1, true);
        }
    }

    private void onAttention(int isAttention) {
        if (isAttention == 1) {
            if (mFollowImage != null) {
                mFollowImage.setImageDrawable(mFollowDrawable);
            }
            if (mFollowText != null) {
                mFollowText.setText(R.string.following);
            }
        } else {
            if (mFollowImage != null) {
                mFollowImage.setImageDrawable(mUnFollowDrawable);
            }
            if (mFollowText != null) {
                mFollowText.setText(R.string.follow);
            }
        }

        if (mBlackText != null) {
            if (isAttention == 1) {
                mBlackText.setText(R.string.black);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {
        if (e.getToUid().equals(mToUid)) {
            int isAttention = e.getIsAttention();
            if (mSearchUserBean != null) {
                mSearchUserBean.setAttention(isAttention);
            }
            onAttention(isAttention);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateFieldEvent(UpdateFieldEvent e) {
        if (mSelf) {
            mIsUpdateField = true;
        }
    }


    /**
     * 前往TA的关注
     */
    private void forwardFollow() {
        FollowActivity.forward(mContext, mToUid);
    }

    /**
     * 前往TA的粉丝
     */
    private void forwardFans() {
        FansActivity.forward(mContext, mToUid);
    }

    /**
     * 拉黑，解除拉黑
     */
    private void setBlack() {
        MainHttpUtil.setBlack(mToUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    boolean isblack = JSON.parseObject(info[0]).getIntValue("isblack") == 1;
                    if (mBlackText != null) {
                        mBlackText.setText(isblack ? R.string.black_ing : R.string.black);
                    }
                    if (isblack) {
                        if (mFollowImage != null) {
                            mFollowImage.setImageDrawable(mUnFollowDrawable);
                        }
                        if (mFollowText != null) {
                            mFollowText.setText(R.string.follow);
                        }
                        EventBus.getDefault().post(new FollowEvent(mToUid, 0));
                    }
                }
            }
        });
    }

    /**
     * 分享
     */
    private void share() {
        LiveShareDialogFragment fragment = new LiveShareDialogFragment();
        fragment.setActionListener(this);
        fragment.show(((AbsActivity) mContext).getSupportFragmentManager(), "LiveShareDialogFragment");
    }


    @Override
    public void onItemClick(String type) {
        if (Constants.LINK.equals(type)) {
            copyLink();
        } else {
            shareHomePage(type);
        }
    }

    /**
     * 复制页面链接
     */
    private void copyLink() {
        if (mUserHomeSharePresenter != null) {
            mUserHomeSharePresenter.copyLink();
        }
    }


    /**
     * 分享页面链接
     */
    private void shareHomePage(String type) {
        if (mUserHomeSharePresenter != null) {
            mUserHomeSharePresenter.shareHomePage(type);
        }
    }

    /**
     * 跳转到直播间
     */
    private void forwardLiveRoom() {
        if (mFromLiveRoom && !TextUtils.isEmpty(mFromLiveUid) && mToUid.equals(mFromLiveUid)) {
            ((UserHomeActivity) mContext).onBackPressed();
            return;
        }
        if (mSearchUserBean == null) {
            return;
        }
        LiveHttpUtil.getLiveInfo(mSearchUserBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    LiveBean liveBean = JSON.parseObject(info[0], LiveBean.class);

                    if (mCheckLivePresenter == null) {
                        mCheckLivePresenter = new LiveRoomCheckLivePresenter(mContext, new LiveRoomCheckLivePresenter.ActionListener() {
                            @Override
                            public void onLiveRoomChanged(LiveBean liveBean, int liveType, int liveTypeVal, int liveSdk) {
                                if (liveBean == null) {
                                    return;
                                }
                                if (mFromLiveRoom) {
                                    ((UserHomeActivity) mContext).onBackPressed();
                                    EventBus.getDefault().post(new LiveRoomChangeEvent(liveBean, liveType, liveTypeVal));
                                } else {
                                    LiveAudienceActivity.forward(mContext, liveBean, liveType, liveTypeVal, "", 0, liveSdk);
                                }
                            }
                        });
                    }
                    mCheckLivePresenter.checkLive(liveBean);
                }
            }
        });
    }


    @Override
    public void release() {
        super.release();
        LiveHttpUtil.cancel(LiveHttpConsts.GET_LIVE_INFO);
        EventBus.getDefault().unregister(this);
        if (mUserHomeSharePresenter != null) {
            mUserHomeSharePresenter.release();
        }
        mUserHomeSharePresenter = null;
        if (mVideoHomeViewHolder != null) {
            mVideoHomeViewHolder.release();
        }
        mVideoHomeViewHolder = null;
        if (mCheckLivePresenter != null) {
            mCheckLivePresenter.cancel();
        }
        mCheckLivePresenter = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainHttpUtil.cancel(MainHttpConsts.GET_USER_HOME);
        CommonHttpUtil.cancel(CommonHttpConsts.SET_ATTENTION);
        MainHttpUtil.cancel(MainHttpConsts.SET_BLACK);
    }

    @Override
    public void onPause() {
        mPaused = true;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSelf && mPaused) {
            if (mIsUpdateField) {
                mIsUpdateField = false;
                refreshUserInfo();
            }
        }
        mPaused = false;
    }

    /**
     * 刷新用户信息
     */
    private void refreshUserInfo() {
        if (TextUtils.isEmpty(mToUid)) {
            return;
        }
        MainHttpUtil.getUserHome(mToUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    SearchUserBean userBean = JSON.toJavaObject(obj, SearchUserBean.class);
                    mSearchUserBean = userBean;
                    String avatar = userBean.getAvatar();
                    ImgLoader.displayBlur(mContext, avatar, mAvatarBg);
                    ImgLoader.displayAvatar(mContext, avatar, mAvatar);
                    String toName = userBean.getUserNiceName();
                    mName.setText(toName);
                    mSex.setImageResource(CommonIconUtil.getSexIcon(userBean.getSex()));
                    mID.setText(userBean.getLiangNameTip());
                    String fansNum = StringUtil.toWan(userBean.getFans());
                    mBtnFans.setText(fansNum + " " + WordUtil.getString(R.string.fans));
                    mBtnFollow.setText(StringUtil.toWan(userBean.getFollows()) + " " + WordUtil.getString(R.string.follow));
                    if (mDetailViewHolder != null) {
                        mDetailViewHolder.refreshData(userBean, obj);
                    }
                    if (mBtnLive != null) {
                        if (obj.getIntValue("islive") == 1) {
                            if (mBtnLive.getVisibility() != View.VISIBLE) {
                                mBtnLive.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (mBtnLive.getVisibility() == View.VISIBLE) {
                                mBtnLive.setVisibility(View.INVISIBLE);
                            }
                        }
                    }

                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

}
