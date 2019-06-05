package cn.zerone.water.views;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import cn.zerone.water.R;


/**
 * Created by Administrator on 2018/10/10.
 */

public class UpdataDialog extends android.app.Dialog implements View.OnClickListener {
  private Context context;

  private int layoutResID;

  /**
   * 要监听的控件id
   */
  private int[] listenedItems;

  private OnCenterItemClickListener listener;
  private int type;
  public UpdataDialog(Context context, int layoutResID, int[] listenedItems,int type) {
    super(context, R.style.MyDialog);
    this.context = context;
    this.layoutResID = layoutResID;
    this.listenedItems = listenedItems;
    this.type=type;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Window window = getWindow();
    window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置为居中
    setContentView(layoutResID);
    // 宽度全屏
    WindowManager windowManager = ((Activity) context).getWindowManager();
    Display display = windowManager.getDefaultDisplay();
    WindowManager.LayoutParams lp = getWindow().getAttributes();
    lp.width = display.getWidth()*4/5; // 设置dialog宽度为屏幕的4/5
    getWindow().setAttributes(lp);
    // 点击Dialog外部消失
    setCanceledOnTouchOutside(false);

    for (int id : listenedItems) {
      if (type==1){
        if (id==R.id.relative_imagv_dialog_cancel){
          findViewById(R.id.relative_imagv_dialog_cancel).setVisibility(View.GONE);
        }
      }else {
        findViewById(R.id.relative_imagv_dialog_cancel).setVisibility(View.VISIBLE);
      }
      findViewById(id).setOnClickListener(this);
    }
  }

  public interface OnCenterItemClickListener {

    void OnCenterItemClick(UpdataDialog dialog, View view);

  }

  public void setOnCenterItemClickListener(OnCenterItemClickListener listener) {
    this.listener = listener;
  }

  @Override
  public void onClick(View view) {
    if (view.getId()==R.id.relative_imagv_dialog_cancel){
      dismiss();
    }else {
      dismiss();
      listener.OnCenterItemClick(this, view);
    }

  }
}
