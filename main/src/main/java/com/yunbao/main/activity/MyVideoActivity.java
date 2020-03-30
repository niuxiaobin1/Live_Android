package com.yunbao.main.activity;

import android.view.ViewGroup;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.views.VideoHomeViewHolder;

/**
 * Created by cxf on 2018/12/14.
 */

public class MyVideoActivity extends AbsActivity {

    private VideoHomeViewHolder mVideoHomeViewHolder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_video;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.video_my_video));
        mVideoHomeViewHolder = new VideoHomeViewHolder(mContext, (ViewGroup) findViewById(R.id.container), CommonAppConfig.getInstance().getUid());
        mVideoHomeViewHolder.addToParent();
        mVideoHomeViewHolder.subscribeActivityLifeCycle();
        mVideoHomeViewHolder.loadData();
    }

    private void release(){
        if(mVideoHomeViewHolder!=null){
            mVideoHomeViewHolder.release();
        }
        mVideoHomeViewHolder=null;
    }

    @Override
    public void onBackPressed() {
        release();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
    }
}
