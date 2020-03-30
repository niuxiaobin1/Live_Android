package com.yunbao.live.views;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.yunbao.common.HtmlConfig;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.GoodsBean;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.JsonUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveGoodsAddActivity;
import com.yunbao.live.adapter.LiveGoodsAddAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LiveGoodsAddViewHolder extends AbsLivePageViewHolder {
    private EditText mEditText;
    private String mKey;
    private CommonRefreshView mRefreshView;
    private CommonRefreshView mRefreshViewSearch;
    private LiveGoodsAddAdapter mLiveGoodsAddAdapter;
    private LiveGoodsAddAdapter mLiveGoodsAddAdapterSearch;
    private MyHandler mHandler;


    public LiveGoodsAddViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
    }


    @Override
    public void init() {
        super.init();
        TextView textView=findViewById(R.id.titleView);
        textView.setText(WordUtil.getString(R.string.goods_tip_21));

        mEditText = findViewById(R.id.edit);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //MainHttpUtil.cancel(MainHttpConsts.SEARCH);
                if (mHandler != null) {
                    mHandler.removeCallbacksAndMessages(null);
                }
                if (!TextUtils.isEmpty(s)) {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(0, 500);
                    }
                } else {
                    mKey = null;
                    if (mLiveGoodsAddAdapterSearch != null) {
                        mLiveGoodsAddAdapterSearch.clearData();
                    }
                    if (mRefreshViewSearch.getVisibility() == View.VISIBLE) {
                        mRefreshViewSearch.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_shop_add);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<GoodsBean>() {
            @Override
            public RefreshAdapter<GoodsBean> getAdapter() {
                if (mLiveGoodsAddAdapter == null) {
                    mLiveGoodsAddAdapter = new LiveGoodsAddAdapter(mContext);
                }
                return mLiveGoodsAddAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                CommonHttpUtil.getGoodsList(p,"",callback);
            }
            @Override
            public List<GoodsBean> processData(String[] info) {
                return parseGoodsData(info);
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
        mRefreshViewSearch = findViewById(R.id.refreshView_search);
        mRefreshViewSearch.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshViewSearch.setDataHelper(new CommonRefreshView.DataHelper<GoodsBean>() {
            @Override
            public RefreshAdapter<GoodsBean> getAdapter() {
                if (mLiveGoodsAddAdapterSearch == null) {
                    mLiveGoodsAddAdapterSearch = new LiveGoodsAddAdapter(mContext);
                }
                return mLiveGoodsAddAdapterSearch;
            }
            @Override
            public void loadData(int p, HttpCallback callback) {
                CommonHttpUtil.getGoodsList(p,mKey,callback);
            }
            @Override
            public List<GoodsBean> processData(String[] info) {
                return parseGoodsData(info);
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
        mHandler = new MyHandler(this);
       // mRefreshView.initData();
    }


    private void search() {
        String key = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(key)) {
            ToastUtil.show(R.string.content_empty);
            return;
        }
//        MainHttpUtil.cancel(MainHttpConsts.SEARCH);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mKey = key;

        if (mRefreshViewSearch.getVisibility() != View.VISIBLE) {
            mRefreshViewSearch.setVisibility(View.VISIBLE);
        }
        mRefreshViewSearch.initData();
    }


    private List<GoodsBean> parseGoodsData(String[] info) {
        if(info!=null&&info.length>0){
            return JsonUtil.getJsonToList(Arrays.toString(info),GoodsBean.class);
        }
        return new ArrayList<>(1);
    }


    @Override
    public void loadData() {
        mRefreshView.initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_goods_add;
    }


    @Override
    public void hide() {
        super.hide();
        if(mLiveGoodsAddAdapter!=null){
            mLiveGoodsAddAdapter.clearData();
        }

        if(mLiveGoodsAddAdapterSearch!=null&&mLiveGoodsAddAdapterSearch.getRecyclerView()!=null){
            mLiveGoodsAddAdapterSearch.clearData();
        }

    }

    private static class MyHandler extends Handler {

        private LiveGoodsAddViewHolder mViewHolder;

        public MyHandler(LiveGoodsAddViewHolder viewHolder) {
            mViewHolder = new WeakReference<>(viewHolder).get();
        }

        @Override
        public void handleMessage(Message msg) {
            if (mViewHolder != null) {
                mViewHolder.search();
            }
        }

        public void release() {
            mViewHolder = null;
        }
    }
}
