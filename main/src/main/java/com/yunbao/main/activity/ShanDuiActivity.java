package com.yunbao.main.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.HttpClient;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.views.ShanduiPopupWindow;

public class ShanDuiActivity extends AbsActivity {

    private TextView act2Tv;
    private TextView act1Tv;
    private TextView num2Tv;
    private TextView num1Tv;

    private String biliaidou;
    private String bilidstt;

    private ShanduiPopupWindow shanduiPopupWindow;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_shan_dui;
    }

    @Override
    protected void main() {
        super.main();
        act2Tv = findViewById(R.id.act2Tv);
        act1Tv = findViewById(R.id.act1Tv);
        num2Tv = findViewById(R.id.num2Tv);
        num1Tv = findViewById(R.id.num1Tv);
        initData();
        initData1();

        act1Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup("1");
            }
        });
        act2Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup("2");
            }
        });

    }


    private void showPopup(final String type) {
        if (TextUtils.isEmpty(biliaidou) || TextUtils.isEmpty(bilidstt)) {
            return;
        }

        if (shanduiPopupWindow != null) {
            shanduiPopupWindow=null;
        }
        shanduiPopupWindow = new ShanduiPopupWindow(ShanDuiActivity.this
                , biliaidou, bilidstt, type);
        shanduiPopupWindow.setOnClickAction(new ShanduiPopupWindow.OnClickAction() {
            @Override
            public void onClick(String num) {
                ShanDui(type, num);
            }
        });
        shanduiPopupWindow.showPopupWindow();
    }

    private void initData() {
        HttpClient.getInstance().get("User.BiLi", MainHttpConsts.GET_BASE_INFO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            biliaidou = obj.getString("biliaidou");
                            bilidstt = obj.getString("bilidstt");
                        }
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    private void initData1() {
        HttpClient.getInstance().get("User.AidouDuihuan", MainHttpConsts.GET_BASE_INFO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            num1Tv.setText(obj.getString("coin"));
                            num2Tv.setText(obj.getString("dstt"));
                        }
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    private void ShanDui(String type, String num) {
        HttpClient.getInstance().get("User.AidouDstt", MainHttpConsts.GET_BASE_INFO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("type", type)
                .params("1".equals(type) ? "aidou" : "dstt", num)
                .params("biliaidou", biliaidou)
                .params("bilidstt", bilidstt)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        ToastUtil.show(msg);
                        if (code == 0 && info.length > 0) {
                            initData1();
                        }
                    }

                    @Override
                    public void onError() {
                    }
                });
    }


}
