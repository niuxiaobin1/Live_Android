package com.yunbao.main.views;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.live.bean.LiveBean;
import com.yunbao.live.utils.LiveStorge;
import com.yunbao.main.R;
import com.yunbao.main.activity.LiveVideoListActivity;
import com.yunbao.main.activity.Main2Activity;
import com.yunbao.main.activity.MainActivity;
import com.yunbao.main.adapter.MainNearNearAdapter;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * 附近 附近
 */

public class MainNearNearViewHolder extends AbsMainHomeChildViewHolder implements OnItemClickListener<LiveBean> {

    private CommonRefreshView mRefreshView;
    private MainNearNearAdapter mAdapter;


    public MainNearNearViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_near;
    }

    @Override
    public void init() {
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<LiveBean>() {
            @Override
            public RefreshAdapter<LiveBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainNearNearAdapter(mContext);
                    mAdapter.setOnItemClickListener(MainNearNearViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getNear(p, callback);
            }

            @Override
            public List<LiveBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), LiveBean.class);
            }

            @Override
            public void onRefreshSuccess(List<LiveBean> list, int count) {
                if (CommonAppConfig.LIVE_ROOM_SCROLL) {
                    LiveStorge.getInstance().put(Constants.LIVE_NEAR, list);
                }
            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<LiveBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
    }


    @Override
    public void loadData() {
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    @Override
    public void onItemClick(LiveBean bean, int position) {
        watchLive(bean, Constants.LIVE_NEAR, position);
    }


    @Override
    public void release() {
        MainHttpUtil.cancel(MainHttpConsts.GET_NEAR);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    @Override
    public void watchLive(LiveBean liveBean, String key, int position) {
        if (mContext instanceof LiveVideoListActivity){
            ((LiveVideoListActivity) mContext).watchLive(liveBean, key, position);
        }else if(mContext instanceof Main2Activity){
            ((Main2Activity) mContext).watchLive(liveBean, key, position);
        }else{
            ((MainActivity) mContext).watchLive(liveBean, key, position);
        }
    }
}
