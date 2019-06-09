package cn.zerone.water.activity;

import android.content.Intent;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.zerone.water.R;
import cn.zerone.water.adapter.SelectionMemberAdapter;
import cn.zerone.water.http.Requests;
import cn.zerone.water.model.UserMode;
import cn.zerone.water.presenter.SelectionMembersPresenter;
import cn.zerone.water.utils.HttpUtil;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class CreateLiveActivity extends AppCompatActivity implements View.OnClickListener, SelectionMembersPresenter.SelectionMemberView {
    private ImageView mImageViewBack;
    private TextView mTextViewConfirm;
    private TextView mTexViewTitle;
    private SelectionMembersPresenter selectionMembersPresenter;
    RecyclerView mRecycleView;
    private SelectionMemberAdapter mAdapter;
    private List<SelectionMemberAdapter.Item> items;

   private List<String> selectedMemberIds=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_live);
        mImageViewBack = this.findViewById(R.id.iv_title_left);
        mTextViewConfirm = this.findViewById(R.id.tv_title_right);
        mTexViewTitle = this.findViewById(R.id.tv_title);
        mTexViewTitle.setText("选择成员");
        mTextViewConfirm.setText("确定");
        mRecycleView = this.findViewById(R.id.recycler_view_create_live);
        selectionMembersPresenter = new SelectionMembersPresenter(this);
        selectionMembersPresenter.Selection();
        mImageViewBack.setOnClickListener(this);
        mTextViewConfirm.setOnClickListener(this);
        selectedMemberIds.clear();
        initData();
    }

    private void initData() {
        mAdapter = new SelectionMemberAdapter();
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setNestedScrollingEnabled(false);
        mRecycleView.setFocusable(false);
        mRecycleView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title_left:
                finish();
                break;
            case R.id.tv_title_right:
             Intent intent=new Intent();
             intent.setClass(this,LiveMemberInfoActivity.class);
             startActivity(intent);


//                Requests.createRoom(new Observer<JSONObject>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(JSONObject jsonObject) {
//                      Log.e("roomjson-->",jsonObject+"");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                }, "identity_id","identity_pwd","直播","start_time",
//                        "end_time",2,20,0,0);
 /*
                 String identity_id, String identity_pwd,
                                  String title,String start_time,String  end_time,int type,int max_users,
                                  int pre_enter_time,int is_long_term
                 */
                break;
        }
    }


    @Override
    public void onSelectionSuccess(ArrayList<UserMode> userModes) {
        if (userModes == null || userModes.size() == 0) {

            return;
        }
        transformData(userModes);
        mAdapter.setNewData(items);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if ( mAdapter.getData().get(position).isChecked){
                    mAdapter.getData().get(position).isChecked=false;
                    if (selectedMemberIds!=null){
                        selectedMemberIds.remove(mAdapter.getData().get(position).id);
                    }
                }else {
                    mAdapter.getData().get(position).isChecked=true;
                    selectedMemberIds.add(mAdapter.getData().get(position).id);
                }
                mAdapter.notifyDataSetChanged();

            }
        });
    }
    public static final String ADVANCED_URL = "http://47.105.187.185:8011";

    private void transformData(ArrayList<UserMode> userModes) {
        if (userModes == null || userModes.size() <= 0) {
            return;
        }
        items = new ArrayList<>();
        for (UserMode item : userModes) {
            SelectionMemberAdapter.Item item_ = new SelectionMemberAdapter.Item();
            item_.photo = ADVANCED_URL+item.getPhoto();
            Log.e("phont-->",item_.photo);
            item_.id = item.getId()+"";
            item_.name = item.getName();
            items.add(item_);
        }
    }

    @Override
    public void onFailure() {

    }
}
