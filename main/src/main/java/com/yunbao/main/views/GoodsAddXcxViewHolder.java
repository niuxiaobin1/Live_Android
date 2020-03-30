package com.yunbao.main.views;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.bean.GoodsBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.JsonUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.live.views.AbsCommonViewHolder;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpUtil;

import java.util.Arrays;

/**
 * Created by cxf on 2019/8/29.
 * 添加 小程序商品
 */

public class GoodsAddXcxViewHolder extends AbsCommonViewHolder implements View.OnClickListener {
    private TextView mTvIdval;
    private TextView mLink;
    private TextView mName;
    private TextView mPriceOrigin;
    private TextView mPriceNow;
    private TextView mDes;
    private View mBtnImgDel;
    private ImageView mImg;
    private View mBtnConfirm;

    public GoodsAddXcxViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_goods_add_xcx;
    }

    @Override
    public void init() {
        mTvIdval=findViewById(R.id.id_val);
        mLink =  findViewById(R.id.link);
        mName = findViewById(R.id.name);
        mPriceOrigin =  findViewById(R.id.price_origin);
        mPriceNow = findViewById(R.id.price_now);
        mDes =findViewById(R.id.des);
        mImg = findViewById(R.id.img);
        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(this);

        findViewById(R.id.btn_goods_info).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      int id=v.getId();
      if(id==R.id.btn_goods_info){
          requstGoodsByXcx(v);
      } else if (id == R.id.btn_confirm) {
          confirm(v);
      }
    }

    private void confirm(View v) {
        if(mGoodsBean!=null){
            v.setEnabled(false);
            setGoods(v,mGoodsBean);
        }

    }

    private void requstGoodsByXcx(final View v) {
        String goodsId=mTvIdval.getText().toString();
        if(TextUtils.isEmpty(goodsId)){
            ToastUtil.show(mContext.getString(R.string.goods_tip_36));
            return;
        }

        v.setEnabled(false);
        MainHttpUtil.getApplets(goodsId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                ToastUtil.show(msg);
                if(code==0&&info.length>0){
                   GoodsBean goodsBean= JsonUtil.getJsonToList(Arrays.toString(info), GoodsBean.class).get(0);
                   layingData(goodsBean);
                }
            }
            @Override
            public void onFinish() {
                super.onFinish();
                v.setEnabled(true);
            }
        });
    }


    private GoodsBean mGoodsBean;
    private void layingData(GoodsBean goodsBean) {
        if(goodsBean==null)
            return;

        mBtnConfirm.setEnabled(true);
        mGoodsBean=goodsBean;
        mName.setText(goodsBean.getName());
        mPriceOrigin.setText(goodsBean.getHaveUnitmOriginPrice());
        mPriceNow.setText(goodsBean.getHaveUnitPrice());
        mDes.setText(goodsBean.getDes());
        ImgLoader.display(mContext,goodsBean.getThumb(),mImg);
    }


    private void setGoods(final View view,GoodsBean goodsBean) {
        MainHttpUtil.setGoods(1, goodsBean, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        ToastUtil.show(msg);
                        if(code==0){
                            finishAcitivty();
                        }
                    }
                    @Override
                    public void onError() {
                        super.onError();
                        view.setEnabled(true);
                    }
                }
        );
    }
}
