package com.yunbao.video.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.yunbao.common.Constants;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.GoodsBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.video.R;
import java.util.List;

/**
 * Created by cxf on 2019/8/29.
 */

public class VideoGoodsAddAdapter extends RefreshAdapter<GoodsBean> {

    private View.OnClickListener mOnClickListener;
    private Drawable mCheckedDrawable;
    private Drawable mUnCheckedDrawable;
    private int mCheckedPosition = -1;

    public VideoGoodsAddAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                int position = (int) v.getTag();
                GoodsBean bean = mList.get(position);
                selGoodsBean(bean,position);
            }
        };
        mCheckedDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_check_1);
        mUnCheckedDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_check_0);
    }

    private void selGoodsBean(GoodsBean bean, int position) {
        if (mCheckedPosition == position) {
            bean.setAdded(false);
            notifyItemChanged(position, Constants.PAYLOAD);
            mCheckedPosition = -1;

            if(mOnItemClickListener!=null){
               mOnItemClickListener.onItemClick(null,position);
            }
        } else {
            if (mCheckedPosition >= 0 && mCheckedPosition < mList.size()) {
                mList.get(mCheckedPosition).setAdded(false);
                notifyItemChanged(mCheckedPosition, Constants.PAYLOAD);
            }
            bean.setAdded(true);
            notifyItemChanged(position, Constants.PAYLOAD);
            mCheckedPosition = position;
            if(mOnItemClickListener!=null){
                mOnItemClickListener.onItemClick(bean,position);
            }
        }
    }

    public GoodsBean getCheckedGoodsBean() {
        if (mCheckedPosition >= 0 && mCheckedPosition < mList.size()) {
            return mList.get(mCheckedPosition);
        }
        return null;
    }

    @Override
    public void refreshData(List<GoodsBean> list) {
        super.refreshData(list);
        mCheckedPosition = -1;
    }

    @Override
    public void clearData() {
        super.clearData();
        mCheckedPosition = -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_video_goods_add, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((Vh) vh).setData(mList.get(position), position, payload);
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mThumb;
        TextView mDes;
        TextView mPrice;
        TextView mPriceOrigin;
        ImageView mCheckImage;

        public Vh(View itemView) {
            super(itemView);
            mThumb = itemView.findViewById(R.id.thumb);
            mDes = itemView.findViewById(R.id.des);
            mPrice = itemView.findViewById(R.id.price);
            mPriceOrigin = itemView.findViewById(R.id.price_origin);
            mCheckImage = itemView.findViewById(R.id.img_check);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(GoodsBean bean, int position, Object payload) {
            if (payload == null) {
                itemView.setTag(position);
                ImgLoader.display(mContext,bean.getThumb(),mThumb);
                mPrice.setText(bean.getHaveUnitPrice());
                mPriceOrigin.setText(bean.getHaveUnitmOriginPrice());
                mPriceOrigin.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);
                mDes.setText(bean.getName());
            }
                mCheckImage.setImageDrawable(bean.isAdded() ? mCheckedDrawable : mUnCheckedDrawable);
        }
    }


    /*外部设置选中商品哦 ,需要统一数据源的状态*/
    public void setSelctGoodsBean(GoodsBean goodsBean){
        if(mList==null){
            return;
        }
        if(goodsBean!=null){
            int size=mList.size();
            for(int i=0;i<size;i++){
                GoodsBean tempGoodsBean=mList.get(i);
                if( tempGoodsBean.getId().equals(goodsBean.getId())){
                    selGoodsBean(tempGoodsBean,i);
                }
            }
        }else{
            if(mCheckedPosition!=-1){
                GoodsBean innerSelGoodsBean=mList.get(mCheckedPosition);
                innerSelGoodsBean.setAdded(true);
                notifyItemChanged(mCheckedPosition, Constants.PAYLOAD);
                mCheckedPosition = -1;
            }
        }

    }
}
