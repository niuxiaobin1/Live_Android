package com.yunbao.live.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.Constants;
import com.yunbao.live.R;
import com.yunbao.live.bean.LiveFunctionBean;
import com.yunbao.common.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/9.
 */

public class LiveFunctionAdapter extends RecyclerView.Adapter<LiveFunctionAdapter.Vh> {

    private List<LiveFunctionBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<Integer> mOnItemClickListener;

    public LiveFunctionAdapter(Context context, boolean hasGame, boolean openFlash) {
        mList = new ArrayList<>();
        mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_BEAUTY, R.mipmap.icon_live_func_beauty, R.string.live_beauty));
        mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_CAMERA, R.mipmap.icon_live_func_camera, R.string.live_camera));
        mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_FLASH, openFlash ? R.mipmap.icon_live_func_flash : R.mipmap.icon_live_func_flash_1, R.string.live_flash));
        mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_MUSIC, R.mipmap.icon_live_func_music, R.string.live_music));
        mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_SHARE, R.mipmap.icon_live_func_share, R.string.live_share));
        if (hasGame) {//含有游戏
            mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_GAME, R.mipmap.icon_live_func_game, R.string.live_game));
        }
        mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_RED_PACK, R.mipmap.icon_live_func_rp, R.string.live_red_pack));
        mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_LINK_MIC, R.mipmap.icon_live_func_lm, R.string.live_link_mic));
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int functionID = (int) tag;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(functionID, 0);
                    }
                }
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener<Integer> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_live_function, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mIcon;
        TextView mName;


        public Vh(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.icon);
            mName = (TextView) itemView.findViewById(R.id.name);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(LiveFunctionBean bean) {
            itemView.setTag(bean.getID());
            mIcon.setImageResource(bean.getIcon());
            mName.setText(bean.getName());
        }
    }
}
