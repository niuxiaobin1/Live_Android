package com.yunbao.common.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yunbao.common.CommonAppContext;
import com.yunbao.common.R;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;

/**
 * Created by cxf on 2018/8/29.
 * 服务器Home.getConfig接口有时候返回的数据无法解析，导致崩溃，
 * 这个类是用来收集服务器返回的错误的信息的
 */

public class ErrorActivity extends AbsActivity {

    public static void forward(String title, String errorInfo) {
        Intent intent = new Intent(CommonAppContext.sInstance, ErrorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("title", title);
        intent.putExtra("error", errorInfo);
        CommonAppContext.sInstance.startActivity(intent);
    }

    private TextView mTextView;
    private String mErrorInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_error;
    }

    @Override
    protected void main() {
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        mErrorInfo = intent.getStringExtra("error");
        setTitle(title);
        mTextView = (TextView) findViewById(R.id.text);
        mTextView.setText(mErrorInfo);
        findViewById(R.id.btn_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyError();
            }
        });
    }

    private void copyError() {
        if (TextUtils.isEmpty(mErrorInfo)) {
            return;
        }
        ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", mErrorInfo);
        clipboardManager.setPrimaryClip(clipData);
        ToastUtil.show(WordUtil.getString(R.string.copy_success));
    }
}
