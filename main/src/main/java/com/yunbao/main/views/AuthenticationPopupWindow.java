package com.yunbao.main.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yunbao.common.Constants;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.HttpClient;
import com.yunbao.common.utils.MD5Util;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.ValidatePhoneUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.activity.LoginActivity;
import com.yunbao.main.http.MainHttpConsts;

import java.util.Timer;
import java.util.TimerTask;

import razerdp.basepopup.BasePopupWindow;

import static com.lzy.okgo.utils.HttpUtils.runOnUiThread;

public class AuthenticationPopupWindow extends BasePopupWindow implements View.OnClickListener {

    private TextView doneTv;
    private TextView getCodeTv;
    private EditText mEditCode;
    private EditText mEditPhone;


    private Timer timer;
    private int countDownNum=60;

    public AuthenticationPopupWindow(Context context) {
        super(context);
        doneTv = findViewById(R.id.doneTv);
        getCodeTv = findViewById(R.id.getCodeTv);
        mEditCode = findViewById(R.id.edit_code);
        mEditPhone = findViewById(R.id.edit_phone);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = mEditPhone.getText().toString();
                String pwd = mEditCode.getText().toString();
                doneTv.setEnabled(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        mEditPhone.addTextChangedListener(textWatcher);
        mEditCode.addTextChangedListener(textWatcher);


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

        doneTv.setOnClickListener(new View.OnClickListener() {
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
                String pwd = mEditCode.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    mEditCode.setError(WordUtil.getString(R.string.login_input_verifycode));
                    mEditCode.requestFocus();
                    return;
                }


            }
        });

    }

    @Override
    public void onClick(View view) {

    }


    private void sendCode(String tel){
        HttpClient.getInstance().get("Login.DuLiaoGetCode", MainHttpConsts.GET_BASE_INFO)
                .params("sign", MD5Util.getMD5("mobile="+tel+"&cb7faa54f668457ab446a2198d3ca43b"))
                .params("mobile",tel )
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code==0){
                            countDownNum=60;
                            timer=new Timer();
                            timer.schedule(new CountDownTask(),100,1000);

                        }else{
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


    class  CountDownTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (countDownNum<=0){
                        timer.cancel();
                        timer=null;
                        getCodeTv.setEnabled(true);
                        getCodeTv.setText("获取验证码");
                    }else{
                        getCodeTv.setText(countDownNum+"s");
                        countDownNum--;
                    }

                }
            });
        }
    }


    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.popup_authentication);
    }

}
