package com.yunbao.main.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.main.utils.MainIconUtil;

/**
 * Created by cxf on 2018/10/20.
 */

public class MyProfitActivity extends AbsActivity implements View.OnClickListener {

    private TextView mAllName;//总映票数TextView
    private TextView mAll;//总映票数
    private TextView mCanName;//可提取映票数TextView
    private TextView mCan;//可提取映票数
    private TextView mGetName;//输入要提取的映票数
    private TextView mMoney;
    private TextView mTip;//温馨提示
    private EditText mEdit;
    private int mRate;
    private long mMaxCanMoney;//可提取映票数
    private View mChooseTip;
    private View mAccountGroup;
    private ImageView mAccountIcon;
    private TextView mAccount;
    private String mAccountID;
    private String mVotesName;
    private View mBtnCash;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_profit;
    }

    @Override
    protected void main() {
        mAllName = (TextView) findViewById(R.id.all_name);
        mAll = (TextView) findViewById(R.id.all);
        mCanName = (TextView) findViewById(R.id.can_name);
        mCan = (TextView) findViewById(R.id.can);
        mGetName = (TextView) findViewById(R.id.get_name);
        mTip = (TextView) findViewById(R.id.tip);
        mMoney = (TextView) findViewById(R.id.money);
        mEdit = findViewById(R.id.edit);
        mEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    long i = Long.parseLong(s.toString());
                    if (i > mMaxCanMoney) {
                        i = mMaxCanMoney;
                        s = String.valueOf(mMaxCanMoney);
                        mEdit.setText(s);
                        mEdit.setSelection(s.length());
                    }
                    if (mRate != 0) {
                        mMoney.setText("￥" + (i / mRate));
                    }
                    mBtnCash.setEnabled(true);
                } else {
                    mMoney.setText("￥");
                    mBtnCash.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mVotesName = CommonAppConfig.getInstance().getVotesName();
        mAllName.setText(String.format(WordUtil.getString(R.string.profit_tip_1), mVotesName));
        mCanName.setText(String.format(WordUtil.getString(R.string.profit_tip_2), mVotesName));
        mGetName.setText(String.format(WordUtil.getString(R.string.profit_tip_3), mVotesName));
        mBtnCash = findViewById(R.id.btn_cash);
        mBtnCash.setOnClickListener(this);
        findViewById(R.id.btn_choose_account).setOnClickListener(this);
        findViewById(R.id.btn_cash_record).setOnClickListener(this);
        mChooseTip = findViewById(R.id.choose_tip);
        mAccountGroup = findViewById(R.id.account_group);
        mAccountIcon = findViewById(R.id.account_icon);
        mAccount = findViewById(R.id.account);
        loadData();
    }


    private void loadData() {
        MainHttpUtil.getProfit(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    try {
                        JSONObject obj = JSON.parseObject(info[0]);
                        mAll.setText(obj.getString("votestotal"));
                        mTip.setText(obj.getString("tips"));
                        String votes = obj.getString("votes");
                        mCan.setText(votes);
                        if (votes.contains(".")) {
                            votes = votes.substring(0, votes.indexOf('.'));
                        }
                        mMaxCanMoney = Long.parseLong(votes);
                        mRate = obj.getIntValue("cash_rate");
                    } catch (Exception e) {
                        L.e("提现接口错误------>" + e.getClass() + "------>" + e.getMessage());
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_cash) {
            cash();

        } else if (i == R.id.btn_choose_account) {
            chooseAccount();

        } else if (i == R.id.btn_cash_record) {
            cashRecord();

        }
    }

    /**
     * 提现记录
     */
    private void cashRecord() {
        WebViewActivity.forward(mContext, HtmlConfig.CASH_RECORD);
    }


    /**
     * 提现
     */
    private void cash() {
        String votes = mEdit.getText().toString().trim();
        if (TextUtils.isEmpty(votes)) {
            ToastUtil.show(String.format(WordUtil.getString(R.string.profit_coin_empty), mVotesName));
            return;
        }
        if (TextUtils.isEmpty(mAccountID)) {
            ToastUtil.show(R.string.profit_choose_account);
            return;
        }
        MainHttpUtil.doCash(votes, mAccountID, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                ToastUtil.show(msg);
            }
        });
    }

    /**
     * 选择账户
     */
    private void chooseAccount() {
        Intent intent = new Intent(mContext, CashActivity.class);
        intent.putExtra(Constants.CASH_ACCOUNT_ID, mAccountID);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getAccount();
    }

    private void getAccount() {
        String[] values = SpUtil.getInstance().getMultiStringValue(Constants.CASH_ACCOUNT_ID, Constants.CASH_ACCOUNT, Constants.CASH_ACCOUNT_TYPE);
        if (values != null && values.length == 3) {
            String accountId = values[0];
            String account = values[1];
            String type = values[2];
            if (!TextUtils.isEmpty(accountId) && !TextUtils.isEmpty(account) && !TextUtils.isEmpty(type)) {
                if (mChooseTip.getVisibility() == View.VISIBLE) {
                    mChooseTip.setVisibility(View.INVISIBLE);
                }
                if (mAccountGroup.getVisibility() != View.VISIBLE) {
                    mAccountGroup.setVisibility(View.VISIBLE);
                }
                mAccountID = accountId;
                mAccountIcon.setImageResource(MainIconUtil.getCashTypeIcon(Integer.parseInt(type)));
                mAccount.setText(account);
            } else {
                if (mAccountGroup.getVisibility() == View.VISIBLE) {
                    mAccountGroup.setVisibility(View.INVISIBLE);
                }
                if (mChooseTip.getVisibility() != View.VISIBLE) {
                    mChooseTip.setVisibility(View.VISIBLE);
                }
                mAccountID = null;
            }
        } else {
            if (mAccountGroup.getVisibility() == View.VISIBLE) {
                mAccountGroup.setVisibility(View.INVISIBLE);
            }
            if (mChooseTip.getVisibility() != View.VISIBLE) {
                mChooseTip.setVisibility(View.VISIBLE);
            }
            mAccountID = null;
        }
    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.DO_CASH);
        MainHttpUtil.cancel(MainHttpConsts.GET_PROFIT);
        super.onDestroy();
    }
}
