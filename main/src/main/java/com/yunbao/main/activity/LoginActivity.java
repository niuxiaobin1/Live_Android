package com.yunbao.main.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.Constants;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.HttpClient;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.mob.LoginData;
import com.yunbao.common.mob.MobBean;
import com.yunbao.common.mob.MobCallback;
import com.yunbao.common.mob.MobLoginUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.MD5Util;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.ValidatePhoneUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.utils.ImPushUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.LoginTypeAdapter;
import com.yunbao.main.event.RegSuccessEvent;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by cxf on 2018/9/17.
 */

public class LoginActivity extends AbsActivity implements OnItemClickListener<MobBean> {

    private View mRoot;
    private EditText mEditPhone;
    private EditText mEditCode;
    private View loginTv;
    private RecyclerView mRecyclerView;
    private MobLoginUtil mLoginUtil;
    private boolean mFirstLogin;//是否是第一次登录
    private boolean mShowInvite;//显示邀请码弹窗
    private String mLoginType = Constants.MOB_PHONE;//登录方式
    private TextView getCodeTv;

    private Timer timer;
    private int countDownNum = 60;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void main() {
        mRoot = findViewById(R.id.root);
        mEditPhone = (EditText) findViewById(R.id.edit_phone);
        mEditCode = (EditText) findViewById(R.id.edit_code);
        getCodeTv = findViewById(R.id.getCodeTv);
        loginTv = findViewById(R.id.loginTv);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = mEditPhone.getText().toString();
                String pwd = mEditCode.getText().toString();
                loginTv.setEnabled(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mEditPhone.addTextChangedListener(textWatcher);
        mEditCode.addTextChangedListener(textWatcher);
        boolean otherLoginType = false;
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean != null) {
            List<MobBean> list = MobBean.getLoginTypeList(configBean.getLoginType());
            if (list != null && list.size() > 0) {
                mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                LoginTypeAdapter adapter = new LoginTypeAdapter(mContext, list);
                adapter.setOnItemClickListener(this);
                mRecyclerView.setAdapter(adapter);
                mLoginUtil = new MobLoginUtil();
                otherLoginType = true;
            }
        }
        if (!otherLoginType) {
            findViewById(R.id.other_login_tip).setVisibility(View.INVISIBLE);
        }

        getCodeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNum = mEditPhone.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNum)) {
                    mEditPhone.setError(WordUtil.getString(R.string.login_input_phone));
                    mEditPhone.requestFocus();
                    return;
                }
                if (!ValidatePhoneUtil.validateMobileNumber(phoneNum)) {
                    mEditPhone.setError(WordUtil.getString(R.string.login_phone_error));
                    mEditPhone.requestFocus();
                    return;
                }
                sendCode(phoneNum);
                getCodeTv.setEnabled(false);
            }
        });

        EventBus.getDefault().register(this);
    }


    public static void forward() {
        Intent intent = new Intent(CommonAppContext.sInstance, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        CommonAppContext.sInstance.startActivity(intent);
    }

    private void sendCode(String tel) {
        HttpClient.getInstance().get("Login.DuLiaoGetCode", MainHttpConsts.GET_BASE_INFO)
                .params("sign", MD5Util.getMD5("mobile=" + tel + "&cb7faa54f668457ab446a2198d3ca43b"))
                .params("mobile", tel)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            countDownNum = 60;
                            timer = new Timer();
                            timer.schedule(new CountDownTask(), 100, 1000);

                        } else {
                            ToastUtil.show(msg);
                            getCodeTv.setEnabled(true);
                        }

                    }

                    @Override
                    public void onError() {
                        getCodeTv.setEnabled(true);
                    }
                });
    }

    class CountDownTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (countDownNum <= 0) {
                        timer.cancel();
                        timer = null;
                        getCodeTv.setEnabled(true);
                        getCodeTv.setText("获取验证码");
                    } else {
                        getCodeTv.setText(countDownNum + "s");
                        countDownNum--;
                    }

                }
            });
        }
    }


    public void loginClick(View v) {
        int i = v.getId();
        if (i == R.id.loginTv) {
            login();

//        } else if (i == R.id.btn_register) {
//            register();
//
//        } else if (i == R.id.btn_forget_pwd) {
//            forgetPwd();
//
//        } else if (i == R.id.btn_tip) {
//            forwardTip();
        }
    }

    //注册
    private void register() {
        startActivity(new Intent(mContext, RegisterActivity.class));
    }

    //忘记密码
    private void forgetPwd() {
        startActivity(new Intent(mContext, FindPwdActivity.class));
    }


    //手机号密码登录
    private void login() {
        String phoneNum = mEditPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            mEditPhone.setError(WordUtil.getString(R.string.login_input_phone));
            mEditPhone.requestFocus();
            return;
        }
        if (!ValidatePhoneUtil.validateMobileNumber(phoneNum)) {
            mEditPhone.setError(WordUtil.getString(R.string.login_phone_error));
            mEditPhone.requestFocus();
            return;
        }
        String pwd = mEditCode.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            mEditCode.setError(WordUtil.getString(R.string.login_input_pwd));
            mEditCode.requestFocus();
            return;
        }
        mLoginType = Constants.MOB_PHONE;
//        MainHttpUtil.login(phoneNum, pwd, new HttpCallback() {
//            @Override
//            public void onSuccess(int code, String msg, String[] info) {
//                onLoginSuccess(code, msg, info);
//            }
//        });
        loginByCode(phoneNum, pwd);
    }

    private void loginByCode(String tel, String vCode) {
        HttpClient.getInstance().get("Login.DuLiaoLoginByCode", MainHttpConsts.LOGIN)
                .params("mobile", tel)
                .params("code", vCode)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        onLoginSuccess(code, msg, info);
                    }
                });
    }

    //登录即代表同意服务和隐私条款
    private void forwardTip() {
        WebViewActivity.forward(mContext, HtmlConfig.LOGIN_PRIVCAY);
    }

    //登录成功！
    private void onLoginSuccess(int code, String msg, String[] info) {
        if (code == 0 && info.length > 0) {
            JSONObject obj = JSON.parseObject(info[0]);
            String uid = obj.getString("id");
            String token = obj.getString("token");
            mFirstLogin = obj.getIntValue("isreg") == 1;
            mShowInvite = obj.getIntValue("isagent") == 1;
            CommonAppConfig.getInstance().setLoginInfo(uid, token, true);
            getBaseUserInfo();
            //友盟统计登录
            MobclickAgent.onProfileSignIn(mLoginType, uid);
        } else {
            ToastUtil.show(msg);
        }
    }

    /**
     * 获取用户信息
     */
    private void getBaseUserInfo() {
        MainHttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
//                if (mFirstLogin) {
//                    RecommendActivity.forward(mContext, mShowInvite);
//                } else {
                Main2Activity.forward(mContext, mShowInvite);
//                }
                finish();
            }
        });
    }

    /**
     * 三方登录
     */
    private void loginBuyThird(LoginData data) {
        mLoginType = data.getType();
        MainHttpUtil.loginByThird(data.getOpenID(), data.getNickName(), data.getAvatar(), data.getType(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                onLoginSuccess(code, msg, info);
            }
        });
    }

    @Override
    public void onItemClick(MobBean bean, int position) {
        if (mLoginUtil == null) {
            return;
        }
        final Dialog dialog = DialogUitl.loginAuthDialog(mContext);
        dialog.show();
        mLoginUtil.execute(bean.getType(), new MobCallback() {
            @Override
            public void onSuccess(Object data) {
                if (data != null) {
                    loginBuyThird((LoginData) data);
                }
            }

            @Override
            public void onError() {
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onFinish() {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegSuccessEvent(RegSuccessEvent e) {
        finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        MainHttpUtil.cancel(MainHttpConsts.LOGIN);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_QQ_LOGIN_UNION_ID);
        MainHttpUtil.cancel(MainHttpConsts.LOGIN_BY_THIRD);
        MainHttpUtil.cancel(MainHttpConsts.GET_BASE_INFO);
        if (mLoginUtil != null) {
            mLoginUtil.release();
        }
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
