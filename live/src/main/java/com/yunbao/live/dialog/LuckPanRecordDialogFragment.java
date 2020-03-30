package com.yunbao.live.dialog;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.JsonUtil;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveActivity;
import com.yunbao.live.adapter.LuckPanRecordAdapter;
import com.yunbao.live.bean.LuckPanBean;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/8/27.
 * 中奖记录
 */

public class LuckPanRecordDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private CommonRefreshView mRefreshView;
    private LuckPanRecordAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_luck_pan_record;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(300);
        params.height = DpUtil.dp2px(320);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<LuckPanBean>() {
            @Override
            public RefreshAdapter<LuckPanBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new LuckPanRecordAdapter(mContext);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                LiveHttpUtil.getTurnRecord(p,callback);
            }
            @Override
            public List<LuckPanBean> processData(String[] info) {
               return JsonUtil.getJsonToList(Arrays.toString(info),LuckPanBean.class);
            }
            @Override
            public void onRefreshSuccess(List<LuckPanBean> list, int listCount) {

            }
            @Override
            public void onRefreshFailure() {

            }
            @Override
            public void onLoadMoreSuccess(List<LuckPanBean> loadItemList, int loadItemCount) {

            }
            @Override
            public void onLoadMoreFailure() {

            }
        });
        mRefreshView.initData();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LiveHttpUtil.cancel(LiveHttpConsts.GET_WIN);
    }

    private String getLiveUid() {
        if(mContext!=null&&mContext instanceof LiveActivity){
            return ((LiveActivity)mContext).getLiveUid();
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_confirm) {
            dismiss();
        }
    }
}
