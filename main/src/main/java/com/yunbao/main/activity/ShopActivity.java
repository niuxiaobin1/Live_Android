package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.yunbao.common.utils.JsonUtil;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.ShopAdapter;
import com.yunbao.main.bean.StoreBean;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import java.util.List;

/**
 * Created by cxf on 2019/8/30.
 * 店铺
 */

public class ShopActivity extends AbsActivity implements OnItemClickListener<GoodsBean> {

    private TextView mTitle;
    private ImageView mThumb;
    private TextView mDes;
    private CommonRefreshView mRefreshView;
    private ShopAdapter mAdapter;
    private String lookUid;
    private View mBtnAdd;
    private ImageView mImgBg;
    private TextView mGoodsCount;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_shop;
    }
    @Override
    protected void main() {
        mTitle = findViewById(R.id.title);
        mThumb = findViewById(R.id.thumb);
        mDes = findViewById(R.id.des);
        mRefreshView = findViewById(R.id.refreshView);
        mBtnAdd= findViewById(R.id.btn_add);
        mImgBg= findViewById(R.id.img_bg);
        mGoodsCount= findViewById(R.id.goods_count);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 10, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        lookUid=getIntent().getStringExtra(Constants.UID);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<GoodsBean>() {
            @Override
            public RefreshAdapter<GoodsBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new ShopAdapter(mContext);
                    mAdapter.setOnItemClickListener(ShopActivity.this);
                }
                return mAdapter;
            }
            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getShop(p,lookUid,callback);
            }
            @Override
            public List<GoodsBean> processData(String[] info) {
                if(info!=null&&info.length>0){
                   StoreBean storeBean= JsonUtil.getJsonToBean(JsonUtil.getString(info[0],"shopinfo"),StoreBean.class);
                   storeBean.setNums(JsonUtil.getInt(info[0],"nums"));
                   layingData(storeBean);
                   return JsonUtil.getJsonToList(JsonUtil.getString(info[0],"list"),GoodsBean.class);
                }
                return null;
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
    }
    @Override
    protected void onStart() {
        super.onStart();
        mRefreshView.initData();
    }
    public void addGoods(View view){
        startActivity(GoodsAddActivity.class);
    }
    private void layingData(StoreBean storeBean) {
        mTitle.setText(storeBean.getName());
        mDes.setText(storeBean.getDes());
        String thumb=storeBean.getThumb();
        ImgLoader.display(this,thumb,mThumb);
        ImgLoader.displayBlur(this,thumb,mImgBg);
        mGoodsCount.setText(WordUtil.getString(R.string.goods_tip_20)+storeBean.getNums());
        String uid=storeBean.getUid();
        if(!TextUtils.isEmpty(uid)&&uid.equals(CommonAppConfig.getInstance().getUid())){
            mBtnAdd.setVisibility(View.VISIBLE);
        }else {
            mBtnAdd.setVisibility(View.INVISIBLE);
        }
    }
    public static void forward(Context context,String toUid){
        Intent intent=new Intent(context,ShopActivity.class);
        intent.putExtra(Constants.UID,toUid);
        context.startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainHttpUtil.cancel(MainHttpConsts.GET_SHOP);
    }
    @Override
    public void onItemClick(GoodsBean bean, int position) {
        RouteUtil.forwardGoods(this,bean,lookUid);
    }
}
