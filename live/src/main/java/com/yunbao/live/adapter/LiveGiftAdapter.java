package com.yunbao.live.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.LiveGiftBean;
import com.yunbao.common.custom.MyRadioButton;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.R;
import com.yunbao.live.bean.BackPackGiftBean;
import com.yunbao.live.custom.GiftMarkView;
import java.util.List;

/**
 * Created by cxf on 2018/10/12.
 */

public  class LiveGiftAdapter extends RecyclerView.Adapter<LiveGiftAdapter.Vh> {

    private Context mContext;
    private List<LiveGiftBean> mList;
    private LayoutInflater mInflater;
    private String mCoinName;
    private View.OnClickListener mOnClickListener;
    private ActionListener mActionListener;
    private int mCheckedPosition = -1;
    private ScaleAnimation mAnimation;
    private View mAnimView;

    public LiveGiftAdapter(Context context,LayoutInflater inflater, List<LiveGiftBean> list, String coinName) {
        mContext=context;
        mInflater = inflater;
        mList = list;
        mCoinName = coinName;
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    LiveGiftBean bean = mList.get(position);
                    if (!bean.isChecked()) {
                        if (!cancelChecked()) {
                            if (mActionListener != null) {
                                mActionListener.onCancel();
                            }
                        }
                        bean.setChecked(true);
                        notifyItemChanged(position, Constants.PAYLOAD);
                        View view = bean.getView();
                        if (view != null) {
                            view.startAnimation(mAnimation);
                            mAnimView = view;
                        }
                        mCheckedPosition = position;
                        if (mActionListener != null) {
                            mActionListener.onItemChecked(bean);
                        }
                    }
                }
            }
        };
        mAnimation = new ScaleAnimation(0.9f, 1.1f, 0.9f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimation.setDuration(400);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setRepeatCount(-1);
    }


    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_live_gift, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {

    }

    public void onBindViewHolder(@NonNull Vh holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        holder.setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * 取消选中
     */
    public boolean cancelChecked() {
        if (mCheckedPosition >= 0 && mCheckedPosition < mList.size()) {
            LiveGiftBean bean = mList.get(mCheckedPosition);
            if (bean.isChecked()) {
                View view = bean.getView();
                if (mAnimView == view) {
                    mAnimView.clearAnimation();
                } else {
                    if (view != null) {
                        view.clearAnimation();
                    }
                }
                mAnimView = null;
                bean.setChecked(false);
                notifyItemChanged(mCheckedPosition, Constants.PAYLOAD);
            }
            mCheckedPosition = -1;
            return true;
        }
        return false;
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public void release() {
        if (mAnimView != null) {
            mAnimView.clearAnimation();
        }
        if (mList != null) {
            mList.clear();
        }
        mOnClickListener = null;
        mActionListener = null;
    }

    class Vh extends RecyclerView.ViewHolder {

        GiftMarkView mMark;
        ImageView mIcon;
        TextView mName;
        TextView mPrice;
        MyRadioButton mRadioButton;

        public Vh(View itemView) {
            super(itemView);
            mMark = (GiftMarkView) itemView.findViewById(R.id.mark);
            mIcon = (ImageView) itemView.findViewById(R.id.icon);
            mName = (TextView) itemView.findViewById(R.id.name);
            mPrice = (TextView) itemView.findViewById(R.id.price);
            mRadioButton = (MyRadioButton) itemView.findViewById(R.id.radioButton);
            mRadioButton.setOnClickListener(mOnClickListener);
        }

        void setData(LiveGiftBean bean, int position, Object payload) {
            if (payload == null) {
                ImgLoader.display(mContext,bean.getIcon(), mIcon);
                bean.setView(mIcon);
                mName.setText(bean.getName());

                int mark=bean.getMark();
                if (bean.getType() == LiveGiftBean.TYPE_NORMAL) {
                    if (mark== LiveGiftBean.MARK_HOT) {
                        mMark.setIconRes(R.mipmap.icon_live_gift_hot, 0);
                    }
                    else if (mark == LiveGiftBean.MARK_GUARD) {
                        mMark.setIconRes(R.mipmap.icon_live_gift_guard, 0);
                    }
                    else if (mark == LiveGiftBean.MARK_LUCK) {
                        mMark.setIconRes(R.mipmap.icon_live_gift_luck, 0);
                    }
                    else {
                        mMark.setIconRes(0, 0);
                    }
                } else {
                    if (mark == LiveGiftBean.MARK_HOT) {
                        mMark.setIconRes(R.mipmap.icon_live_gift_hot, R.mipmap.icon_live_gift_hao);
                    } else if (mark == LiveGiftBean.MARK_GUARD) {
                        mMark.setIconRes(R.mipmap.icon_live_gift_guard, R.mipmap.icon_live_gift_hao);
                    }
                    else if (mark == LiveGiftBean.MARK_LUCK) {
                        mMark.setIconRes(R.mipmap.icon_live_gift_luck, R.mipmap.icon_live_gift_hao);
                    }
                    else {
                        mMark.setIconRes(0, R.mipmap.icon_live_gift_hao);
                    }
                }

                if(bean instanceof BackPackGiftBean){
                    BackPackGiftBean backPackGiftBean= (BackPackGiftBean) bean;
                    mPrice.setText(backPackGiftBean.getNums() + WordUtil.getString(R.string.ge));
                }else{
                    mPrice.setText(bean.getPrice() + mCoinName);
                }


            }
            mRadioButton.setTag(position);
            mRadioButton.doChecked(bean.isChecked());
        }
    }


    public interface ActionListener {
        void onCancel();

        void onItemChecked(LiveGiftBean bean);
    }

}
