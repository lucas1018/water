package cn.zerone.water.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import cn.zerone.water.R;

/**
 * Created by zero on 2018/12/13.
 */

public class DialogActivity extends Activity {
    static DialogActivity activity  =null;
    boolean isBack = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alert);
        DialogActivity.activity = this;
        if(getIntent().getBooleanExtra("progress",false)){
            findViewById(R.id.dismiss).setVisibility(View.GONE);
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
        }else{
            isBack = true;
            findViewById(R.id.dismiss).setVisibility(View.VISIBLE);
            findViewById(R.id.progress).setVisibility(View.GONE);

        }
        ((TextView)findViewById(R.id.message)).setText(getIntent().getStringExtra("message"));
        ((TextView)findViewById(R.id.title)).setText(getIntent().getStringExtra("title"));
        findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(!isBack){
                return true;
            }
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
    }

    public static void dismiss() {
        try{
            if(activity!=null){
                activity.finish();
                activity = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
