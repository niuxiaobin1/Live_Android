package com.yunbao.main.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.live.activity.LiveAddImpressActivity;
import com.yunbao.main.R;
import com.yunbao.main.views.UserHomeViewHolder2;

/**
 * Created by cxf on 2018/9/25.
 */
@Route(path = RouteUtil.PATH_USER_HOME)
public class UserHomeActivity extends AbsActivity {

    private UserHomeViewHolder2 mUserHomeViewHolder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_empty;
    }

    @Override
    protected boolean isStatusBarWhite() {
        return true;
    }

    @Override
    protected void main() {
        Intent intent = getIntent();
        String toUid = intent.getStringExtra(Constants.TO_UID);
        if (TextUtils.isEmpty(toUid)) {
            return;
        }
        boolean fromLiveRoom = intent.getBooleanExtra(Constants.FROM_LIVE_ROOM, false);
        String fromLiveUid = fromLiveRoom ? intent.getStringExtra(Constants.LIVE_UID) : null;
        mUserHomeViewHolder = new UserHomeViewHolder2(mContext, (ViewGroup) findViewById(R.id.container), toUid, fromLiveRoom,fromLiveUid);
        mUserHomeViewHolder.addToParent();
        mUserHomeViewHolder.subscribeActivityLifeCycle();
        mUserHomeViewHolder.loadData();
    }


    public void addImpress(String toUid) {
        Intent intent = new Intent(mContext, LiveAddImpressActivity.class);
        intent.putExtra(Constants.TO_UID, toUid);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (mUserHomeViewHolder != null) {
                mUserHomeViewHolder.refreshImpress();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mUserHomeViewHolder != null) {
            mUserHomeViewHolder.release();
        }
        super.onDestroy();
    }
}
