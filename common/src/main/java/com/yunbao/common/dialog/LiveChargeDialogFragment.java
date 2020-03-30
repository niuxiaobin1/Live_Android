package com.yunbao.common.dialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.CoinBean;
import com.yunbao.common.bean.CoinPayBean;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.pay.PayPresenter;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.R;
import com.yunbao.common.adapter.ChatChargeCoinAdapter;

import java.util.List;

/**
 * Created by cxf on 2019/4/22.
 */

public class LiveChargeDialogFragment extends AbsDialogFragment implements View.OnClickListener, OnItemClickListener<CoinBean>, LiveChargePayDialogFragment.ActionListener {

    private RecyclerView mRecyclerView;
    private TextView mBtnCharge;
    private List<CoinPayBean> mPayList;
    private ChatChargeCoinAdapter mAdapter;
    private CoinBean mCheckedCoinBean;
    private PayPresenter mPayPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_chat_charge;
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
        params.height = DpUtil.dp2px(310);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 20);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRecyclerView.addItemDecoration(decoration);
        mBtnCharge = (TextView) findViewById(R.id.btn_charge);
        mBtnCharge.setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        loadData();
    }

    private void loadData() {
        CommonHttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    if (mRecyclerView == null) {
                        return;
                    }
                    JSONObject obj = JSON.parseObject(info[0]);
                    List<CoinPayBean> paylist = JSON.parseArray(obj.getString("paylist"), CoinPayBean.class);
                    mPayList = paylist;
                    List<CoinBean> list = JSON.parseArray(obj.getString("rules"), CoinBean.class);
                    if (list != null && list.size() > 0) {
                        CoinBean bean = list.get(0);
                        bean.setChecked(true);
                        mAdapter = new ChatChargeCoinAdapter(mContext, list);
                        mAdapter.setOnItemClickListener(LiveChargeDialogFragment.this);
                        mRecyclerView.setAdapter(mAdapter);
                        showMoney(bean);
                    }
                    if (mPayPresenter != null) {
                        String coin = obj.getString("coin");
                        mPayPresenter.setBalanceValue(Long.parseLong(coin));
                        mPayPresenter.setAliPartner(obj.getString("aliapp_partner"));
                        mPayPresenter.setAliSellerId(obj.getString("aliapp_seller_id"));
                        mPayPresenter.setAliPrivateKey(obj.getString("aliapp_key_android"));
                        mPayPresenter.setWxAppID(obj.getString("wx_appid"));
                    }
                }
            }
        });
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

    @Override
    public void onItemClick(CoinBean bean, int position) {
        showMoney(bean);
    }

    private void showMoney(CoinBean bean) {
        mCheckedCoinBean = bean;
        if (mCheckedCoinBean != null && mBtnCharge != null) {
            mBtnCharge.setText(String.format(WordUtil.getString(R.string.chat_charge_tip), mCheckedCoinBean.getMoney()));
        }
    }

    private void charge() {
        if (mCheckedCoinBean == null || mPayList == null || mPayList.size() == 0) {
            return;
        }
        LiveChargePayDialogFragment fragment = new LiveChargePayDialogFragment();
        fragment.setCoinString(StringUtil.contact(mCheckedCoinBean.getCoin(), CommonAppConfig.getInstance().getCoinName()));
        fragment.setMoneyString(mCheckedCoinBean.getMoney());
        fragment.setPayList(mPayList);
        fragment.setActionListener(this);
        fragment.show(((AbsActivity) mContext).getSupportFragmentManager(), "ChatChargePayDialogFragment");
    }

    @Override
    public void onChargeClick(CoinPayBean coinPayBean) {
        if (mPayPresenter != null && mCheckedCoinBean != null) {
            String href = coinPayBean.getHref();
            if (TextUtils.isEmpty(href)) {
                String money = mCheckedCoinBean.getMoney();
                String goodsName = StringUtil.contact(mCheckedCoinBean.getCoin(), CommonAppConfig.getInstance().getCoinName());
                String orderParams = StringUtil.contact(
                        "&uid=", CommonAppConfig.getInstance().getUid(),
                        "&money=", money,
                        "&changeid=", mCheckedCoinBean.getId(),
                        "&coin=", mCheckedCoinBean.getCoin());
                mPayPresenter.pay(coinPayBean.getId(), money, goodsName, orderParams);
            } else {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(href));
                mContext.startActivity(intent);
            }
        }
        dismiss();
    }

    @Override
    public void onDestroy() {
        CommonHttpUtil.cancel(CommonHttpConsts.GET_BALANCE);
        mPayPresenter = null;
        super.onDestroy();
    }

    public void setPayPresenter(PayPresenter payPresenter) {
        mPayPresenter = payPresenter;
    }
}
