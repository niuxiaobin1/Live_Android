package com.yunbao.main.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.GoodsBean;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.IntentHelper;
import com.yunbao.common.utils.JsonUtil;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.ShopAdapter;
import com.yunbao.main.bean.StoreBean;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/8/30.
 * 店铺 商品详情页
 */

@Route(path = RouteUtil.PATH_GOODS)
public class ShopGoodsActivity extends AbsActivity implements OnItemClickListener<GoodsBean>, AppBarLayout.OnOffsetChangedListener {

    public static final int UNDER_CARRIAGE=-1;  //已下架
    public static final int ON_SHELVES=1;     //已上架
    public static final int UNDER_CARRIAGE_MANNGER=-2;     //管理上架

    private GoodsBean goodsBean;
    private boolean isSelfStore;

    private CommonRefreshView mRefreshView;
    private ShopAdapter mAdapter;
    private View mTitleView;
    private ImageView mThumb;
    private TextView mTitle;
    private TextView mTitle2;
    private TextView mPrice;
    private TextView mDes;
    private ImageView mShopThumb;
    private TextView mShopName;
    private TextView mShopGoodsCount;

    private float mRate;
    private TextView mBtnOff;
    private TextView mBtnDelete;

    private AppBarLayout appBarLayout;

    private String  mStoreUid;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_shop_goods;
    }





    private void banAppBarScroll(boolean isScroll){
        View mAppBarChildAt = appBarLayout.getChildAt(0);
        AppBarLayout.LayoutParams  mAppBarParams = (AppBarLayout.LayoutParams)mAppBarChildAt.getLayoutParams();
        if (isScroll) {
            mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
            mAppBarChildAt.setLayoutParams(mAppBarParams);
        } else {
            mAppBarParams.setScrollFlags(0);
        }

    }

    @Override
    protected void main() {
        appBarLayout=findViewById(R.id.appBarLayout);

        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(this);
        mTitleView = findViewById(R.id.title_view);
        mThumb = findViewById(R.id.thumb);
        mTitle = findViewById(R.id.title);
        mTitle2 = findViewById(R.id.title_2);
        mPrice = findViewById(R.id.price);
        mDes = findViewById(R.id.des);
        mShopThumb = findViewById(R.id.shop_thumb);
        mShopName = findViewById(R.id.shop_name);
        mShopGoodsCount = findViewById(R.id.shop_goods_count);
        mBtnOff= findViewById(R.id.btn_off);
        mBtnDelete= findViewById(R.id.btn_delete);
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 10, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);


        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<GoodsBean>() {
            @Override
            public RefreshAdapter<GoodsBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new ShopAdapter(mContext);
                    mAdapter.setOnItemClickListener(ShopGoodsActivity.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                if(goodsBean!=null)
                 MainHttpUtil.getRecommentGoods(goodsBean.getUid(),p,callback);
            }

            @Override
            public List<GoodsBean> processData(String[] info) {
                if(info!=null&&info.length>0){
                   return JsonUtil.getJsonToList(Arrays.toString(info),GoodsBean.class);
                }
                return new ArrayList<>(1);
            }
            @Override
            public void onRefreshSuccess(List<GoodsBean> list, int listCount) {
            }
            @Override
            public void onRefreshFailure() {

            }
            @Override
            public void onLoadMoreSuccess(List<GoodsBean> loadItemList, int loadItemCount) {

            }
            @Override
            public void onLoadMoreFailure() {

            }
        });

        goodsBean=getIntent().getParcelableExtra(Constants.GOODS);
        mStoreUid=getIntent().getStringExtra(Constants.UID);

        if(goodsBean==null){
            finish();
        }
        isSelfStore= CommonAppConfig.getInstance().getUid().equals(goodsBean.getUid());
        changeBottomButtonState();
        ImgLoader.display(this,goodsBean.getThumb(),mThumb);
        mTitle.setText(goodsBean.getName());
        mTitle2.setText(goodsBean.getName());
        mDes.setText(goodsBean.getDes());
        mPrice.setText(goodsBean.getHaveUnitPrice());

        requestStoreMessage();
        mRefreshView.initData();

    }

    private void requestStoreMessage() {
        if(goodsBean!=null&& !TextUtils.isEmpty(goodsBean.getUid())){
           MainHttpUtil.getShopInfo( goodsBean.getUid(), new HttpCallback() {
               @Override
               public void onSuccess(int code, String msg, String[] info) {
                   if(info!=null&&info.length>0){
                       StoreBean storeBean= JsonUtil.getJsonToBean(info[0],StoreBean.class);
                       storeBean.setNums(JsonUtil.getInt(info[0],"nums"));
                       layingStoreData(storeBean);
                   }
               }
           });
        }
    }

    private void layingStoreData(StoreBean storeBean) {
        mShopName.setText(storeBean.getName());
        mShopGoodsCount.setText(WordUtil.getString(R.string.goods_tip_17)+" "+storeBean.getNums());
        ImgLoader.display(this,storeBean.getThumb(),mShopThumb);
    }

    /*判断底部按钮状态*/
    private void changeBottomButtonState() {
        if(goodsBean==null){
            return;
        }else if(!isSelfStore){
            mBtnOff.setText(WordUtil.getString(R.string.goods_tip_29));
            mBtnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buyGoods();
                }
            });
            mBtnDelete.setVisibility(View.GONE);
            mBtnOff.setEnabled(true);
        }else if(isSelfStore&&(goodsBean.getStatus()==UNDER_CARRIAGE)){
            mBtnOff.setText(WordUtil.getString(R.string.goods_tip_33));
            mBtnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    grounding(true,v);
                }
            });
            mBtnDelete.setVisibility(View.VISIBLE);
            mBtnOff.setEnabled(true);
        }else if(isSelfStore&&(goodsBean.getStatus()==UNDER_CARRIAGE_MANNGER)){
            mBtnOff.setText(WordUtil.getString(R.string.goods_tip_33));
            mBtnOff.setEnabled(false);
            mBtnDelete.setVisibility(View.VISIBLE);
        }
        else{
            mBtnOff.setText(WordUtil.getString(R.string.goods_tip_30));
            mBtnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openUnGroundingDialog();
                }
            });
            mBtnOff.setEnabled(true);
            mBtnDelete.setVisibility(View.VISIBLE);
        }
    }


    private void openUnGroundingDialog() {
        new DialogUitl.Builder(mContext)
                .setTitle(" ")
                .setContent(WordUtil.getString(R.string.goods_tip_35))
                .setCancelable(true)
                .setCancelString(WordUtil.getString(R.string.cancel))
                .setConfrimString(WordUtil.getString(R.string.goods_tip_30))
                .setClickCallback(new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        grounding(false,null);
                    }
                })
                .build()
                .show();
    }

    /*上架 or 下架*/
    private void grounding(boolean isGrounding,final View view) {
            if(goodsBean==null){
               return;
            }
            final int upStatus=isGrounding?ON_SHELVES:UNDER_CARRIAGE;
            if(view!=null){
               view.setEnabled(false);
            }
           MainHttpUtil.upGoodsStatus(goodsBean.getId(), upStatus, new HttpCallback() {
               @Override
               public void onSuccess(int code, String msg, String[] info) {
                   if(code==0&&info.length>0){
                        int newStatus=JsonUtil.getInt(info[0],"status");
                        goodsBean.setStatus(newStatus);
                        changeBottomButtonState();
                   }
               }
               @Override
               public void onFinish() {
                   super.onFinish();
                   if(view!=null){
                       view.setEnabled(true);
                   }
               }
           });
    }


    private void buyGoods() {
        if(goodsBean!=null){
           IntentHelper.intentGoodsLinks(mContext,goodsBean);
        }
    }

    @Override
    public void onItemClick(GoodsBean bean, int position) {
        RouteUtil.forwardGoods(this,bean,null);
    }


    public void toStore(View view){
        if(goodsBean!=null){
            if(StringUtil.compareString(mStoreUid,goodsBean.getUid())){
               finish();
            }else{
                ShopActivity.forward(this,goodsBean.getUid());
            }

        }
    }

    public void openDelGoodsWindow(final View v){
        new DialogUitl.Builder(mContext)
                .setTitle(" ")
                .setContent(WordUtil.getString(R.string.goods_tip_34))
                .setCancelable(true)
                .setCancelString(WordUtil.getString(R.string.cancel))
                .setConfrimString(WordUtil.getString(R.string.delete))
                .setClickCallback(new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        delGoods();
                    }
                })
                .build()
                .show();
    }

    public void delGoods(){
        if(goodsBean!=null){
            MainHttpUtil.delGoods(goodsBean.getId(), new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                        ToastUtil.show(msg);
                        if(code==0){
                          finish();
                        }
                }
                   @Override
                   public void onFinish() {
                      super.onFinish();
                        }
                    }
            );
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        float totalScrollRange = appBarLayout.getTotalScrollRange();
        float rate = -1 * verticalOffset / totalScrollRange * 3;

        if (rate >= 1) {
            rate = 1;
        }
        if (mRate != rate) {
            mRate = rate;
            mTitleView.setAlpha(rate);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainHttpUtil.cancel(MainHttpConsts.GET_RECOMMENT);
        MainHttpUtil.cancel(MainHttpConsts.GET_SHOP_INFO);
        MainHttpUtil.cancel(MainHttpConsts.DEL_GOODS);
    }
}
