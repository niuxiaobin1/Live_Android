package com.yunbao.common.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.Constants;
import com.yunbao.common.bean.CoinPayBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.R;

import java.util.List;

/**
 * Created by cxf on 2019/4/22.
 */

public class ChatChargePayAdapter extends RecyclerView.Adapter<ChatChargePayAdapter.Vh> {

    private Context mContext;
    private List<CoinPayBean> mList;
    private LayoutInflater mInflater;
    private Drawable mCheckedDrawable;
    private int mCheckedPosition;
    private View.OnClickListener mOnClickListener;

    public ChatChargePayAdapter(Context context, List<CoinPayBean> list) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mList = list;
        mCheckedPosition = 0;
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                int positon = (int) tag;
                CoinPayBean bean = mList.get(positon);
                if (mCheckedPosition != positon) {
                    if (mCheckedPosition >= 0 && mCheckedPosition < mList.size()) {
                        mList.get(mCheckedPosition).setChecked(false);
                        notifyItemChanged(mCheckedPosition, Constants.PAYLOAD);
                    }
                    bean.setChecked(true);
                    notifyItemChanged(positon, Constants.PAYLOAD);
                    mCheckedPosition = positon;
                }
            }
        };
        mCheckedDrawable = ContextCompat.getDrawable(context, R.drawable.icon_chat_charge_pay_1);
    }

    public CoinPayBean getCheckedPayBean() {
        if (mList != null && mCheckedPosition >= 0 && mCheckedPosition < mList.size()) {
            return mList.get(mCheckedPosition);
        }
        return null;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_chat_charge_pay, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position, @NonNull List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mThumb;
        TextView mName;
        ImageView mImg;

        public Vh(View itemView) {
            super(itemView);
            mThumb = itemView.findViewById(R.id.thumb);
            mName = itemView.findViewById(R.id.name);
            mImg = itemView.findViewById(R.id.img);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(CoinPayBean bean, int position, Object payload) {
            if (payload == null) {
                itemView.setTag(position);
                ImgLoader.display(mContext, bean.getThumb(), mThumb);
                mName.setText(bean.getName());
            }
            mImg.setImageDrawable(bean.isChecked() ? mCheckedDrawable : null);
        }
    }

}
