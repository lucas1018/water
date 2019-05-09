package cn.zerone.water.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import cn.zerone.water.R;

public class CalenderActivity extends AppCompatActivity {

    private ImageView calender_back;
    private EditText edit;
    private Button btn_commit;
    private String editContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        initView();
        initOnClick();



    }
    /*
    初始化控件
     */
    private void initView() {
        calender_back = (ImageView) findViewById(R.id.calender_back);
        edit = (EditText) findViewById(R.id.edit);
        btn_commit = (Button) findViewById(R.id.btn_commit);
    }

    /*
    设置控件的点击事件
     */
    private void initOnClick() {
        //返回上一级页面
        calender_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //点击提交的时候获取edit控件内输入的值，传递到后台
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取edit控件的值
                editContent = edit.getText().toString().trim();
                //数据交互，将输入的数据传递到后台

            }
        });
    }


}
