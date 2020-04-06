package com.yunbao.main.views;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunbao.common.Constants;
import com.yunbao.main.R;

import razerdp.basepopup.BasePopupWindow;

public class ZhangdanPopupWindow extends BasePopupWindow implements View.OnClickListener {

    private LinearLayout contentLl;


    public ZhangdanPopupWindow(Context context) {
        super(context);
        contentLl = findViewById(R.id.contentLl);
        for (int i = 0; i < contentLl.getChildCount(); i++) {
            final int p = i;
            final TextView tv = (TextView) contentLl.getChildAt(i);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onSelectAction != null) {
                        onSelectAction.onSelect(String.valueOf(p), tv.getText().toString().toLowerCase());
                    }
                    dismiss();
                }
            });
        }

    }

    @Override
    public void onClick(View view) {

    }


    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.zhangdan_popup);
    }


    public interface OnSelectAction {
        void onSelect(String type, String text);
    }

    private OnSelectAction onSelectAction;

    public void setOnSelectAction(OnSelectAction onSelectAction) {
        this.onSelectAction = onSelectAction;
    }
}
