package cn.zerone.water.fragment;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.activity.ApproveActivity;
import cn.zerone.water.activity.CheckDetailsActivity;
import cn.zerone.water.activity.CheckedInfoActivity;
import cn.zerone.water.adapter.ApproveAdapter;
import cn.zerone.water.adapter.ApproveItem;
import cn.zerone.water.http.Requests;
import cn.zerone.water.utils.ImageUtil;
import io.reactivex.Observer;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;

import android.content.Context;
import android.widget.AdapterView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class CheckRejectedFragment extends Fragment {
    private RecyclerView recyclerView;
    private ApproveAdapter mAdapter;
    private List<ApproveItem> mMyItemList_rejected;
    private Handler mHandler;
    private SwipeRefreshLayout mSwipe;
    private int mRejected_PageIndex = 1;
    private int mCurrState = 2;
    private final int mPageSize = 10;
    private DividerItemDecoration mDivider;//分隔线

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reject, container, false);
        init(view);

        //首次预先加载
        UpdateAdapter(mMyItemList_rejected);
        getDatas(mRejected_PageIndex);
        return view;
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.rv_reject);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //初始化分隔线、添加分隔线
        //mDivider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        //recyclerView.addItemDecoration(mDivider);
        mMyItemList_rejected = new ArrayList<>();
        //mAdapter = new ApproveAdapter(mylist);
        //recyclerView.setAdapter(mAdapter);

        mSwipe = (SwipeRefreshLayout) view.findViewById(R.id.srl_reject);
        /*
         * 设置进度条的颜色
         * 参数是一个可变参数、可以填多个颜色
         */
        mSwipe.setColorSchemeColors(Color.parseColor("#d7a101"),Color.parseColor("#54c745"),
                Color.parseColor("#f16161"),Color.BLUE,Color.YELLOW);

        /*
         * 设置下拉刷新的监听
         */
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //Thread.sleep(5000);
                            mRejected_PageIndex++;
                            getDatas(mRejected_PageIndex);

                            //mHandler.sendEmptyMessage(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        // 初始化Handler
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    // 在ListView中更新数据
                    mAdapter.setData(mMyItemList_rejected);
                    mAdapter.notifyDataSetChanged();
                    // 取消SwipeRefreshLayout的刷新状态
                    mSwipe.setRefreshing(false);
                }
                //如果没有新数据，页码回退
                else if(msg.what == 2){
                    // 取消SwipeRefreshLayout的刷新状态
                    mSwipe.setRefreshing(false);
                    Toast.makeText(getContext(), "没有更多数据", Toast.LENGTH_LONG).show();
                    if(mRejected_PageIndex > 1)
                        mRejected_PageIndex--;


                }
            }
        };

    }

    private void UpdateAdapter(List<ApproveItem> mylist) {

        mAdapter = new ApproveAdapter(mylist);
        recyclerView.setAdapter(mAdapter);

        // 设置item及item中控件的点击事件
        mAdapter.setOnItemClickListener(MyItemClickListener);


    }

    /**
     * item＋item里的控件点击监听事件
     */
    private ApproveAdapter.OnItemClickListener MyItemClickListener = new ApproveAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(View v, ApproveAdapter.ViewName viewName, int position) {
            //viewName可区分item及item内部控件
            switch (v.getId()){
                case R.id.item_go:
                    ApproveItem item = mMyItemList_rejected.get(position);
                    int status = item.getItemStatus();
                    if(status == 2){
                        Bundle bundle = new Bundle();
                        bundle.putString("Url", item.getWebUrl());
                        bundle.putInt("checkID", item.getCheckID());
                        bundle.putString("CheckName", item.getItemAbstruct());

                        Intent intent = new Intent();
                        intent.putExtras(bundle);
                        intent.setClass(getContext(), CheckedInfoActivity.class);
                        //getContext().startActivity(intent);
                        startActivity(intent);
                    }
                    break;
                default:
                    ApproveItem item1 = mMyItemList_rejected.get(position);
                    int status1 = item1.getItemStatus();
                    if(status1 == 2){
                        Bundle bundle = new Bundle();
                        bundle.putString("Url", item1.getWebUrl());
                        bundle.putInt("checkID", item1.getCheckID());
                        bundle.putString("CheckName", item1.getItemAbstruct());

                        Intent intent = new Intent();
                        intent.putExtras(bundle);
                        intent.setClass(getContext(), CheckedInfoActivity.class);
                        //getContext().startActivity(intent);
                        startActivity(intent);
                    }
                    break;
            }
        }

        @Override
        public void onItemLongClick(View v) {

        }
    };


    public void getDatas(int pindex){

        final ApproveItem myItem = new ApproveItem();

        Requests.GetCheckInfo(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(JSONObject jsonObject) {

                int  count = jsonObject.getIntValue("Count");
                if(count != 0){

                    JSONArray jsonArray = jsonObject.getJSONArray("ModelList");
                    for(int i = 0; i<jsonArray.size(); i++){

                        JSONObject   subObject  =  jsonArray.getJSONObject(i) ;
                        String ApplyUserIdName = subObject.getString("ApplyUserIdName");
                        String ApplyUserId_Photo = subObject.getString("ApplyUserId_Photo");
                        String CheckName = subObject.getString("CheckName");
                        int  CheckID = subObject.getIntValue("ID");
                        String  subUrl = subObject.getString("Url");
                        String AddTime = subObject.getString("AddTime");

                        String photo_url = App.mPreUrl + ApplyUserId_Photo;
                        ImageUtil imageUtil = ImageUtil.getInstance();
                        Bitmap temp_bitmap = ImageUtil.getBitMBitmap(photo_url);
                        Bitmap bitmap = imageUtil.comp(temp_bitmap);

                        //String realTime = AddTime.substring(6,19);
                        int lowIndex = AddTime.indexOf("(");
                        int highIndex = AddTime.indexOf(")");
                        String realTime = AddTime.substring(lowIndex + 1,highIndex);
                        Long longtime = Long.parseLong(realTime);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                        String d = format.format(longtime);

                        String web_url = App.mPreUrl + subUrl;

                        myItem.setItemIcon(bitmap);
                        myItem.setItemAbstruct(CheckName);
                        myItem.setItemApplyUser(ApplyUserIdName);
                        myItem.setItemTime(d);
                        myItem.setWebUrl(web_url);
                        myItem.setCheckID(CheckID);
                        myItem.setItemStatus(mCurrState);

                        mMyItemList_rejected.add(myItem);

                    }
                    mHandler.sendEmptyMessage(1);

                }
                else{
                    mHandler.sendEmptyMessage(2);
                }


                //UpdateAdapter(mMyItemList);
                //mHandler.sendEmptyMessage(1);

            }
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
            @Override
            public void onComplete() {
            }
        }, App.userId, mCurrState, pindex, mPageSize);

    }

}
