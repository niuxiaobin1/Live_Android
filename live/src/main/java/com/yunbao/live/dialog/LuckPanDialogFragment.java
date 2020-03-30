package com.yunbao.live.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.live.bean.TurntableConfigBean;
import com.yunbao.live.bean.TurntableGiftBean;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.JsonUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveActivity;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/8/27.
 * 抽奖转盘
 */

public class LuckPanDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private ImageView[] mPrizeIcons;
    private TextView[] mPrizeNames;

    private TextView[] mTurnTableBtnTimes;
    private TextView[] mTurnTableBtnPrices;

    private View mPan;
    private ObjectAnimator mAnimator;

    private List<TurntableConfigBean> mTurntableConfigBeanList;
    private TurntableConfigBean mSelTurnTableBean;
    private List<TurntableGiftBean> mTurntableGiftBeanList;
    private List<TurntableGiftBean> winResultGiftBeanList;




    @Override
    protected int getLayoutId() {
        return R.layout.dialog_luck_pan;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPan = findViewById(R.id.pan);
        mPrizeIcons = new ImageView[8];
        mPrizeNames = new TextView[8];
        mPrizeIcons[0] = (ImageView) findViewById(R.id.img_0);
        mPrizeIcons[1] = (ImageView) findViewById(R.id.img_1);
        mPrizeIcons[2] = (ImageView) findViewById(R.id.img_2);
        mPrizeIcons[3] = (ImageView) findViewById(R.id.img_3);
        mPrizeIcons[4] = (ImageView) findViewById(R.id.img_4);
        mPrizeIcons[5] = (ImageView) findViewById(R.id.img_5);
        mPrizeIcons[6] = (ImageView) findViewById(R.id.img_6);
        mPrizeIcons[7] = (ImageView) findViewById(R.id.img_7);
        mPrizeNames[0] = (TextView) findViewById(R.id.name_0);
        mPrizeNames[1] = (TextView) findViewById(R.id.name_1);
        mPrizeNames[2] = (TextView) findViewById(R.id.name_2);
        mPrizeNames[3] = (TextView) findViewById(R.id.name_3);
        mPrizeNames[4] = (TextView) findViewById(R.id.name_4);
        mPrizeNames[5] = (TextView) findViewById(R.id.name_5);
        mPrizeNames[6] = (TextView) findViewById(R.id.name_6);
        mPrizeNames[7] = (TextView) findViewById(R.id.name_7);

        mTurnTableBtnTimes=new TextView[3];
        mTurnTableBtnPrices=new TextView[3];

        mTurnTableBtnTimes[0]=findViewById(R.id.tv_turn_time1);
        mTurnTableBtnTimes[1]=findViewById(R.id.tv_turn_time2);
        mTurnTableBtnTimes[2]=findViewById(R.id.tv_turn_time3);


        mTurnTableBtnPrices[0]=findViewById(R.id.tv_turn_coin1);
        mTurnTableBtnPrices[1]=findViewById(R.id.tv_turn_coin2);
        mTurnTableBtnPrices[2]=findViewById(R.id.tv_turn_coin3);


        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_one).setOnClickListener(this);
        findViewById(R.id.btn_ten).setOnClickListener(this);
        findViewById(R.id.btn_hundred).setOnClickListener(this);
        findViewById(R.id.btn_game_rule).setOnClickListener(this);
        findViewById(R.id.btn_win_record).setOnClickListener(this);


        mAnimator = ObjectAnimator.ofFloat(mPan, "rotation", 0);
        mAnimator.setDuration(3000);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mContext != null) {
                    ((LiveActivity) mContext).openLuckPanWinWindow(winResultGiftBeanList);
                }
            }
        });

        requestPanGiftList();
    }


    private void requestPanGiftList() {
        LiveHttpUtil.getTurntable(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(code==0&& info.length > 0){
                    String jsonData=info[0];
                    mTurntableConfigBeanList= JsonUtil.getJsonToList(JsonUtil.getString(jsonData,"config"),TurntableConfigBean.class);
                    mTurntableGiftBeanList= JsonUtil.getJsonToList(JsonUtil.getString(jsonData,"list"),TurntableGiftBean.class);
                    layingData(mTurntableConfigBeanList,mTurntableGiftBeanList);
                    selTurnConfigBean(0);
                }
            }
        });
    }

    private void layingData(List<TurntableConfigBean> mTurntableConfigBeanList, List<TurntableGiftBean> turntableGiftBeanList) {
            /*转盘发起按钮数据*/
            if(mTurntableConfigBeanList!=null){
                String coinName= CommonAppConfig.getInstance().getCoinName();
                int widetSize=mTurnTableBtnTimes.length;
                int dataSize=mTurntableConfigBeanList.size();
                for(int i=0;i<widetSize;i++){
                    if(dataSize==i){
                        break;
                    }
                    TurntableConfigBean turntableConfigBean=mTurntableConfigBeanList.get(i);
                    mTurnTableBtnTimes[i].setText(WordUtil.getString(R.string.pan_turn_times,turntableConfigBean.getTimes()));
                    mTurnTableBtnPrices[i].setText(turntableConfigBean.getCoin()+ coinName);

                }
            }

            /*转盘数据*/
            if(turntableGiftBeanList!=null){
                int widetSize=mPrizeIcons.length;
                int dataSize=turntableGiftBeanList.size();
                for(int i=0;i<widetSize;i++){
                    if(dataSize==i){
                        break;
                    }
                    ImageView imgPriceIcon=mPrizeIcons[i];
                    TextView tvPriceName=mPrizeNames[i];
                    TurntableGiftBean turntableGiftBean=turntableGiftBeanList.get(i);

                    if(turntableGiftBean.getType()==1||turntableGiftBean.getType()==0){
                        tvPriceName.setText(turntableGiftBean.getType_val());
                    }


                    ImgLoader.display(mContext,turntableGiftBean.getThumb(),imgPriceIcon);
                }
            }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_start) {
            turnTable(v);
        } else if (i == R.id.btn_one) {
            selTurnConfigBean(0);
        } else if (i == R.id.btn_ten) {
            selTurnConfigBean(1);
        } else if (i == R.id.btn_hundred) {
            selTurnConfigBean(2);
        } else if (i == R.id.btn_game_rule) {
            if(mContext!=null){
                ((LiveActivity)mContext).openLuckPanTipWindow();
            }
        } else if (i == R.id.btn_win_record) {
            if(mContext!=null){
                ((LiveActivity)mContext).openLuckPanRecordWindow();
            }
        }
    }


    //抽奖
    private void turnTable(final View view) {
         if(mContext instanceof LiveActivity){
             LiveActivity liveActivity= (LiveActivity) mContext;
             String liveUid=liveActivity.getLiveUid();
             String stream=liveActivity.getStream();
             if(mSelTurnTableBean!=null&&mTurntableGiftBeanList!=null){
                 view.setEnabled(false);
                 getDialog().setCancelable(false);
                 LiveHttpUtil.turn(mSelTurnTableBean.getId(),liveUid,stream, new HttpCallback() {
                             @Override
                             public void onSuccess(int code, String msg, String[] info) {
                                 if(code==0&&info.length>0){
                                     String listStr= JsonUtil.getString(info[0],"list");
                                     List<TurntableGiftBean>luckPanBeanList=JsonUtil.getJsonToList(listStr  ,TurntableGiftBean.class);
                                     setResult(luckPanBeanList);
                                 }else{
                                     ToastUtil.show(msg);
                                 }
                             }
                             @Override
                             public void onFinish() {
                                 view.setEnabled(true);
                                 getDialog().setCancelable(true);
                                 super.onFinish();

                             }
                         }
                 );
             }
         }

    }


    private void setResult(List<TurntableGiftBean> luckPanBeanList) {
        this.winResultGiftBeanList=luckPanBeanList;
        if(luckPanBeanList!=null&&luckPanBeanList.size()>0){
            TurntableGiftBean lastResultBean=  luckPanBeanList.get(luckPanBeanList.size()-1);
            int index=mTurntableGiftBeanList.indexOf(lastResultBean);
            rotate(index);
        }else{
            int size=mTurntableGiftBeanList.size();
            for(int i=0;i<size;i++){
                if(mTurntableGiftBeanList.get(i).getType()==0){
                    rotate(i);
                    break;
                }
            }

        }
    }


    private void selTurnConfigBean(int i) {
        if(mTurntableConfigBeanList!=null&&mTurntableConfigBeanList.size()>i){
            mSelTurnTableBean=mTurntableConfigBeanList.get(i);
        }
    }




    private void rotate(int index) {
        if(index<0||index>7){
            L.e("index is outBound");
            return;
        }
        L.e("rotate-----index------> " + index);
        float targetAngle = 3960 - (45 * index + 22.5f);
        float rotation = mPan.getRotation() % 360;
        mAnimator.setFloatValues(rotation, targetAngle);
        mAnimator.start();
    }


    @Override
    public void onDestroy() {
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator.removeAllListeners();
            mAnimator.removeAllUpdateListeners();
        }
        LiveHttpUtil.cancel(LiveHttpConsts.GET_TURN_TABLE);
        LiveHttpUtil.cancel(LiveHttpConsts.TURN);
        mAnimator = null;
        super.onDestroy();
    }
}
