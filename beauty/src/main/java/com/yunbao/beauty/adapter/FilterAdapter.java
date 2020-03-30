package com.yunbao.beauty.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yunbao.beauty.bean.FilterBean;
import com.yunbao.common.Constants;
import com.yunbao.beauty.R;
import com.yunbao.common.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import cn.tillusory.sdk.bean.TiFilterEnum;

/**
 * Created by cxf on 2018/6/22.
 */

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.Vh> {

    private List<FilterBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<FilterBean> mOnItemClickListener;
    private int mCheckedPosition;

    public FilterAdapter(Context context) {
        mList = new ArrayList<>();
        mList.add(new FilterBean(R.mipmap.icon_filter_orginal, 0, TiFilterEnum.NO_FILTER, 0, true));
        mList.add(new FilterBean(R.mipmap.icon_filter_langman, R.mipmap.filter_langman, TiFilterEnum.CHOCOLATE_FILTER));
        mList.add(new FilterBean(R.mipmap.icon_filter_qingxin, R.mipmap.filter_qingxin, TiFilterEnum.COCO_FILTER));
        mList.add(new FilterBean(R.mipmap.icon_filter_weimei, R.mipmap.filter_weimei, TiFilterEnum.COFFEE_FILTER));
        mList.add(new FilterBean(R.mipmap.icon_filter_fennen, R.mipmap.filter_fennen, TiFilterEnum.DELICIOUS_FILTER));
        mList.add(new FilterBean(R.mipmap.icon_filter_huaijiu, R.mipmap.filter_huaijiu, TiFilterEnum.FIRSTLOVE_FILTER));
        mList.add(new FilterBean(R.mipmap.icon_filter_qingliang, R.mipmap.filter_qingliang, TiFilterEnum.FOREST_FILTER));
        mList.add(new FilterBean(R.mipmap.icon_filter_landiao, R.mipmap.filter_landiao, TiFilterEnum.GLOSSY_FILTER));
        mList.add(new FilterBean(R.mipmap.icon_filter_rixi, R.mipmap.filter_rixi, TiFilterEnum.GRASS_FILTER));
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    if (mCheckedPosition == position) {
                        return;
                    }
                    if (mCheckedPosition >= 0 && mCheckedPosition < mList.size()) {
                        mList.get(mCheckedPosition).setChecked(false);
                        notifyItemChanged(mCheckedPosition, Constants.PAYLOAD);
                    }
                    mList.get(position).setChecked(true);
                    notifyItemChanged(position, Constants.PAYLOAD);
                    mCheckedPosition = position;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mList.get(position), position);
                    }
                }
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener<FilterBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_filter, parent, false));
    }

    @Override
    public void onBindViewHolder(Vh holder, int position) {

    }

    @Override
    public void onBindViewHolder(Vh vh, int position, List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mImg;
        ImageView mCheckImg;

        public Vh(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.img);
            mCheckImg = (ImageView) itemView.findViewById(R.id.check_img);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(FilterBean bean, int position, Object payload) {
            itemView.setTag(position);
            if (payload == null) {
                mImg.setImageResource(bean.getImgSrc());
            }
            if (bean.isChecked()) {
                if (mCheckImg.getVisibility() != View.VISIBLE) {
                    mCheckImg.setVisibility(View.VISIBLE);
                }
            } else {
                if (mCheckImg.getVisibility() == View.VISIBLE) {
                    mCheckImg.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}
