package com.yunbao.main.activity;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.HttpClient;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpConsts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LqadActivity extends AbsActivity {

    private LinearLayout actionLayout;
    private TextView actionTv;
    private TextView syTv;
    private TextView ljlqTv;
    private TextView ruleTv;
    private RecyclerView detailList;
    private List<Map<String, String>> list = new ArrayList<>();
    private MyAdapter myAdapter;
    private String perNum;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_lqad;
    }


    @Override
    protected void main() {
        super.main();
        actionLayout = findViewById(R.id.actionLayout);
        actionTv = findViewById(R.id.actionTv);
        syTv = findViewById(R.id.syTv);
        ljlqTv = findViewById(R.id.ljlqTv);
        ruleTv = findViewById(R.id.ruleTv);
        detailList = findViewById(R.id.detailList);
        initData();
        myAdapter = new MyAdapter();
        detailList.setLayoutManager(new LinearLayoutManager(this));
        detailList.setAdapter(myAdapter);

        actionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("领取".equals(actionTv.getText().toString().trim())) {
                    doLingqu();
                }
            }
        });
    }

    private void initData() {
        HttpClient.getInstance().get("User.GetAidouList", MainHttpConsts.GET_BASE_INFO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            syTv.setText("剩余" + obj.getString("shengyu_day") + "天");
                            ruleTv.setText("免费赠送" + obj.getString("free_aidou") + "个爱豆," +
                                    "每天可领取" + obj.getString("clear_aidou") + "个");
                            ljlqTv.setText(obj.getString("aidou_leiji"));
                            perNum = obj.getString("clear_aidou");
                            if ("0".equals(obj.getString("is_get"))) {
                                actionLayout.setSelected(true);
                                actionTv.setSelected(true);
                                actionTv.setText("领取");
                            } else {
                                actionLayout.setSelected(false);
                                actionTv.setSelected(false);
                                actionTv.setText("已领取");
                            }

                            JSONArray array = obj.getJSONArray("record");
                            list.clear();
                            for (int i = 0; i < array.size(); i++) {
                                Map<String, String> map = new HashMap<>();
                                JSONObject jsonObject = array.getJSONObject(i);
                                map.put("id", jsonObject.getString("id"));
                                map.put("aidou_num", jsonObject.getString("aidou_num"));
                                map.put("aidou_type", jsonObject.getString("aidou_type"));
                                map.put("create_time", jsonObject.getString("create_time"));
                                list.add(map);
                            }
                            myAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    private void doLingqu() {
        HttpClient.getInstance().get("User.DuliaoExecAidou", MainHttpConsts.GET_BASE_INFO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        ToastUtil.show(msg);
                        if (code == 0) {
                            actionLayout.setSelected(false);
                            actionTv.setSelected(false);
                            actionTv.setText("已领取");
                            float num;
                            try {
                                num = Float.parseFloat(ljlqTv.getText().toString().trim());
                            } catch (NumberFormatException e) {
                                num = 0f;
                            }
                            float num1;
                            try {
                                num1 = Float.parseFloat(perNum);
                            } catch (NumberFormatException e) {
                                num1 = 0f;
                            }

                            num += num1;
                            ljlqTv.setText(String.valueOf(num));

                        } else {
                            ToastUtil.show(msg);
                        }
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.aidou_list_item
                    , parent, false);
            return new MyHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            holder.numTv.setText("+" + list.get(position).get("aidou_num"));
            holder.timeTv.setText(list.get(position).get("create_time"));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class MyHolder extends RecyclerView.ViewHolder {

        private TextView timeTv;
        private TextView numTv;

        public MyHolder(View itemView) {
            super(itemView);
            numTv = itemView.findViewById(R.id.numTv);
            timeTv = itemView.findViewById(R.id.timeTv);
        }
    }
}
