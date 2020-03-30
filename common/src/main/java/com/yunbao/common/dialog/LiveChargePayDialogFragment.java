package com.yunbao.common.dialog;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yunbao.common.bean.CoinPayBean;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.R;
import com.yunbao.common.adapter.ChatChargePayAdapter;

import java.util.List;

/**
 * Created by cxf on 2019/4/22.
 */

public class LiveChargePayDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private List<CoinPayBean> mPayList;
    private ChatChargePayAdapter mAdapter;
    private String mCoinString;
    private String mMoneyString;
    private ActionListener mActionListener;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_chat_charge_pay;
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
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(310);
        params.height = DpUtil.dp2px(330);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViewById(R.id.btn_charge).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        TextView coin = (TextView) findViewById(R.id.coin);
        TextView money = (TextView) findViewById(R.id.money);
        coin.setText(mCoinString);
        money.setText(mMoneyString);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        if (mPayList != null) {
            mAdapter = new ChatChargePayAdapter(mContext, mPayList);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_close) {
            dismiss();
        } else if (i == R.id.btn_charge) {
            charge();
        }
    }

    private void charge() {
        if (mAdapter != null && mActionListener != null) {
            mActionListener.onChargeClick(mAdapter.getCheckedPayBean());
            dismiss();
        }
    }


    public void setPayList(List<CoinPayBean> payList) {
        for (int i = 0, size = payList.size(); i < size; i++) {
            payList.get(i).setChecked(i == 0);
        }
        mPayList = payList;
    }

    public void setCoinString(String coinString) {
        mCoinString = coinString;
    }

    public void setMoneyString(String moneyString) {
        mMoneyString = moneyString;
    }

    @Override
    public void onDestroy() {
        mActionListener = null;
        super.onDestroy();
    }

    public interface ActionListener {
        void onChargeClick(CoinPayBean coinPayBean);
    }


    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }
}
