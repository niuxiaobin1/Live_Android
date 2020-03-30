package com.yunbao.live.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.GoodsBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.live.R;

/**
 * Created by cxf on 2019/8/29.
 */

public class LiveShopAdapter extends RefreshAdapter<GoodsBean> {

    private View.OnClickListener mOnClickListener;


    public LiveShopAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer tag = (Integer) v.getTag();
                if (tag != null && mOnChildClickListner != null) {
                    mOnChildClickListner.onItemClick(mList.get(tag), tag,v);
                }
            }
        };

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_live_shop, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position),position);
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mThumb;
        TextView mDes;
        TextView mPrice;
        TextView mPriceOrigin;
        View mBtnBuy;

        public Vh(View itemView) {
            super(itemView);
            mThumb = itemView.findViewById(R.id.thumb);
            mDes = itemView.findViewById(R.id.des);
            mPrice = itemView.findViewById(R.id.price);
            mPriceOrigin = itemView.findViewById(R.id.price_origin);
            mBtnBuy = itemView.findViewById(R.id.btn_buy);
            mBtnBuy.setOnClickListener(mOnClickListener);
        }

        void setData(GoodsBean bean, int position) {
            mBtnBuy.setTag(position);
            ImgLoader.display(mContext,bean.getThumb(),mThumb);
            mPrice.setText(bean.getHaveUnitPrice());
            mPriceOrigin.setText(bean.getHaveUnitmOriginPrice());
            mPriceOrigin.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);
            mDes.setText(bean.getName());
        }
    }


}
