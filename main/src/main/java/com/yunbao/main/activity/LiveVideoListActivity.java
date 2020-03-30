package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.widget.FrameLayout;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.live.bean.LiveBean;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.main.presenter.CheckLivePresenter;
import com.yunbao.main.views.MainHomeLiveViewHolder;

public class LiveVideoListActivity extends AbsActivity {

    private MainHomeLiveViewHolder mLiveViewHolder;
    private CheckLivePresenter mCheckLivePresenter;

    @Override
    protected void main() {
        super.main();
        mLiveViewHolder = new MainHomeLiveViewHolder(mContext, (FrameLayout)findViewById(R.id.parentView));
        mLiveViewHolder.addToParent();
        mLiveViewHolder.subscribeActivityLifeCycle();
        mLiveViewHolder.loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainHttpUtil.getHot(1, mRefreshCallback);
    }

    public static void forward(Context context) {
        context.startActivity(new Intent(context, LiveVideoListActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCheckLivePresenter != null) {
            mCheckLivePresenter.cancel();
        }
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

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_video_list;
    }

    private HttpCallback mRefreshCallback = new HttpCallback() {


        @Override
        public void onSuccess(int code, String msg, String[] info) {
        }


        @Override
        public void onError() {
        }

        @Override
        public void onFinish() {
        }
    };
}
