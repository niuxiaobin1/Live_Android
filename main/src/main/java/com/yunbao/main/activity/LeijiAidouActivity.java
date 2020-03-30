package com.yunbao.main.activity;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.bean.UserItemBean;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.HttpClient;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpConsts;

import java.util.List;

public class LeijiAidouActivity extends AbsActivity {
    private TextView ljsyTv;
    private TextView dqsyTv;
    private RecyclerView recylerView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_leiji_aidou;
    }

    @Override
    protected void main() {
        super.main();
        ljsyTv=findViewById(R.id.ljsyTv);
        dqsyTv=findViewById(R.id.dqsyTv);
        recylerView=findViewById(R.id.recylerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();

    }

    public void backClick(View v) {
        finish();
    }


    private void getData() {
        HttpClient.getInstance().get("User.GetAidouList", MainHttpConsts.GET_BASE_INFO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            Log.e("nxb",info[0]);
//                            JSONObject obj = JSON.parseObject(info[0]);
//                            UserBean bean = JSON.toJavaObject(obj, UserBean.class);
//                            CommonAppConfig.getInstance().setUserBean(bean);
//                            CommonAppConfig.getInstance().setUserItemList(obj.getString("list"));
//                            SpUtil.getInstance().setStringValue(SpUtil.USER_INFO, info[0]);
//
//                            List<UserItemBean> list = CommonAppConfig.getInstance().getUserItemList();
//                            if (bean != null) {
//                                showData(bean, list);
//                            }
                        }
                    }

                    @Override
                    public void onError() {
                    }
                });
    }
}
