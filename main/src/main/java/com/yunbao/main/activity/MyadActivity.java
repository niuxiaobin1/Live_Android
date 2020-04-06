package com.yunbao.main.activity;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.HttpClient;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.views.ZhangdanPopupWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyadActivity extends AbsActivity {

    private RecyclerView zhangdanList;
    private TextView shouruTv;
    private TextView zhichuTv;
    private TextView chooseTypeTv;
    private List<Map<String,String>> list=new ArrayList<>();
    private MyAdapter myAdapter;
    private String way="0";
    private ZhangdanPopupWindow zhangdanPopupWindow;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_myad;
    }

    @Override
    protected void main() {
        super.main();
        zhangdanList=findViewById(R.id.zhangdanList);
        shouruTv=findViewById(R.id.shouruTv);
        zhichuTv=findViewById(R.id.zhichuTv);
        chooseTypeTv=findViewById(R.id.chooseTypeTv);
        myAdapter=new MyAdapter();
        zhangdanList.setLayoutManager(new LinearLayoutManager(this));
        zhangdanList.setAdapter(myAdapter);
        initData();

        chooseTypeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (zhangdanPopupWindow==null){
                    zhangdanPopupWindow=new ZhangdanPopupWindow(MyadActivity.this);
                }
                zhangdanPopupWindow.setOnSelectAction(new ZhangdanPopupWindow.OnSelectAction() {
                    @Override
                    public void onSelect(String type,String text) {
                        way=type;
                        chooseTypeTv.setText(text);
                        initData();
                    }
                });
                zhangdanPopupWindow.showPopupWindow();
            }
        });
    }


    private void initData(){
        HttpClient.getInstance().get("User.AidouOrder", MainHttpConsts.GET_BASE_INFO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("way", way)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            JSONArray array=obj.getJSONArray("record");
                            list.clear();
                            for (int i = 0; i <array.size() ; i++) {
                                Map<String,String> map=new HashMap<>();
                                JSONObject jsonObject=array.getJSONObject(i);
                                map.put("type",jsonObject.getString("type"));
                                map.put("totalcoin",jsonObject.getString("totalcoin"));
                                map.put("addtime",jsonObject.getString("addtime"));
                                map.put("action",jsonObject.getString("action"));
                                list.add(map);
                            }
                            zhichuTv.setText("支出："+obj.getString("zhichu"));
                            shouruTv.setText("收入："+obj.getString("shouru"));
                            myAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    class MyAdapter extends RecyclerView.Adapter<MyHolder>{

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.zhangdan_list_item
                    ,parent,false);
            return new MyHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            String ac="+";
            if ("income".equals(list.get(position).get("type"))){
                ac="+";
            }else{
                ac="-";
            }
            if ("chongzhi".equals(list.get(position).get("action"))){
                holder.avatar.setImageResource(R.mipmap.chongzhi_icon);
            }else if("yaoqing".equals(list.get(position).get("action"))){
                holder.avatar.setImageResource(R.mipmap.tuijian_icon);
            }else if("guanli".equals(list.get(position).get("action"))){
                holder.avatar.setImageResource(R.mipmap.guanli_icon);
            }else if("dashang".equals(list.get(position).get("action"))){
                holder.avatar.setImageResource(R.mipmap.dashang_icon);
            }else if("shifang".equals(list.get(position).get("action"))){
                holder.avatar.setImageResource(R.mipmap.shifang_icon);
            }else if("duihuan".equals(list.get(position).get("action"))){
                holder.avatar.setImageResource(R.mipmap.duihuan_icon);
            }
            holder.numTv.setText(ac+list.get(position).get("totalcoin"));
            holder.timeTv.setText(list.get(position).get("addtime"));
            holder.nameTv.setText(list.get(position).get("action"));

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class MyHolder extends RecyclerView.ViewHolder{

        private RoundedImageView avatar;
        private TextView nameTv;
        private TextView timeTv;
        private TextView numTv;
        public MyHolder(View itemView) {
            super(itemView);
            avatar=itemView.findViewById(R.id.avatar);
            nameTv=itemView.findViewById(R.id.nameTv);
            timeTv=itemView.findViewById(R.id.timeTv);
            numTv=itemView.findViewById(R.id.numTv);
        }
    }
}
