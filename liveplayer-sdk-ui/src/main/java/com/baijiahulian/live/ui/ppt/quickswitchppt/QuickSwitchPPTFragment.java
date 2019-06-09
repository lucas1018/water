package com.baijiahulian.live.ui.ppt.quickswitchppt;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baijiahulian.live.ui.R;
import com.baijiahulian.live.ui.base.BaseDialogFragment;
import com.baijiahulian.live.ui.utils.AliCloudImageUtil;
import com.baijiahulian.live.ui.utils.QueryPlus;
import com.baijiahulian.livecore.viewmodels.impl.LPDocListViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by szw on 17/7/4.
 */

public class QuickSwitchPPTFragment extends BaseDialogFragment implements SwitchPPTContract.View {
    private SwitchPPTContract.Presenter presenter;
    private QueryPlus $;
    private List<LPDocListViewModel.DocModel> quickDocList = new ArrayList<>();
    private List<LPDocListViewModel.DocModel> docModelList = new ArrayList<>();
    private QuickSwitchPPTAdapter adapter;
    private boolean isStudent = false;
    private int maxIndex;//学生可以快速滑动ppt的最大页数
    private int currentIndex;

    public static QuickSwitchPPTFragment newInstance() {
        Bundle args = new Bundle();
        QuickSwitchPPTFragment quickSwitchPPTFragment = new QuickSwitchPPTFragment();
        quickSwitchPPTFragment.setArguments(args);
        return quickSwitchPPTFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_ppt_switch;
    }

    @Override
    protected void init(Bundle savedInstanceState, Bundle arguments) {
        super.hideTitleBar();
        $ = QueryPlus.with(contentView);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        adapter = new QuickSwitchPPTAdapter();
        ((RecyclerView) $.id(R.id.dialog_switch_ppt_rv).view()).setLayoutManager(manager);
        ((RecyclerView) $.id(R.id.dialog_switch_ppt_rv).view()).setAdapter(adapter);
        this.maxIndex = getArguments().getInt("maxIndex");
        this.currentIndex = getArguments().getInt("currentIndex");
    }

    @Override
    protected void setWindowParams(WindowManager.LayoutParams windowParams) {
        windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        windowParams.gravity = Gravity.BOTTOM | GravityCompat.END;
        windowParams.windowAnimations = R.style.LiveBaseSendMsgDialogAnim;
    }

    @Override
    public void setPresenter(SwitchPPTContract.Presenter presenter) {
        this.presenter = presenter;
        super.setBasePresenter(presenter);
    }

    @Override
    public void setIndex() {
        ((RecyclerView) $.id(R.id.dialog_switch_ppt_rv).view()).smoothScrollToPosition(currentIndex);
    }

    @Override
    public void setMaxIndex(int updateMaxIndex) {
        this.maxIndex = updateMaxIndex;
        this.quickDocList.clear();
        if (isStudent) {
            quickDocList.addAll(docModelList.subList(0, maxIndex + 1));
        }
        ((RecyclerView) $.id(R.id.dialog_switch_ppt_rv).view()).smoothScrollToPosition(maxIndex);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setType(boolean isStudent) {
        this.isStudent = isStudent;
    }

    @Override
    public void docListChanged(List<LPDocListViewModel.DocModel> docModelList) {
        this.quickDocList.clear();
        this.docModelList.clear();
        this.docModelList.addAll(docModelList);
        if (isStudent) {
            if (maxIndex > docModelList.size())
                quickDocList.addAll(docModelList);
            else{
                quickDocList.addAll(docModelList.subList(0, maxIndex + 1));
            }
        } else {
            quickDocList.addAll(docModelList);
        }
        adapter.notifyDataSetChanged();
    }

    private class QuickSwitchPPTAdapter extends RecyclerView.Adapter<SwitchHolder> {

        @Override
        public SwitchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SwitchHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_switch_ppt, parent, false));
        }

        @Override
        public void onBindViewHolder(final SwitchHolder holder, int position) {
            Picasso.with(getActivity()).load(AliCloudImageUtil.getScaledUrl(quickDocList.get(position).url)).into(holder.PPTView);
            if (position == 0) {
                holder.PPTOrder.setText("白板");
            } else {
                holder.PPTOrder.setText(String.valueOf(position));
            }
            holder.PPTRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.setSwitchPosition(holder.getAdapterPosition());
                }
            });
        }

        @Override
        public int getItemCount() {
            return quickDocList.size();
        }
    }

    class SwitchHolder extends RecyclerView.ViewHolder {
        ImageView PPTView;
        TextView PPTOrder;
        RelativeLayout PPTRL;

        SwitchHolder(View itemView) {
            super(itemView);
            this.PPTView = (ImageView) itemView.findViewById(R.id.item_ppt_view);
            this.PPTOrder = (TextView) itemView.findViewById(R.id.item_ppt_order);
            this.PPTRL = (RelativeLayout) itemView.findViewById(R.id.item_ppt_rl);
        }
    }
}
