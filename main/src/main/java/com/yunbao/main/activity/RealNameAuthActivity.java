package com.yunbao.main.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.HttpClient;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.ValidatePhoneUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpConsts;

public class RealNameAuthActivity extends AbsActivity {

    private TextView doneTv;
    private EditText codeEt;
    private EditText nameEt;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_real_name_auth;
    }

    @Override
    protected void main() {
        super.main();
        doneTv=findViewById(R.id.doneTv);
        codeEt=findViewById(R.id.codeEt);
        nameEt=findViewById(R.id.nameEt);

        doneTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEt.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    nameEt.setError(WordUtil.getString(R.string.login_input_name));
                    nameEt.requestFocus();
                    return;
                }
                String id = codeEt.getText().toString().trim();
                if (TextUtils.isEmpty(id)) {
                    codeEt.setError(WordUtil.getString(R.string.login_input_id));
                    codeEt.requestFocus();
                    return;
                }
                userAuth(name,id);
            }
        });


        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = codeEt.getText().toString();
                String pwd = nameEt.getText().toString();
                doneTv.setEnabled(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };


        nameEt.addTextChangedListener(textWatcher);
        codeEt.addTextChangedListener(textWatcher);
    }

    private void userAuth(String name,String id){
        HttpClient.getInstance().get("User.UserAuth", MainHttpConsts.GET_BASE_INFO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("real_name", name)
                .params("cer_num", id)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        ToastUtil.show(msg);
                        if (code == 0 ) {
                            finish();
                        }
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

}
