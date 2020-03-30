package com.yunbao.live.dialog;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yunbao.common.Constants;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.GoodsBean;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.custom.RecyclerViewNoBugLinearLayoutManager;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.JsonUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveAnchorActivity;
import com.yunbao.live.adapter.LiveShopAdapter;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2019/8/29.
 */

public class LiveShopDialogFragment extends AbsDialogFragment implements RefreshAdapter.OnItemChildClickListner<GoodsBean>, View.OnClickListener {

    private CommonRefreshView mRefreshView;
    private LiveShopAdapter mAdapter;
    private TextView mTitle;
    private String mLiveUid;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_shop;
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
        params.height = DpUtil.dp2px(320);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mLiveUid = bundle.getString(Constants.LIVE_UID);
        }
        mTitle = (TextView) findViewById(R.id.title);
        findViewById(R.id.btn_add).setOnClickListener(this);
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_shop);
        mRefreshView.setLayoutManager(new RecyclerViewNoBugLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<GoodsBean>() {
            @Override
            public RefreshAdapter<GoodsBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new LiveShopAdapter(mContext);
                    mAdapter.setOnChildClickListner(LiveShopDialogFragment.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                LiveHttpUtil.getSale(p,mLiveUid,callback);
            }

            @Override
            public List<GoodsBean> processData(String[] info) {
                if(info!=null&&info.length>0){
                    String jsonStr=info[0];
                    String count= JsonUtil.getString(jsonStr,"nums");
                    mTitle.setText(WordUtil.getString(R.string.goods_tip_17)+count);
                    return JsonUtil.getJsonToList(JsonUtil.getString(jsonStr,"list"),GoodsBean.class);
                }else{
                    return new ArrayList<>(1);
                }
            }

            @Override
            public void onRefreshSuccess(List<GoodsBean> list, int listCount) {

            }
            @Override
            public void onRefreshFailure() {

            }
            @Override
            public void onLoadMoreSuccess(List<GoodsBean> loadItemList, int loadItemCount) {

            }
            @Override
            public void onLoadMoreFailure() {

            }
        });
        mRefreshView.initData();
    }

    @Override
    public void onDestroy() {
        mContext = null;
        super.onDestroy();
        LiveHttpUtil.cancel( LiveHttpConsts.GET_SALE);
        LiveHttpUtil.cancel( LiveHttpConsts.SET_SALE);
    }



    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_add) {
            if (mContext != null) {
                ((LiveAnchorActivity)mContext).forwardAddGoods();
            }
            dismiss();
        }
    }

    @Override
    public void onItemClick(GoodsBean bean, final int position,final  View view) {
        view.setEnabled(false);
        LiveHttpUtil.shopSetSale(bean.getId(), 0, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(code==0){
                    mAdapter.getList().remove(position);
                    mAdapter.notifyItemRemoved(position);
                    mTitle.setText(WordUtil.getString(R.string.goods_tip_17)+mAdapter.getItemCount());

                }
            }
                    @Override
                    public void onFinish() {
                        super.onFinish();
                        view.setEnabled(true);
                    }
                }

        );
    }
}
