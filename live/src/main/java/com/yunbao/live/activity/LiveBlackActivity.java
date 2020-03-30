package com.yunbao.live.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.R;
import com.yunbao.live.adapter.LiveBlackAdapter;
import com.yunbao.live.bean.LiveShutUpBean;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/4/27.
 * 直播间拉黑用户列表
 */

public class LiveBlackActivity extends AbsActivity implements OnItemClickListener<LiveShutUpBean> {


    public static void forward(Context context, String liveUid) {
        Intent intent = new Intent(context, LiveBlackActivity.class);
        intent.putExtra(Constants.LIVE_UID, liveUid);
        context.startActivity(intent);
    }

    private CommonRefreshView mRefreshView;
    private LiveBlackAdapter mAdapter;
    private String mLiveUid;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_shut_up;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.live_user_black_list));
        mLiveUid = getIntent().getStringExtra(Constants.LIVE_UID);
        if (TextUtils.isEmpty(mLiveUid)) {
            return;
        }
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_black);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<LiveShutUpBean>() {
            @Override
            public RefreshAdapter<LiveShutUpBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new LiveBlackAdapter(mContext);
                    mAdapter.setOnItemClickListener(LiveBlackActivity.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                LiveHttpUtil.getLiveBlackList(mLiveUid, p, callback);
            }

            @Override
            public List<LiveShutUpBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), LiveShutUpBean.class);
            }

            @Override
            public void onRefreshSuccess(List<LiveShutUpBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<LiveShutUpBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        mRefreshView.initData();
    }

    @Override
    public void onItemClick(final LiveShutUpBean bean, int position) {
        LiveHttpUtil.liveCancelBlack(mLiveUid, bean.getUid(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (mAdapter != null) {
                        mAdapter.removeItem(bean.getUid());
                    }
                }
                ToastUtil.show(msg);
            }
        });

    }

    @Override
    protected void onDestroy() {
        LiveHttpUtil.cancel(LiveHttpConsts.GET_LIVE_BLACK_LIST);
        LiveHttpUtil.cancel(LiveHttpConsts.LIVE_CANCEL_BLACK);
        super.onDestroy();
    }
}
