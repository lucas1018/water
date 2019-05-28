package cn.zerone.water.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.zxing.cameraapplication.CameraActivity;

import java.io.File;

import cn.zerone.water.R;

/**
 * Created by litinghui on 2019/5/28.
 */

public class ProblemActivity extends AppCompatActivity {

    private ImageView log_back = null;
    private ImageView problem_go = null;
    private TextView problem_type = null;
    private String problem_typeID = "";
    private ImageButton pic = null;
    private ImageButton video = null;

    public  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_record);

        log_back = (ImageView)findViewById(R.id.log_back);
        problem_go = (ImageView)findViewById(R.id.problem_go);
        problem_type = (TextView)findViewById(R.id.problem_type);

        log_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 获取问题类型
        problem_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(ProblemActivity.this, ProblemTypeActivity.class), 1);

            }
        });

        //拍照
        pic = findViewById(R.id.problemImageButton);
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ProblemActivity.this, CameraActivity.class), 1);

            }
        });

        video = findViewById(R.id.problemVideoButton);
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ProblemActivity.this, CameraActivity.class), 1);

            }
        });



    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，获取问题类型
        if (requestCode == 1 && resultCode == 720) {
            String result = data.getStringExtra("Name");
            problem_typeID = data.getStringExtra("ID");
            problem_type.setText(result);
        }

        //获取拍照图片
        if (requestCode == 1 && resultCode == 101) {
            String path = data.getStringExtra("path");

            ImageButton newPic = findViewById(R.id.problemImageButton);
            newPic.setVisibility(View.GONE);

            ImageView image = findViewById(R.id.problemImage);
            image.setVisibility(View.VISIBLE);
            image.setImageURI(Uri.fromFile(new File(path)));

        }

        //获取拍摄视频
        if (requestCode == 1 && resultCode == 102) {
            String path = data.getStringExtra("path");

            ImageButton newVideo = findViewById(R.id.problemVideoButton);
            newVideo.setVisibility(View.GONE);

            VideoView video = findViewById(R.id.problem_video);
            video.setVisibility(View.VISIBLE);

            video.setMediaController(new MediaController(this));

            video.setVideoPath(path);

            video.start();

        }

    }
}
