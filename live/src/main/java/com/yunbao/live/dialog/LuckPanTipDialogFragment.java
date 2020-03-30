package com.yunbao.live.dialog;

import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.yunbao.common.HtmlConfig;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.live.R;

/**
 * Created by cxf on 2019/8/27.
 * 转盘规则说明
 */

public class LuckPanTipDialogFragment extends AbsDialogFragment implements View.OnClickListener {
    private WebView mWebView;
    private ViewGroup scrollContainer;
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_luck_pan_tip;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(300);
        params.height = DpUtil.dp2px(320);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        scrollContainer=findViewById(R.id.scroll_container);

        mWebView = new WebView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.topMargin = DpUtil.dp2px(1);
        mWebView.setLayoutParams(params);
        mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scrollContainer.addView(mWebView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.loadUrl(HtmlConfig.GAME_RULE);
    }
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_confirm) {
            dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            if (parent != null) {
                parent.removeView(mWebView);
            }
            mWebView.destroy();
        }
        super.onDestroy();
    }
}
