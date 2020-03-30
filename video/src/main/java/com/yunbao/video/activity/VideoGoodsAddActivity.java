package com.yunbao.video.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.GoodsBean;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.JsonUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.video.R;
import com.yunbao.video.adapter.VideoGoodsAddAdapter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.yunbao.common.Constants.GOODS;

/**
 * Created by cxf on 2019/8/29.
 */

public class VideoGoodsAddActivity extends AbsActivity implements OnItemClickListener<GoodsBean> {
    public static final int SEL_GOODS=100;

    private EditText mEditText;
    private String mKey="";
    private CommonRefreshView mRefreshView;
    private CommonRefreshView mRefreshViewSearch;
    private VideoGoodsAddAdapter mVideoGoodsAddAdapter;
    private VideoGoodsAddAdapter mVideoGoodsAddAdapterSearch;
    private MyHandler mHandler;
    private GoodsBean mSelGoodsBean;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_goods_add;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.goods_tip_31));
        mSelGoodsBean=getIntent().getParcelableExtra(GOODS);
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

                if (mHandler != null) {
                    mHandler.removeCallbacksAndMessages(null);
                }
                if (!TextUtils.isEmpty(s)) {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(0, 500);
                    }
                } else {
                    mKey = null;
                    if (mVideoGoodsAddAdapterSearch != null) {
                        mVideoGoodsAddAdapterSearch.clearData();
                    }
                    if (mRefreshViewSearch.getVisibility() == View.VISIBLE) {
                        mRefreshViewSearch.setVisibility(View.INVISIBLE);
                        mVideoGoodsAddAdapter.setSelctGoodsBean(mSelGoodsBean);
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
                if (mVideoGoodsAddAdapter == null) {
                    mVideoGoodsAddAdapter = new VideoGoodsAddAdapter(mContext);
                    mVideoGoodsAddAdapter.setmOnItemClickListener(VideoGoodsAddActivity.this);
                }
                return mVideoGoodsAddAdapter;
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
                mVideoGoodsAddAdapter.setSelctGoodsBean(mSelGoodsBean);
            }
            @Override
            public void onRefreshFailure() {

            }
            @Override
            public void onLoadMoreSuccess(List<GoodsBean> loadItemList, int loadItemCount) {
                mVideoGoodsAddAdapter.setSelctGoodsBean(mSelGoodsBean);
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
                if (mVideoGoodsAddAdapterSearch == null) {
                    mVideoGoodsAddAdapterSearch = new VideoGoodsAddAdapter(mContext);
                    mVideoGoodsAddAdapterSearch.setmOnItemClickListener(VideoGoodsAddActivity.this);
                }
                return mVideoGoodsAddAdapterSearch;
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
                mVideoGoodsAddAdapterSearch.setSelctGoodsBean(mSelGoodsBean);
            }
            @Override
            public void onRefreshFailure() {
            }
            @Override
            public void onLoadMoreSuccess(List<GoodsBean> loadItemList, int loadItemCount) {
                mVideoGoodsAddAdapterSearch.setSelctGoodsBean(mSelGoodsBean);
            }
            @Override
            public void onLoadMoreFailure() {

            }
        });
        mHandler = new MyHandler(this);
        mRefreshView.initData();

    }

    private List<GoodsBean> parseGoodsData(String[] info) {
        if(info!=null&&info.length>0){
            return JsonUtil.getJsonToList(Arrays.toString(info),GoodsBean.class);
        }
        return new ArrayList<>(1);
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


    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.release();
        }
        mHandler = null;
        CommonHttpUtil.cancel(CommonHttpConsts.GET_GOODS_LIST);
        super.onDestroy();
    }


    /*两个adapter需要统一数据*/
    @Override
    public void onItemClick(GoodsBean bean, int position) {
        mSelGoodsBean=bean;
    }

    private static class MyHandler extends Handler {

        private VideoGoodsAddActivity mActivity;

        public MyHandler(VideoGoodsAddActivity activity) {
            mActivity = new WeakReference<>(activity).get();
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity != null) {
                mActivity.search();
            }
        }

        public void release() {
            mActivity = null;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=getIntent();
        if(mSelGoodsBean!=null){
          intent.putExtra(GOODS,mSelGoodsBean);
        }else{
            intent.removeExtra(GOODS);
        }
        setResult(RESULT_OK,intent);
        super.onBackPressed();
    }

}
