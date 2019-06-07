package cn.zerone.water.activity;

import android.app.ActionBar;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
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
import cn.zerone.water.utils.image2Base64Util;

/**
 * Created by litinghui on 2019/5/29.
 */

public class ProblemDetailActivity extends AppCompatActivity {

    private image2Base64Util img2base;
    private String basepic = "http://47.105.187.185:8011";

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

                    //从数据库拿到的path是从/Content开始的，必须加上前缀的IP地址才能获取到真正的图片url
                    //解析图片的url转化为64位编码，供ImageView生成图片
                    String path_server = basepic + path;

                    img2base = new image2Base64Util();
                    String path64 = img2base.encodeImageToBase64(path_server);
                    byte[] decodedStringAfter = Base64.decode(path64, Base64.DEFAULT);
                    Bitmap decodedByteAfter = BitmapFactory.decodeByteArray(decodedStringAfter, 0, decodedStringAfter.length);
                    problemImage.setImageBitmap(decodedByteAfter);
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
