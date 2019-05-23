package cn.zerone.water.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import cn.zerone.water.R;

public class ConstructionLog extends Activity {
    private ImageView log_back = null;
    public  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);
        log_back = (ImageView)findViewById(R.id.log_back);
        log_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}
