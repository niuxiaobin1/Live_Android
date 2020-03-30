package com.yunbao.main.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;

/**
 * Created by cxf on 2018/9/22.
 * MainActivity 首页
 */

public class MainHomeViewHolder extends AbsMainHomeParentViewHolder {

    private MainHomeFollowViewHolder mFollowViewHolder;
    private MainHomeLiveViewHolder mLiveViewHolder;
    private MainHomeVideoViewHolder mVideoViewHolder;
    private View[] mIcons;


    public MainHomeViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home;
    }

    @Override
    public void init() {
        super.init();
        mIcons = new View[3];
        mIcons[0] = findViewById(R.id.icon_home_top_follow);
        mIcons[1] = findViewById(R.id.icon_home_top_live);
        mIcons[2] = findViewById(R.id.icon_home_top_video);
    }

    @Override
    protected void loadPageData(int position) {
        if (mViewHolders == null) {
            return;
        }
        AbsMainHomeChildViewHolder vh = mViewHolders[position];
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }
                if (position == 0) {
                    mFollowViewHolder = new MainHomeFollowViewHolder(mContext, parent);
                    vh = mFollowViewHolder;
                } else if (position == 1) {
                    mLiveViewHolder = new MainHomeLiveViewHolder(mContext, parent);
                    vh = mLiveViewHolder;
                } else if (position == 2) {
                    mVideoViewHolder = new MainHomeVideoViewHolder(mContext, parent);
                    vh = mVideoViewHolder;
                }
                if (vh == null) {
                    return;
                }
                mViewHolders[position] = vh;
                vh.addToParent();
                vh.subscribeActivityLifeCycle();
            }
        }
        if (vh != null) {
            vh.loadData();
        }
        if (mIcons != null) {
            for (int i = 0, len = mIcons.length; i < len; i++) {
                View v = mIcons[i];
                if (v != null) {
                    if (i == position) {
                        if (v.getVisibility() != View.VISIBLE) {
                            v.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (v.getVisibility() == View.VISIBLE) {
                            v.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected int getPageCount() {
        return 3;
    }

    @Override
    protected String[] getTitles() {
        return new String[]{
                WordUtil.getString(R.string.follow),
                WordUtil.getString(R.string.live),
                WordUtil.getString(R.string.video)
        };
    }


}
