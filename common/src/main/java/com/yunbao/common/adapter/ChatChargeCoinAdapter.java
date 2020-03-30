package com.yunbao.common.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.CoinBean;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.R;

import java.util.List;

/**
 * Created by cxf on 2019/4/22.
 */

public class ChatChargeCoinAdapter extends RecyclerView.Adapter<ChatChargeCoinAdapter.Vh> {

    private List<CoinBean> mList;
    private LayoutInflater mInflater;
    private String mCoinName;
    private String mGiveString;
    private Drawable mCheckedDrawable;
    private Drawable mUnCheckedDrawable;
    private int mCheckedPosition;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<CoinBean> mOnItemClickListener;

    public ChatChargeCoinAdapter(Context context, List<CoinBean> list) {
        mInflater = LayoutInflater.from(context);
        mList = list;
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        mGiveString = WordUtil.getString(R.string.coin_give);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                int positon = (int) tag;
                CoinBean bean = mList.get(positon);
                if (mCheckedPosition != positon) {
                    if (mCheckedPosition >= 0 && mCheckedPosition < mList.size()) {
                        mList.get(mCheckedPosition).setChecked(false);
                        notifyItemChanged(mCheckedPosition, Constants.PAYLOAD);
                    }
                    bean.setChecked(true);
                    notifyItemChanged(positon, Constants.PAYLOAD);
                    mCheckedPosition = positon;
                }
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(bean, positon);
                }
            }
        };
        mCheckedDrawable = ContextCompat.getDrawable(context, R.drawable.bg_coin_item_1);
        mUnCheckedDrawable = ContextCompat.getDrawable(context, R.drawable.bg_coin_item_0);
    }


    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_coin, parent, false));
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

        TextView mCoin;
        TextView mMoney;
        TextView mGive;
        View mGiveGroup;
        View mBg;

        public Vh(View itemView) {
            super(itemView);
            mCoin = itemView.findViewById(R.id.coin);
            mMoney = itemView.findViewById(R.id.money);
            mGive = itemView.findViewById(R.id.give);
            mGiveGroup = itemView.findViewById(R.id.give_group);
            mBg = itemView.findViewById(R.id.bg);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(CoinBean bean, int position, Object payload) {
            if (payload == null) {
                itemView.setTag(position);
                mCoin.setText(bean.getCoin());
                mMoney.setText("￥" + bean.getMoney());
                if (!"0".equals(bean.getGive())) {
                    if (mGiveGroup.getVisibility() != View.VISIBLE) {
                        mGiveGroup.setVisibility(View.VISIBLE);
                    }
                    mGive.setText(mGiveString + bean.getGive() + mCoinName);
                } else {
                    if (mGiveGroup.getVisibility() == View.VISIBLE) {
                        mGiveGroup.setVisibility(View.INVISIBLE);
                    }
                }
            }
            mBg.setBackground(bean.isChecked() ? mCheckedDrawable : mUnCheckedDrawable);
        }
    }

    public void setOnItemClickListener(OnItemClickListener<CoinBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
