package com.yunbao.live.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.live.R;
import com.yunbao.live.bean.GuardRightBean;
import com.yunbao.live.bean.LuckPanBean;

import java.util.List;

/**
 * Created by cxf on 2019/8/27.
 */

public class LuckPanRecordAdapter extends RefreshAdapter<LuckPanBean> {

    public LuckPanRecordAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_luck_pan_record, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((LuckPanRecordAdapter.Vh) holder).setData(mList.get(position), position, payload);

    }
    /*public void onBindViewHolder(@NonNull Vh vh, int position, @NonNull List<LuckPanBean> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), position, payload);
    }*/

    class Vh extends RecyclerView.ViewHolder {

        TextView mNum;
        TextView mTime;
        ImageView mIcon;
        TextView mCount;

        public Vh(View itemView) {
            super(itemView);
            mNum=itemView.findViewById(R.id.num);
            mTime=itemView.findViewById(R.id.time);
            mIcon=itemView.findViewById(R.id.icon);
            mCount=itemView.findViewById(R.id.count);
        }

        void setData(LuckPanBean bean, int postion, Object payload) {
            if(payload==null){
                mNum.setText(postion+1+"");
                mTime.setText(bean.getAddtime());
                mCount.setText("x"+bean.getNums());
                ImgLoader.display(mContext,bean.getThumb(),mIcon);
            }
        }

    }
}
