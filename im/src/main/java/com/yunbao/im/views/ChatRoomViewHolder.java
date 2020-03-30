package com.yunbao.im.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.ImChatFacePagerAdapter;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.event.FollowEvent;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.KeyBoardHeightChangeListener;
import com.yunbao.common.interfaces.OnFaceClickListener;
import com.yunbao.common.utils.DateFormatUtil;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsViewHolder;
import com.yunbao.im.R;
import com.yunbao.im.activity.ChatRoomActivity;
import com.yunbao.im.adapter.ImRoomAdapter;
import com.yunbao.im.bean.ImMessageBean;
import com.yunbao.im.custom.MyImageView;
import com.yunbao.im.dialog.ChatImageDialog;
import com.yunbao.im.http.ImHttpUtil;
import com.yunbao.im.interfaces.ChatRoomActionListener;
import com.yunbao.im.utils.ImMessageUtil;
import com.yunbao.im.utils.ImTextRender;
import com.yunbao.im.utils.MediaRecordUtil;
import com.yunbao.im.utils.VoiceMediaPlayerUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;


/**
 * Created by cxf on 2018/10/24.
 */

public class ChatRoomViewHolder extends AbsViewHolder implements
        View.OnClickListener, OnFaceClickListener,
        ImRoomAdapter.ActionListener {

    private InputMethodManager imm;
    private RecyclerView mRecyclerView;
    private ImRoomAdapter mAdapter;
    private EditText mEditText;
    private TextView mVoiceRecordEdit;
    private Drawable mVoiceUnPressedDrawable;
    private Drawable mVoicePressedDrawable;
    private TextView mTitleView;
    private UserBean mUserBean;
    private String mToUid;
    private ChatRoomActionListener mActionListener;
    private ImMessageBean mCurMessageBean;
    private long mLastSendTime;//上一次发消息的时间
    private HttpCallback mCheckBlackCallback;
    private CheckBox mBtnFace;
    private CheckBox mBtnVoice;
    private View mFaceView;//表情控件
    private View mMoreView;//更多控件
    private ViewGroup mFaceContainer;//表情弹窗
    private ViewGroup mMoreContainer;//更多弹窗
    private ChatImageDialog mChatImageDialog;//图片预览弹窗
    private boolean mFollowing;
    private View mFollowGroup;
    private String mPressSayString;
    private String mUnPressStopString;
    private MediaRecordUtil mMediaRecordUtil;
    private File mRecordVoiceFile;//录音文件
    private long mRecordVoiceDuration;//录音时长
    private Handler mHandler;
    private VoiceMediaPlayerUtil mVoiceMediaPlayerUtil;

    public ChatRoomViewHolder(Context context, ViewGroup parentView, UserBean userBean, boolean following) {
        super(context, parentView, userBean, following);
    }

    @Override
    protected void processArguments(Object... args) {
        mUserBean = (UserBean) args[0];
        mFollowing = (boolean) args[1];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_chat_room;
    }

    @Override
    public void init() {
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mFaceContainer = (ViewGroup) findViewById(R.id.face_container);
        mMoreContainer = (ViewGroup) findViewById(R.id.more_container);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mTitleView = (TextView) findViewById(R.id.titleView);
        mEditText = (EditText) findViewById(R.id.edit);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendText();
                    return true;
                }
                return false;
            }
        });
        mEditText.setOnClickListener(this);
        mVoiceRecordEdit = (TextView) findViewById(R.id.btn_voice_record_edit);
        if (mVoiceRecordEdit != null) {
            mVoiceUnPressedDrawable = ContextCompat.getDrawable(mContext, R.drawable.bg_chat_voice_record_0);
            mVoicePressedDrawable = ContextCompat.getDrawable(mContext, R.drawable.bg_chat_voice_record_1);
            mPressSayString = WordUtil.getString(R.string.im_press_say);
            mUnPressStopString = WordUtil.getString(R.string.im_unpress_stop);
            mVoiceRecordEdit.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent e) {
                    switch (e.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startRecordVoice();
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            stopRecordVoice();
                            break;
                    }
                    return true;
                }
            });
        }
        mFollowGroup = findViewById(R.id.btn_follow_group);
        if (!mFollowing) {
            mFollowGroup.setVisibility(View.VISIBLE);
            mFollowGroup.findViewById(R.id.btn_close_follow).setOnClickListener(this);
            mFollowGroup.findViewById(R.id.btn_follow).setOnClickListener(this);
        }
        findViewById(R.id.btn_back).setOnClickListener(this);
        mBtnFace = (CheckBox) findViewById(R.id.btn_face);
        mBtnFace.setOnClickListener(this);
        View btnAdd = findViewById(R.id.btn_add);
        if (btnAdd != null) {
            btnAdd.setOnClickListener(this);
        }
        mBtnVoice = (CheckBox) findViewById(R.id.btn_voice_record);
        if (mBtnVoice != null) {
            mBtnVoice.setOnClickListener(this);
        }
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    return hideSoftInput() || hideFace() || hideMore();
                }
                return false;
            }
        });
        mCheckBlackCallback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                processCheckBlackData(code, msg, info);
            }
        };
        EventBus.getDefault().register(this);
        mHandler = new Handler();
        mEditText.requestFocus();
        findViewById(R.id.btn_user_home).setOnClickListener(this);
    }

    /**
     * 初始化表情控件
     */
    private View initFaceView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.view_chat_face, mFaceContainer, false);
        v.findViewById(R.id.btn_send).setOnClickListener(this);
        final RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radio_group);
        ViewPager viewPager = (ViewPager) v.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(10);
        ImChatFacePagerAdapter adapter = new ImChatFacePagerAdapter(mContext, this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((RadioButton) radioGroup.getChildAt(position)).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for (int i = 0, pageCount = adapter.getCount(); i < pageCount; i++) {
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.view_chat_indicator, radioGroup, false);
            radioButton.setId(i + 10000);
            if (i == 0) {
                radioButton.setChecked(true);
            }
            radioGroup.addView(radioButton);
        }
        return v;
    }


    private View initMoreView() {
        View v = LayoutInflater.from(mContext).inflate(R.layout.view_chat_more, null);
        v.findViewById(R.id.btn_img).setOnClickListener(this);
        v.findViewById(R.id.btn_camera).setOnClickListener(this);
        v.findViewById(R.id.btn_voice).setOnClickListener(this);
        v.findViewById(R.id.btn_location).setOnClickListener(this);
        return v;
    }


    public void loadData() {
        if (mUserBean == null) {
            return;
        }
        mToUid = mUserBean.getId();
        if (TextUtils.isEmpty(mToUid)) {
            return;
        }
        mTitleView.setText(mUserBean.getUserNiceName());
        mAdapter = new ImRoomAdapter(mContext, mToUid, mUserBean);
        mAdapter.setActionListener(this);
        mRecyclerView.setAdapter(mAdapter);
        ImMessageUtil.getInstance().getChatMessageList(mToUid, new CommonCallback<List<ImMessageBean>>() {
            @Override
            public void callback(List<ImMessageBean> list) {
                mAdapter.setList(list);
                mAdapter.scrollToBottom();
            }
        });
    }


    public void setActionListener(ChatRoomActionListener actionListener) {
        mActionListener = actionListener;
    }

    public void scrollToBottom() {
        if (mAdapter != null) {
            mAdapter.scrollToBottom();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_back) {
            back();

        } else if (i == R.id.btn_send) {
            sendText();

        } else if (i == R.id.btn_face) {
            faceClick();

        } else if (i == R.id.edit) {
            clickInput();

        } else if (i == R.id.btn_add) {
            clickMore();

        } else if (i == R.id.btn_voice_record) {
            if (mActionListener != null) {
                mActionListener.onVoiceClick();
            }

        } else if (i == R.id.btn_img) {
            if (mActionListener != null) {
                mActionListener.onChooseImageClick();
            }

        } else if (i == R.id.btn_camera) {
            if (mActionListener != null) {
                mActionListener.onCameraClick();
            }

        } else if (i == R.id.btn_voice) {
            if (mActionListener != null) {
                mActionListener.onVoiceInputClick();
            }

        } else if (i == R.id.btn_location) {
            if (mActionListener != null) {
                mActionListener.onLocationClick();
            }

        } else if (i == R.id.btn_close_follow) {
            closeFollow();

        } else if (i == R.id.btn_follow) {
            follow();

        } else if (i == R.id.btn_user_home) {
            if (mContext instanceof ChatRoomActivity && ((ChatRoomActivity) mContext).isFromUserHome()) {
                ((ChatRoomActivity) mContext).superBackPressed();
            } else {
                RouteUtil.forwardUserHome(mContext, mToUid);
            }
        }
    }

    /**
     * 关闭关注提示
     */
    private void closeFollow() {
        if (mFollowGroup != null && mFollowGroup.getVisibility() == View.VISIBLE) {
            mFollowGroup.setVisibility(View.GONE);
        }
    }

    /**
     * 关注
     */
    private void follow() {
        CommonHttpUtil.setAttention(mToUid, null);
    }

    /**
     * 返回
     */
    public void back() {
        if (hideMore() || hideFace() || hideSoftInput()) {
            return;
        }
        if (mActionListener != null) {
            mActionListener.onCloseClick();
        }
    }

    /**
     * 点击输入框
     */
    private void clickInput() {
        hideFace();
        hideMore();
    }


    /**
     * 显示软键盘
     */
    private void showSoftInput() {
        if (!((KeyBoardHeightChangeListener) mContext).isSoftInputShowed() && imm != null && mEditText != null) {
            imm.showSoftInput(mEditText, InputMethodManager.SHOW_FORCED);
            mEditText.requestFocus();
        }
    }

    /**
     * 隐藏键盘
     */
    private boolean hideSoftInput() {
        if (((KeyBoardHeightChangeListener) mContext).isSoftInputShowed() && imm != null && mEditText != null) {
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
            return true;
        }
        return false;
    }

    /**
     * 点击表情按钮
     */
    private void faceClick() {
        hideMore();
        if (mBtnFace.isChecked()) {
            hideSoftInput();
            hideVoiceRecord();
            if (mHandler != null) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showFace();
                    }
                }, 200);
            }
        } else {
            hideFace();
            showSoftInput();
        }
    }

    /**
     * 表情弹窗是否显示
     */
    private boolean isFaceShowing() {
        return mFaceContainer != null && mFaceContainer.getVisibility() != View.GONE;
    }

    /**
     * 更多弹窗是否显示
     */
    private boolean isMoreShowing() {
        return mMoreContainer != null && mMoreContainer.getVisibility() != View.GONE;
    }


    /**
     * 显示表情弹窗
     */
    private void showFace() {
        if (isFaceShowing()) {
            return;
        }
        hideMore();
        if (mFaceView == null) {
            mFaceView = initFaceView();
            mFaceContainer.addView(mFaceView);
        }
        mFaceContainer.setVisibility(View.VISIBLE);
        scrollToBottom();
    }

    /**
     * 隐藏表情弹窗
     */
    private boolean hideFace() {
        if (isFaceShowing()) {
            mFaceContainer.setVisibility(View.GONE);
            if (mBtnFace != null) {
                mBtnFace.setChecked(false);
            }
            return true;
        }
        return false;
    }


    /**
     * 点击更多按钮
     */
    private void clickMore() {
        hideFace();
        hideSoftInput();
        hideVoiceRecord();
        if (mHandler != null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showMore();
                }
            }, 200);
        }
    }

    /**
     * 显示更多弹窗
     */
    private void showMore() {
        if (isMoreShowing()) {
            return;
        }
        hideFace();
        if (mMoreView == null) {
            mMoreView = initMoreView();
            mMoreContainer.addView(mMoreView);
        }
        mMoreContainer.setVisibility(View.VISIBLE);
        scrollToBottom();
    }

    /**
     * 隐藏更多弹窗
     */
    private boolean hideMore() {
        if (isMoreShowing()) {
            mMoreContainer.setVisibility(View.GONE);
            return true;
        }
        return false;
    }


    /**
     * 点击表情图标按钮
     */
    @Override
    public void onFaceClick(String str, int faceImageRes) {
        if (mEditText != null) {
            Editable editable = mEditText.getText();
            editable.insert(mEditText.getSelectionStart(), ImTextRender.getFaceImageSpan(str, faceImageRes));
        }
    }

    /**
     * 点击表情删除按钮
     */
    @Override
    public void onFaceDeleteClick() {
        if (mEditText != null) {
            int selection = mEditText.getSelectionStart();
            String text = mEditText.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1, selection);
                if ("]".equals(text2)) {
                    int start = text.lastIndexOf("[", selection);
                    if (start >= 0) {
                        mEditText.getText().delete(start, selection);
                    } else {
                        mEditText.getText().delete(selection - 1, selection);
                    }
                } else {
                    mEditText.getText().delete(selection - 1, selection);
                }
            }
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        if (mMediaRecordUtil != null) {
            mMediaRecordUtil.release();
        }
        mMediaRecordUtil = null;
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.destroy();
        }
        mVoiceMediaPlayerUtil = null;
        if (mAdapter != null) {
            mAdapter.release();
        }
        ImMessageUtil.getInstance().refreshAllUnReadMsgCount();
        EventBus.getDefault().unregister(this);
        mActionListener = null;
        if (mChatImageDialog != null) {
            mChatImageDialog.dismiss();
        }
        mChatImageDialog = null;
    }

    /**
     * 点击图片的回调，显示图片
     */
    @Override
    public void onImageClick(MyImageView imageView, int x, int y) {
        if (mAdapter == null || imageView == null) {
            return;
        }
        hideSoftInput();
        File imageFile = imageView.getFile();
        ImMessageBean imMessageBean = imageView.getImMessageBean();
        if (imageFile != null && imMessageBean != null) {
            mChatImageDialog = new ChatImageDialog();
            mChatImageDialog.setImageInfo(mAdapter.getChatImageBean(imMessageBean), x, y, imageView.getWidth(), imageView.getHeight(), imageView.getDrawable());
            mChatImageDialog.show(((AbsActivity) mContext).getSupportFragmentManager(), "ChatImageDialog2");
        }
    }


    /**
     * 点击语音消息的回调，播放语音
     */
    @Override
    public void onVoiceStartPlay(File voiceFile) {
        if (mVoiceMediaPlayerUtil == null) {
            mVoiceMediaPlayerUtil = new VoiceMediaPlayerUtil(mContext);
            mVoiceMediaPlayerUtil.setActionListener(new VoiceMediaPlayerUtil.ActionListener() {
                @Override
                public void onPlayEnd() {
                    if (mAdapter != null) {
                        mAdapter.stopVoiceAnim();
                    }
                }
            });
        }
        mVoiceMediaPlayerUtil.startPlay(voiceFile.getAbsolutePath());
    }

    /**
     * 点击语音消息的回调，停止播放语音
     */
    @Override
    public void onVoiceStopPlay() {
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.stopPlay();
        }
    }


    /**
     * 隐藏录音
     */
    private void hideVoiceRecord() {
        if (mBtnVoice != null && mBtnVoice.isChecked()) {
            mBtnVoice.setChecked(false);
            if (mEditText.getVisibility() != View.VISIBLE) {
                mEditText.setVisibility(View.VISIBLE);
                mEditText.requestFocus();
            }
            if (mVoiceRecordEdit.getVisibility() == View.VISIBLE) {
                mVoiceRecordEdit.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 点击录音
     */
    public void clickVoiceRecord() {
        if (mBtnVoice == null) {
            return;
        }
        if (mBtnVoice.isChecked()) {
            hideSoftInput();
            hideFace();
            hideMore();
            if (mEditText.getVisibility() == View.VISIBLE) {
                mEditText.setVisibility(View.INVISIBLE);
            }
            if (mVoiceRecordEdit != null && mVoiceRecordEdit.getVisibility() != View.VISIBLE) {
                mVoiceRecordEdit.setVisibility(View.VISIBLE);
            }
        } else {
            if (mVoiceRecordEdit != null && mVoiceRecordEdit.getVisibility() == View.VISIBLE) {
                mVoiceRecordEdit.setVisibility(View.INVISIBLE);
            }
            if (mEditText.getVisibility() != View.VISIBLE) {
                mEditText.setVisibility(View.VISIBLE);
                mEditText.requestFocus();
            }
        }
    }

    /**
     * 开始录音
     */
    public void startRecordVoice() {
        if (mVoiceRecordEdit == null) {
            return;
        }
        mVoiceRecordEdit.setBackground(mVoicePressedDrawable);
        mVoiceRecordEdit.setText(mUnPressStopString);
        if (mMediaRecordUtil == null) {
            mMediaRecordUtil = new MediaRecordUtil();
        }
        File dir = new File(CommonAppConfig.MUSIC_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        mRecordVoiceFile = new File(dir, DateFormatUtil.getCurTimeString() + ".m4a");
        mMediaRecordUtil.startRecord(mRecordVoiceFile.getAbsolutePath());
        if (mHandler != null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopRecordVoice();
                }
            }, 60000);
        }
    }

    /**
     * 结束录音
     */
    public void stopRecordVoice() {
        if (mVoiceRecordEdit == null) {
            return;
        }
        mVoiceRecordEdit.setBackground(mVoiceUnPressedDrawable);
        mVoiceRecordEdit.setText(mPressSayString);
        mRecordVoiceDuration = mMediaRecordUtil.stopRecord();
        if (mRecordVoiceDuration < 2000) {
            ToastUtil.show(WordUtil.getString(R.string.im_record_audio_too_short));
            deleteVoiceFile();
        } else {
            mCurMessageBean = ImMessageUtil.getInstance().createVoiceMessage(mToUid, mRecordVoiceFile, mRecordVoiceDuration);
            if (mCurMessageBean != null) {
                sendMessage();
            } else {
                deleteVoiceFile();
            }
        }
    }

    /**
     * 删除录音文件
     */
    private void deleteVoiceFile() {
        if (mRecordVoiceFile != null && mRecordVoiceFile.exists()) {
            mRecordVoiceFile.delete();
        }
        mRecordVoiceFile = null;
        mRecordVoiceDuration = 0;
    }

    /**************************************************************************************************/
    /*********************************以上是处理界面逻辑，以下是处理消息逻辑***********************************/
    /**************************************************************************************************/

    /**
     * 刷新最后一条聊天数据
     */
    public void refreshLastMessage() {
        if (mAdapter != null) {
            ImMessageBean bean = mAdapter.getLastMessage();
            if (bean != null) {
                ImMessageUtil.getInstance().refreshLastMessage(mToUid, bean);
            }
        }
    }


    /**
     * 收到消息的回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImMessageBean(ImMessageBean bean) {
        if (!bean.getUid().equals(mToUid)) {
            return;
        }
        if (mAdapter != null) {
            mAdapter.insertItem(bean);
            ImMessageUtil.getInstance().markAllMessagesAsRead(mToUid, false);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {
        if (e.getToUid().equals(mToUid)) {
            if (mFollowGroup != null) {
                if (e.getIsAttention() == 1) {
                    if (mFollowGroup.getVisibility() == View.VISIBLE) {
                        mFollowGroup.setVisibility(View.GONE);
                    }
                } else {
                    if (mFollowGroup.getVisibility() != View.VISIBLE) {
                        mFollowGroup.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }


    /**
     * 检查是否能够发送消息
     */
    private boolean isCanSendMsg() {
        if (!CommonAppConfig.getInstance().isLoginIM()) {
            ToastUtil.show("IM暂未接入，无法使用");
            return false;
        }
        long curTime = System.currentTimeMillis();
        if (curTime - mLastSendTime < 1500) {
            ToastUtil.show(R.string.im_send_too_fast);
            return false;
        }
        mLastSendTime = curTime;
        return true;
    }

    /**
     * 发送文本信息
     */
    public void sendText(String content) {
        if (TextUtils.isEmpty(content)) {
            ToastUtil.show(R.string.content_empty);
            return;
        }
        ImMessageBean messageBean = ImMessageUtil.getInstance().createTextMessage(mToUid, content);
        if (messageBean == null) {
            ToastUtil.show(R.string.im_msg_send_failed);
            return;
        }
        mCurMessageBean = messageBean;
        sendMessage();
    }

    /**
     * 发送文本信息
     */
    private void sendText() {
        String content = mEditText.getText().toString().trim();
        sendText(content);
    }

    /**
     * 发送图片消息
     */
    public void sendImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        ImMessageBean messageBean = ImMessageUtil.getInstance().createImageMessage(mToUid, path);
        if (messageBean == null) {
            ToastUtil.show(R.string.im_msg_send_failed);
            return;
        }
        mCurMessageBean = messageBean;
        sendMessage();
    }

    /**
     * 发送位置消息
     */
    public void sendLocation(double lat, double lng, int scale, String address) {
        ImMessageBean messageBean = ImMessageUtil.getInstance().createLocationMessage(mToUid, lat, lng, scale, address);
        if (messageBean == null) {
            ToastUtil.show(R.string.im_msg_send_failed);
            return;
        }
        mCurMessageBean = messageBean;
        sendMessage();
    }


    /**
     * 发送消息
     */
    private void sendMessage() {
        if (!isCanSendMsg()) {
            return;
        }
        if (mCurMessageBean != null) {
            ImHttpUtil.checkBlack(mToUid, mCheckBlackCallback);
        } else {
            ToastUtil.show(R.string.im_msg_send_failed);
        }
    }

    /**
     * 处理拉黑接口返回的数据
     */
    private void processCheckBlackData(int code, String msg, String[] info) {
        if (code == 0) {
            if (info.length > 0) {
                JSONObject obj = JSON.parseObject(info[0]);
                int t2u = obj.getIntValue("t2u");
                if (1 == t2u) {//被拉黑
                    ToastUtil.show(R.string.im_you_are_blacked);
                    if (mCurMessageBean != null) {
                        ImMessageUtil.getInstance().removeMessage(mToUid, mCurMessageBean);
                    }
                } else {
                    if (mCurMessageBean != null) {
                        if (mCurMessageBean.getType() == ImMessageBean.TYPE_TEXT) {
                            mEditText.setText("");
                        }
                        if (mAdapter != null) {
                            mAdapter.insertSelfItem(mCurMessageBean);
                        }
                    } else {
                        ToastUtil.show(WordUtil.getString(R.string.im_msg_send_failed));
                    }
                }
            }
        } else {
            ToastUtil.show(msg);
        }
    }


    public void onPause() {
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.pausePlay();
        }
    }

    public void onResume() {
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.resumePlay();
        }
    }


}
