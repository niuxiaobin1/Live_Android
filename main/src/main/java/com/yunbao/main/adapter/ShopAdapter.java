package com.yunbao.main.adapter;

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
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;

/**
 * Created by cxf on 2019/8/30.
 */

public class ShopAdapter extends RefreshAdapter<GoodsBean> {

    private View.OnClickListener mOnClickListener;

    public ShopAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick((GoodsBean) tag, 0);
                }
            }
        };
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_shop, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position));
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mThumb;
        TextView mDes;
        TextView mPrice;
        TextView mPriceOrigin;
        TextView mTvStatus;

        public Vh(View itemView) {
            super(itemView);
            mThumb = itemView.findViewById(R.id.thumb);
            mDes = itemView.findViewById(R.id.des);
            mPrice = itemView.findViewById(R.id.price);
            mPriceOrigin = itemView.findViewById(R.id.price_origin);
            mTvStatus= itemView.findViewById(R.id.tv_status);
            mPriceOrigin.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(GoodsBean bean) {
            itemView.setTag(bean);
            ImgLoader.display(mContext,bean.getThumb(),mThumb);
            mPrice.setText(bean.getHaveUnitPrice());
            mPriceOrigin.setText(bean.getHaveUnitmOriginPrice());
            mDes.setText(bean.getName());
            int status=bean.getStatus();

            if(status==-2){
                mTvStatus.setText(WordUtil.getString(R.string.goods_tip_37));
                mTvStatus.setVisibility(View.VISIBLE);
            }else if(status==-1){
                mTvStatus.setText(WordUtil.getString(R.string.goods_tip_38));
                mTvStatus.setVisibility(View.VISIBLE);
            }else{
                mTvStatus.setVisibility(View.INVISIBLE);
            }
        }
    }
}
