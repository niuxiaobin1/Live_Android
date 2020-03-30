package com.yunbao.main.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.bean.UserItemBean;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.HttpClient;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpConsts;

import java.util.List;

public class BindingRecommanderActivity extends AbsActivity {

    private EditText bindingEt;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_binding_recommander;
    }


    @Override
    protected void main() {
        super.main();
        bindingEt=findViewById(R.id.bindingEt);
    }

    public void sureBing(View v){
        String id=bindingEt.getText().toString().trim();
        if (TextUtils.isEmpty(id)){
            ToastUtil.show("输入不能为空");
            return;
        }

        HttpClient.getInstance().get("User.SetDistribut", MainHttpConsts.GET_BASE_INFO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("code",id)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code==0){
                            ToastUtil.show("设置成功");
                            finish();
                        }else{
                            ToastUtil.show(msg);
                        }

                    }

                    @Override
                    public void onError() {
                    }
                });

    }
}
