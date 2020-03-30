package com.yunbao.main.views;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.activity.LiveContributeActivity;
import com.yunbao.live.activity.LiveGuardListActivity;
import com.yunbao.live.bean.ImpressBean;
import com.yunbao.live.bean.SearchUserBean;
import com.yunbao.live.custom.MyTextView;
import com.yunbao.live.views.AbsUserHomeViewHolder;
import com.yunbao.main.R;
import com.yunbao.main.activity.MyImpressActivity;
import com.yunbao.main.activity.UserHomeActivity;
import com.yunbao.main.bean.UserHomeConBean;

import java.util.List;

/**
 * Created by cxf on 2018/12/14.
 * 用户个人中心资料
 */

public class UserHomeDetailViewHolder extends AbsUserHomeViewHolder implements View.OnClickListener {

    private String mToUid;
    private boolean mSelf;
    private LayoutInflater mInflater;
    private TextView mVotesName;
    private ImageView[] mAvatarCon;
    private ImageView[] mAvatarGuard;
    private LinearLayout mImpressGroup;
    private View mNoImpressTip;
    private TextView mSign;
    private TextView mBirthday;
    private TextView mCity;
    private View.OnClickListener mAddImpressOnClickListener;

    public UserHomeDetailViewHolder(Context context, ViewGroup parentView, String toUid, boolean self) {
        super(context, parentView, toUid, self);
    }

    @Override
    protected void processArguments(Object... args) {
        mToUid = (String) args[0];
        mSelf = (boolean) args[1];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_user_home_detail;
    }

    @Override
    public void init() {
        if (TextUtils.isEmpty(mToUid)) {
            return;
        }
        mInflater = LayoutInflater.from(mContext);
        findViewById(R.id.con_group_wrap).setOnClickListener(this);
        findViewById(R.id.guard_group_wrap).setOnClickListener(this);
        if (mSelf) {
            findViewById(R.id.btn_impress).setOnClickListener(this);
            findViewById(R.id.impress_arrow).setVisibility(View.VISIBLE);
        }
        mVotesName = (TextView) findViewById(R.id.votes_name);
        mAvatarCon = new ImageView[3];
        mAvatarGuard = new ImageView[3];
        mAvatarCon[0] = (ImageView) findViewById(R.id.avatar_con_1);
        mAvatarCon[1] = (ImageView) findViewById(R.id.avatar_con_2);
        mAvatarCon[2] = (ImageView) findViewById(R.id.avatar_con_3);
        mAvatarGuard[0] = (ImageView) findViewById(R.id.avatar_guard_1);
        mAvatarGuard[1] = (ImageView) findViewById(R.id.avatar_guard_2);
        mAvatarGuard[2] = (ImageView) findViewById(R.id.avatar_guard_3);
        mImpressGroup = (LinearLayout) findViewById(R.id.impress_group);
        mNoImpressTip = findViewById(R.id.no_impress_tip);
        mSign = (TextView) findViewById(R.id.sign);
        mBirthday = (TextView) findViewById(R.id.birthday);
        mCity = (TextView) findViewById(R.id.city);
        mAddImpressOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImpress();
            }
        };
    }

    /**
     * 显示印象
     */
    public void showImpress(String impressJson) {
        if (mImpressGroup != null) {
            mImpressGroup.removeAllViews();
            List<ImpressBean> list = JSON.parseArray(impressJson, ImpressBean.class);
            if (list.size() == 0) {
                if (mSelf) {
                    mNoImpressTip.setVisibility(View.VISIBLE);
                } else {
                    TextView textView = (TextView) mInflater.inflate(R.layout.view_impress_item_add, mImpressGroup, false);
                    textView.setOnClickListener(mAddImpressOnClickListener);
                    mImpressGroup.addView(textView);
                }
                return;
            }

            if (list.size() > 3) {
                list = list.subList(0, 3);
            }
            for (int i = 0, size = list.size(); i < size; i++) {
                MyTextView myTextView = (MyTextView) mInflater.inflate(R.layout.view_impress_item_3, mImpressGroup, false);
                ImpressBean bean = list.get(i);
                bean.setCheck(1);
                myTextView.setBean(bean);
                mImpressGroup.addView(myTextView);
            }
            if (!mSelf) {
                TextView textView = (TextView) mInflater.inflate(R.layout.view_impress_item_add, mImpressGroup, false);
                textView.setOnClickListener(mAddImpressOnClickListener);
                mImpressGroup.addView(textView);
            }
        }
    }


    /**
     * 显示贡献榜
     */
    private void showContribute(String conJson) {
        List<UserHomeConBean> list = JSON.parseArray(conJson, UserHomeConBean.class);
        if (list.size() == 0) {
            return;
        }
        if (list.size() > 3) {
            list = list.subList(0, 3);
        }
        for (int i = 0, size = list.size(); i < size; i++) {
            if (mAvatarCon[i] != null) {
                ImgLoader.display(mContext, list.get(i).getAvatar(), mAvatarCon[i]);
            }
        }
    }

    /**
     * 显示守护榜
     */
    private void showGuardList(String guardJson) {
        List<UserBean> list = JSON.parseArray(guardJson, UserBean.class);
        if (list.size() == 0) {
            return;
        }
        if (list.size() > 3) {
            list = list.subList(0, 3);
        }
        for (int i = 0, size = list.size(); i < size; i++) {
            if (mAvatarGuard[i] != null) {
                ImgLoader.display(mContext, list.get(i).getAvatar(), mAvatarGuard[i]);
            }
        }
    }


    /**
     * 添加印象
     */
    private void addImpress() {
        if (mContext instanceof UserHomeActivity) {
            ((UserHomeActivity) mContext).addImpress(mToUid);
        }
    }


    /**
     * 查看贡献榜
     */
    private void forwardContribute() {
        Intent intent = new Intent(mContext, LiveContributeActivity.class);
        intent.putExtra(Constants.TO_UID, mToUid);
        mContext.startActivity(intent);
    }

    /**
     * 查看守护榜
     */
    private void forwardGuardList() {
        LiveGuardListActivity.forward(mContext, mToUid);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.con_group_wrap) {
            forwardContribute();
        } else if (i == R.id.guard_group_wrap) {
            forwardGuardList();
        } else if (i == R.id.btn_impress) {
            if (mSelf && mContext != null) {
                mContext.startActivity(new Intent(mContext, MyImpressActivity.class));
            }
        }
    }

    @Override
    public void loadData() {
    }


    public void showData(SearchUserBean userBean, JSONObject obj) {
        if (mSign != null) {
            mSign.setText(userBean.getSignature());
        }
        if (mVotesName != null) {
            mVotesName.setText(StringUtil.contact(CommonAppConfig.getInstance().getVotesName(), WordUtil.getString(R.string.live_user_home_con)));
        }
        if (mBirthday != null) {
            mBirthday.setText(StringUtil.contact(WordUtil.getString(R.string.edit_profile_birthday), "： ", obj.getString("birthday")));
        }
        if (mCity != null) {
            mCity.setText(StringUtil.contact(WordUtil.getString(R.string.edit_profile_city), "： ", obj.getString("location")));
        }
        showImpress(obj.getString("label"));
        showContribute(obj.getString("contribute"));
        showGuardList(obj.getString("guardlist"));
    }

    public void refreshData(SearchUserBean userBean, JSONObject obj) {
        if (mSign != null) {
            mSign.setText(userBean.getSignature());
        }
        if (mBirthday != null) {
            mBirthday.setText(StringUtil.contact(WordUtil.getString(R.string.edit_profile_birthday), "： ", obj.getString("birthday")));
        }
        if (mCity != null) {
            mCity.setText(StringUtil.contact(WordUtil.getString(R.string.edit_profile_city), "： ", obj.getString("location")));
        }
    }


}
