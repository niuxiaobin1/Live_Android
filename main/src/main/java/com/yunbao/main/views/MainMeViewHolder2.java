package com.yunbao.main.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.bean.UserItemBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.HttpClient;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.im.activity.ChatActivity;
import com.yunbao.im.utils.ImMessageUtil;
import com.yunbao.im.utils.ImPushUtil;
import com.yunbao.live.activity.LiveRecordActivity;
import com.yunbao.live.activity.RoomManageActivity;
import com.yunbao.main.R;
import com.yunbao.main.activity.AdrwActivity;
import com.yunbao.main.activity.BindingRecommanderActivity;
import com.yunbao.main.activity.DaRenSqActivity;
import com.yunbao.main.activity.EditProfileActivity;
import com.yunbao.main.activity.FansActivity;
import com.yunbao.main.activity.FollowActivity;
import com.yunbao.main.activity.LeijiAidouActivity;
import com.yunbao.main.activity.LoginActivity;
import com.yunbao.main.activity.LqadActivity;
import com.yunbao.main.activity.MyDsttRedPacketActivity;
import com.yunbao.main.activity.MyProfitActivity;
import com.yunbao.main.activity.MyVideoActivity;
import com.yunbao.main.activity.MyadActivity;
import com.yunbao.main.activity.RealNameAuthActivity;
import com.yunbao.main.activity.RechargeActivity;
import com.yunbao.main.activity.SettingActivity;
import com.yunbao.main.activity.ShareActivity;
import com.yunbao.main.activity.ShopActivity;
import com.yunbao.main.activity.ThreeDistributActivity;
import com.yunbao.main.activity.WodeShouyiActivity;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * 我的
 */

public class MainMeViewHolder2 extends AbsMainViewHolder implements OnItemClickListener<UserItemBean>, View.OnClickListener {
    private MyGridView mGridView;
    private TextView mTtileView;
    private ImageView mAvatar;
    private TextView mName;
    private TextView mID;
    private boolean mPaused;
    private AppBarLayout mAppBarLayout;

    private TextView adTv;
    private TextView dsttTv;
    private TextView ljadTv;
    private TextView adczTv;
    private TextView codeTv;
    private TextView drsqTv;

    private List<MyTask> myTasks;
    private AuthenticationPopupWindow popupWindow;

    public MainMeViewHolder2(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_me2;
    }

    @Override
    public void init() {
        adTv = findViewById(R.id.adTv);
        dsttTv = findViewById(R.id.dsttTv);
        ljadTv = findViewById(R.id.ljadTv);
        mGridView = findViewById(R.id.mGridView);
        mTtileView = (TextView) findViewById(R.id.titleView);
        mAvatar = (ImageView) findViewById(R.id.avatar);
        mName = (TextView) findViewById(R.id.name);
        mID = (TextView) findViewById(R.id.id_val);
        codeTv = (TextView) findViewById(R.id.codeTv);
        drsqTv = (TextView) findViewById(R.id.drsqTv);


        mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float totalScrollRange = appBarLayout.getTotalScrollRange();
                float rate = -1 * verticalOffset / totalScrollRange * 2;
                if (rate >= 1) {
                    rate = 1;
                }
                if (rate > 0.5) {
                    mTtileView.setVisibility(View.VISIBLE);
                } else {
                    mTtileView.setVisibility(View.GONE);
                }
                if (mTtileView != null) {
                    mTtileView.setAlpha(rate);
                }
            }
        });

        findViewById(R.id.userProfile).setOnClickListener(this);
        findViewById(R.id.adTv).setOnClickListener(this);
        findViewById(R.id.ljadTv).setOnClickListener(this);
        findViewById(R.id.drsqTv).setOnClickListener(this);
        findViewById(R.id.logoutTv).setOnClickListener(this);
        findViewById(R.id.myFollowTv).setOnClickListener(this);
        findViewById(R.id.wyfxTv).setOnClickListener(this);
        findViewById(R.id.authTv).setOnClickListener(this);
        findViewById(R.id.lqadImage).setOnClickListener(this);
        findViewById(R.id.wdsyImage).setOnClickListener(this);

        MyTask myTask1 = new MyTask();
        myTask1.setIcon(R.mipmap.icon_wdsj);
        myTask1.setText("我的商家");
        myTask1.setId(1);

        MyTask myTask2 = new MyTask();
        myTask2.setIcon(R.mipmap.icon_shdz);
        myTask2.setText("收货地址");
        myTask2.setId(2);

        MyTask myTask3 = new MyTask();
        myTask3.setIcon(R.mipmap.icon_wddd);
        myTask3.setText("我的订单");
        myTask3.setId(3);

        MyTask myTask4 = new MyTask();
        myTask4.setIcon(R.mipmap.icon_wdlp);
        myTask4.setText("我的礼品");
        myTask4.setId(4);

        MyTask myTask5 = new MyTask();
        myTask5.setIcon(R.mipmap.icon_kfzx);
        myTask5.setText("客服中心");
        myTask5.setId(5);


        MyTask myTask8 = new MyTask();
        myTask8.setIcon(R.mipmap.icon_wlsp);
        myTask8.setText("网红商城");
        myTask8.setId(8);

        myTasks = new ArrayList<>();
        myTasks.add(myTask1);
        myTasks.add(myTask2);
        myTasks.add(myTask3);
        myTasks.add(myTask5);
        myTasks.add(myTask4);
        myTasks.add(myTask8);
        mGridView.setAdapter(new MyAdapter());
    }

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isShowed() && mPaused) {
            loadData();
        }
        mPaused = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainHttpUtil.cancel(MainHttpConsts.GET_BASE_INFO);
    }

    @Override
    public void loadData() {
        if (isFirstLoadData()) {
            CommonAppConfig appConfig = CommonAppConfig.getInstance();
            UserBean u = appConfig.getUserBean();
            List<UserItemBean> list = appConfig.getUserItemList();
            if (u != null && list != null) {
                showData(u, list);
            }
        }

        getUserInfo();
    }

    private void getUserInfo() {
        HttpClient.getInstance().get("User.NewUser", MainHttpConsts.GET_BASE_INFO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            UserBean bean = JSON.toJavaObject(obj, UserBean.class);
                            CommonAppConfig.getInstance().setUserBean(bean);
                            CommonAppConfig.getInstance().setUserItemList(obj.getString("list"));
                            SpUtil.getInstance().setStringValue(SpUtil.USER_INFO, info[0]);

                            List<UserItemBean> list = CommonAppConfig.getInstance().getUserItemList();
                            if (bean != null) {
                                showData(bean, list);
                            }
                        }
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    private CommonCallback<UserBean> mCallback = new CommonCallback<UserBean>() {
        @Override
        public void callback(UserBean bean) {
            List<UserItemBean> list = CommonAppConfig.getInstance().getUserItemList();
            if (bean != null) {
                showData(bean, list);
            }
        }
    };

    private void showData(UserBean u, List<UserItemBean> list) {
        ImgLoader.displayAvatar(mContext, u.getAvatar(), mAvatar);
        mTtileView.setText(u.getUserNiceName());
        mName.setText(u.getUserNiceName());
        mID.setText(u.getLiangNameTip());
        adTv.setText(u.getAidou() + "\n爱豆");
        dsttTv.setText(u.getDstt() + "\nDSTT");
        ljadTv.setText(u.getLeiji_aidou() + "\n累计爱豆");
        drsqTv.setText(u.getInvite_num() + "\n达人社群");
        codeTv.setText("邀请码：" + u.getInvite_code());

    }


    @Override
    public void onItemClick(UserItemBean bean, int position) {
        String url = bean.getHref();
        if (TextUtils.isEmpty(url)) {
            switch (bean.getId()) {
                case 1:
                    forwardProfit();
                    break;
                case 2:
                    forwardCoin();
                    break;
                case 13:
                    forwardSetting();
                    break;
                case 19:
                    forwardMyVideo();
                    break;
                case 20:
                    forwardRoomManage();
                    break;
                case 22:
                    forwardStore();
                    break;

            }
        } else {
            if (bean.getId() == 8) {//三级分销
                ThreeDistributActivity.forward(mContext, bean.getName(), url);
            } else {
                WebViewActivity.forward(mContext, url);
            }
        }
    }


    private void forwardStore() {
        ShopActivity.forward(mContext, CommonAppConfig.getInstance().getUid());

    }

    private void bindingRecommander() {
        mContext.startActivity(new Intent(mContext, BindingRecommanderActivity.class));
    }

    private void shareActivity() {
        mContext.startActivity(new Intent(mContext, ShareActivity.class));
    }
    private void userAuthActivity() {
        mContext.startActivity(new Intent(mContext, RealNameAuthActivity.class));
    }
    private void lqadActivity() {
        mContext.startActivity(new Intent(mContext, LqadActivity.class));
    }
    private void drsqActivity() {
        mContext.startActivity(new Intent(mContext, DaRenSqActivity.class));
    }
    private void wdsyActivity() {
        mContext.startActivity(new Intent(mContext, WodeShouyiActivity.class));
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.userProfile) {
            RouteUtil.forwardUserHome(mContext, CommonAppConfig.getInstance().getUid());
        } else if (i == R.id.myFollowTv) {
            forwardFollow();

        } else if (i == R.id.btn_fans) {
            forwardFans();

        } else if (i == R.id.btn_msg) {
            ChatActivity.forward(mContext);
        } else if (i == R.id.btn_wallet) {
            RouteUtil.forwardMyCoin(mContext);
        } else if (i == R.id.btn_detail) {
            WebViewActivity.forward(mContext, HtmlConfig.DETAIL);
        } else if (i == R.id.btn_shop) {
            WebViewActivity.forward(mContext, HtmlConfig.SHOP);
        } else if (i == R.id.adTv) {
            mContext.startActivity(new Intent(mContext, MyadActivity.class));
        } else if (i == R.id.ljadTv) {
            mContext.startActivity(new Intent(mContext, LeijiAidouActivity.class));
        } else if (i == R.id.wyfxTv) {
            shareActivity();
        } else if (i == R.id.logoutTv) {
            logOut();
        } else if (i == R.id.authTv) {
//            checkBindingStatus();
//            showPopup();
            userAuthActivity();
        }else if(i==R.id.lqadImage){
            lqadActivity();
        }else if(i==R.id.drsqTv){
            drsqActivity();
        }else if(i==R.id.wdsyImage){
            wdsyActivity();
        }
    }

    private void logOut() {
        CommonAppConfig.getInstance().clearLoginInfo();
        //退出极光
        ImMessageUtil.getInstance().logoutImClient();
        ImPushUtil.getInstance().logout();
        //友盟统计登出
        MobclickAgent.onProfileSignOff();
        LoginActivity.forward();
    }

    private void showPopup(){
        if (popupWindow!=null){
            popupWindow=null;
        }
        popupWindow=new AuthenticationPopupWindow(mContext);
        popupWindow.showPopupWindow();
    }

    private void checkBindingStatus() {
        HttpClient.getInstance().get("User.CheckIsAgent", MainHttpConsts.GET_BASE_INFO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            if ("0".equals(info[0])) {
                                bindingRecommander();
                            } else {
                                ToastUtil.show("已经绑定过推荐人");
                            }
                        }
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    /**
     * 编辑个人资料
     */
    private void forwardEditProfile() {
        mContext.startActivity(new Intent(mContext, EditProfileActivity.class));
    }

    /**
     * 我的关注
     */
    private void forwardFollow() {
        FollowActivity.forward(mContext, CommonAppConfig.getInstance().getUid());
    }

    /**
     * 我的粉丝
     */
    private void forwardFans() {
        FansActivity.forward(mContext, CommonAppConfig.getInstance().getUid());
    }

    /**
     * 直播记录
     */
    private void forwardLiveRecord() {
        LiveRecordActivity.forward(mContext, CommonAppConfig.getInstance().getUserBean());
    }

    /**
     * 我的收益
     */
    private void forwardProfit() {
        mContext.startActivity(new Intent(mContext, MyProfitActivity.class));
    }

    /**
     * 我的钻石
     */
    private void forwardCoin() {
        RouteUtil.forwardMyCoin(mContext);
    }

    /**
     * 设置
     */
    private void forwardSetting() {
        mContext.startActivity(new Intent(mContext, SettingActivity.class));
    }

    /**
     * 我的视频
     */
    private void forwardMyVideo() {
        mContext.startActivity(new Intent(mContext, MyVideoActivity.class));
    }

    /**
     * 房间管理
     */
    private void forwardRoomManage() {
        mContext.startActivity(new Intent(mContext, RoomManageActivity.class));
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return myTasks.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            MyHolder holder;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.mine_grid_item, null);
                holder = new MyHolder();
                holder.textView = view.findViewById(R.id.textView);
                view.setTag(holder);
            } else {
                holder = (MyHolder) view.getTag();
            }
            holder.textView.setText(myTasks.get(i).getText());
            Drawable d = mContext.getDrawable(myTasks.get(i).getIcon());
            holder.textView.setCompoundDrawablesWithIntrinsicBounds(null,
                    d, null, null);
            return view;
        }

        class MyHolder {
            TextView textView;
        }
    }

    class MyTask {

        int icon;
        String text;
        int id;

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

}
