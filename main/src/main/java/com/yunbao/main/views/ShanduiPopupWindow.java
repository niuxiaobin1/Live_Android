package com.yunbao.main.views;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunbao.common.utils.ToastUtil;
import com.yunbao.main.R;

import razerdp.basepopup.BasePopupWindow;

public class ShanduiPopupWindow extends BasePopupWindow implements View.OnClickListener {

    private EditText edit_input;
    private TextView biliTv;
    private TextView doneTv;
    private CheckBox checkBox;

    public ShanduiPopupWindow(Context context, String biliAidou, String biliDstt, String type) {
        super(context);

        doneTv = findViewById(R.id.doneTv);
        biliTv = findViewById(R.id.biliTv);
        edit_input = findViewById(R.id.edit_input);
        checkBox = findViewById(R.id.checkBox);

        if ("1".equals(type)) {
            edit_input.setHint("请输入爱豆数量");
            biliTv.setText("兑换比例 " + biliAidou + ":" + biliDstt);
            checkBox.setText("确认兑换爱豆吗？");
        } else {
            edit_input.setHint("请输入DSTT数量");
            biliTv.setText("兑换比例 " + biliDstt + ":" + biliAidou);
            checkBox.setText("确认兑换DSTT吗？");
        }

        doneTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkBox.isChecked()) {
                    ToastUtil.show("请先勾选下方提示");
                    return;
                }
                String num = edit_input.getText().toString().trim();
                if (!TextUtils.isEmpty(num)) {
                    if (onClickAction != null) {
                        onClickAction.onClick(num);
                        dismiss();
                    }
                }else{
                    ToastUtil.show("请先输入兑换的数量");
                }
            }
        });

    }

    @Override
    public void onClick(View view) {

    }


    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.popup_shandui);
    }


    public interface OnClickAction {
        void onClick(String num);
    }

    private OnClickAction onClickAction;

    public void setOnClickAction(OnClickAction onClickAction) {
        this.onClickAction = onClickAction;
    }
}
