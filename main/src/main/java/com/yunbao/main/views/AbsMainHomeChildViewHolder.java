package com.yunbao.main.views;

import android.content.Context;
import android.view.ViewGroup;

import com.yunbao.main.activity.LiveVideoListActivity;
import com.yunbao.main.activity.MainActivity;
import com.yunbao.live.bean.LiveBean;

/**
 * Created by cxf on 2018/9/22.
 * MainActivity中的首页，附近 的子页面
 */

public abstract class AbsMainHomeChildViewHolder extends AbsMainViewHolder {


    public AbsMainHomeChildViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    /**
     * 观看直播
     */
    public void watchLive(LiveBean liveBean, String key, int position) {
        if (mContext instanceof LiveVideoListActivity){
            ((LiveVideoListActivity) mContext).watchLive(liveBean, key, position);
        }else{
            ((MainActivity) mContext).watchLive(liveBean, key, position);
        }

    }
}
