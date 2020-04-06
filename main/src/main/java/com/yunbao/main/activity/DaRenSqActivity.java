package com.yunbao.main.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.HttpClient;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpConsts;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DaRenSqActivity extends AbsActivity {

    private TextView numTv;
    private TextView personNumTv;
    private MagicIndicator magic_indicator;
    private RecyclerView darenList;
    private CommonNavigator commonNavigator;
    private List<Map<String,String>> list=new ArrayList<>();
    private String is_auth="1";

    private int currentSelect=0;
    private MyAdapter myAdapter;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_da_ren_sq;
    }

    @Override
    protected void main() {
        super.main();
        numTv=findViewById(R.id.numTv);
        magic_indicator=findViewById(R.id.magic_indicator);
        darenList=findViewById(R.id.darenList);
        personNumTv=findViewById(R.id.personNumTv);

        myAdapter=new MyAdapter();
        darenList.setLayoutManager(new LinearLayoutManager(this));
        darenList.setAdapter(myAdapter);


        commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setScrollPivotX(0.5f);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                String text="";
                if (index==0){
                        text="已认证";
                }else{
                    text="未认证";
                }
                simplePagerTitleView.setText(text);
                simplePagerTitleView.setTextSize(16);
                simplePagerTitleView.setNormalColor(Color.parseColor("#4E4E4E"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#B63AF3"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       if (index==0){
                           scroll2ToPosition1();
                           is_auth="1";
                           initData();
                       }else{
                           scroll1ToPosition2();
                           is_auth="0";
                           initData();
                       }
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                indicator.setLineHeight(UIUtil.dip2px(context, 1));
//                indicator.setLineWidth(UIUtil.dip2px(context, 10));
                indicator.setRoundRadius(UIUtil.dip2px(context, 2.5));
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                indicator.setYOffset(UIUtil.dip2px(context, 0));
                indicator.setXOffset(UIUtil.dip2px(context, 3));
                indicator.setColors(Color.parseColor("#B63AF3"));
                return indicator;
            }
        });
        magic_indicator.setNavigator(commonNavigator);

        initData();
    }



    private void initData(){
        HttpClient.getInstance().get("User.UserAuthList", MainHttpConsts.GET_BASE_INFO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("is_auth", is_auth)
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
                                map.put("avatar",jsonObject.getString("avatar"));
                                map.put("mobile",jsonObject.getString("mobile"));
                                map.put("user_nicename",jsonObject.getString("user_nicename"));
                                list.add(map);
                            }
                            personNumTv.setText("人数："+obj.getString("user_num"));
                            numTv.setText(obj.getString("user_count"));
                            myAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError() {
                    }
                });
    }



    private void scroll2ToPosition1() {
        if (currentSelect == 0) {
            return ;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                magic_indicator.onPageScrolled(0, (Float) valueAnimator.getAnimatedValue(), 1);
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                currentSelect = 0;
                magic_indicator.onPageSelected(currentSelect);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });


        animator.setDuration(200);
        animator.start();
    }


    private void scroll1ToPosition2() {
        if (currentSelect == 1) {
            return ;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                magic_indicator.onPageScrolled(0, (Float) valueAnimator.getAnimatedValue(), 1);
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                currentSelect = 1;
                magic_indicator.onPageSelected(currentSelect);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });


        animator.setDuration(200);
        animator.start();
    }



    class MyAdapter extends RecyclerView.Adapter<MyHolder>{

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.daren_list_item
                    ,parent,false);
            return new MyHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {

            ImgLoader.displayAvatar(mContext, list.get(position).get("avatar"), holder.avatar);
            holder.timeTv.setText(list.get(position).get("mobile"));
            holder.nameTv.setText(list.get(position).get("user_nicename"));

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
        public MyHolder(View itemView) {
            super(itemView);
            avatar=itemView.findViewById(R.id.avatar);
            nameTv=itemView.findViewById(R.id.nameTv);
            timeTv=itemView.findViewById(R.id.timeTv);
        }
    }

}
