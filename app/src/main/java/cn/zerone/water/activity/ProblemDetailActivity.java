package cn.zerone.water.activity;

import android.app.ActionBar;
import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.InputStream;

import cn.zerone.water.R;
import cn.zerone.water.http.Requests;

/**
 * Created by litinghui on 2019/5/29.
 */

public class ProblemDetailActivity extends AppCompatActivity {

    public  void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_detail);

        ImageView iv = (ImageView)findViewById(R.id.pro_back);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView problem_title = (TextView)findViewById(R.id.problem_title) ;
        TextView problem_type = (TextView)findViewById(R.id.problem_type) ;
        TextView pro_content = (TextView)findViewById(R.id.pro_content) ;

        ImageView problemImage = (ImageView)findViewById(R.id.problemImage) ;
        VideoView problemVideo = (VideoView) findViewById(R.id.problem_video);

        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString("Title");
        problem_title.setText(title);

        String createtime = bundle.getString("AddTime");

        String content = bundle.getString("Remark");
        pro_content.setText(content);

        String typeName =bundle.getString("typeName");
        problem_type.setText(typeName);

        //通过主键ID查询是否有视频或者照片与该问题绑定
        String id = bundle.getString("ID");

        JSONArray jsonObjects = Requests.getAttachmentsByProId(id);

        if (jsonObjects != null){
            for (int i = 0 ; i < jsonObjects.size() ; i++){

                JSONObject jsonObject = jsonObjects.getJSONObject(i);
                String fileType = jsonObject.getString("FileType");
                String path = jsonObject.getString("Path");
                if (fileType.equals("0")){
                    problemImage.setVisibility(View.VISIBLE);
                    problemImage.setImageURI(Uri.fromFile(new File(path)));
                }else if (fileType.equals("2")){

                    problemVideo.setVisibility(View.VISIBLE);

                    problemVideo.setMediaController(new MediaController(this));

                    problemVideo.setVideoPath(path);

                    problemVideo.start();

                }

            }
        }


    }
}
