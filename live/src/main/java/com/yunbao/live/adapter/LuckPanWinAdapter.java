package com.yunbao.live.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.live.bean.TurntableGiftBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.live.R;

import java.util.List;

/**
 * Created by cxf on 2019/8/27.
 */

public class LuckPanWinAdapter extends RecyclerView.Adapter<LuckPanWinAdapter.Vh> {

    private final Context mContext;
    private List<TurntableGiftBean> mList;
    private LayoutInflater mInflater;


    public LuckPanWinAdapter(Context context, List<TurntableGiftBean> list) {
        mInflater = LayoutInflater.from(context);
        this.mContext=context;
        mList = list;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_luck_pan_win, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull LuckPanWinAdapter.Vh vh, int position, @NonNull List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mImg;
        TextView mText;

        public Vh(View itemView) {
            super(itemView);
            mImg = itemView.findViewById(R.id.img);
            mText = itemView.findViewById(R.id.text);
        }

        public void setData(TurntableGiftBean turntableGiftBean, int position, Object payload) {
            if(payload==null){
                ImgLoader.display(mContext,turntableGiftBean.getThumb(),mImg);
                mText.setText(turntableGiftBean.getName()+"x"+turntableGiftBean.getNums());
            }

        }
    }
}
