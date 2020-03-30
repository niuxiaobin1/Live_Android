package com.yunbao.main.views;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yunbao.common.Constants;
import com.yunbao.common.mob.MobShareUtil;
import com.yunbao.main.R;

import razerdp.basepopup.BasePopupWindow;

public class SharePopupWindow extends BasePopupWindow implements View.OnClickListener {

    private TextView qqShareTv;
    private TextView wxShareTv;
    private TextView cancelTv;


    public SharePopupWindow(Context context) {
        super(context);
        qqShareTv = findViewById(R.id.qqShareTv);
        wxShareTv = findViewById(R.id.wxShareTv);
        cancelTv = findViewById(R.id.cancelTv);
        cancelTv.setOnClickListener(this);
        wxShareTv.setOnClickListener(this);
        qqShareTv.setOnClickListener(this);


        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        qqShareTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onShareAction!=null){
                    onShareAction.share(Constants.MOB_QQ);
                }
            }
        });
        wxShareTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onShareAction!=null){
                    onShareAction.share(Constants.MOB_WX);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

    }



    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.share_popup);
    }


    public interface OnShareAction {
        void share(String type);
    }

    private OnShareAction onShareAction;

    public void setOnShareAction(OnShareAction onShareAction) {
        this.onShareAction = onShareAction;
    }
}
