package com.yunbao.main.activity;

import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.bean.ImpressBean;
import com.yunbao.live.custom.MyTextView;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/10/15.
 * 我收到的主播印象
 */

public class MyImpressActivity extends AbsActivity {

    private LinearLayout mGroup;
    private TextView mTip;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_impress;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.impress));
        mGroup = (LinearLayout) findViewById(R.id.group);
        mTip = (TextView) findViewById(R.id.tip);
        MainHttpUtil.getMyImpress(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (info.length > 0) {
                        List<ImpressBean> list = JSON.parseArray(Arrays.toString(info), ImpressBean.class);
                        int line = 0;
                        int fromIndex = 0;
                        boolean hasNext = true;
                        LayoutInflater inflater = LayoutInflater.from(mContext);
                        while (hasNext) {
                            LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.view_impress_line, mGroup, false);
                            int endIndex = line % 2 == 0 ? fromIndex + 3 : fromIndex + 2;
                            if (endIndex >= list.size()) {
                                endIndex = list.size();
                                hasNext = false;
                            }
                            for (int i = fromIndex; i < endIndex; i++) {
                                MyTextView item = (MyTextView) inflater.inflate(R.layout.view_impress_item, linearLayout, false);
                                ImpressBean bean = list.get(i);
                                bean.setCheck(1);
                                item.setBean(bean, true);
                                linearLayout.addView(item);
                            }
                            fromIndex = endIndex;
                            line++;
                            mGroup.addView(linearLayout);
                        }
                    } else {
                        mTip.setText(WordUtil.getString(R.string.impress_tip_3));
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_MY_IMPRESS);
        super.onDestroy();
    }
}
