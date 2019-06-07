package cn.zerone.water.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.alibaba.fastjson.JSONObject;
import com.zxing.cameraapplication.CameraActivity;

import java.io.File;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import cn.zerone.water.utils.image2Base64Util;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

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
    private TextView save = null;
    private TextView pro_list = null;
    private EditText pro_title = null;
    private EditText pro_content = null;
    private boolean has_type = false;
    private boolean has_pic = false;
    private boolean has_video = false;

    private String problem_newId = null;

    private String pic_path = "";
    private String video_path = "";

    private image2Base64Util img2base;
    private String basicPicturePath = "http://47.105.187.185:8011/Content/img/WebImg/";

    public  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_record);

        log_back = (ImageView)findViewById(R.id.log_back);
        problem_go = (ImageView)findViewById(R.id.problem_go);
        problem_type = (TextView)findViewById(R.id.problem_type);
        save = (TextView) findViewById(R.id.pr_save);
        pro_list = findViewById(R.id.pr_list);
        pro_title = (EditText) findViewById(R.id.pro_title);
        pro_content = (EditText) findViewById(R.id.pro_content);

        img2base = new image2Base64Util();

        log_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //进入问题列表
        pro_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(ProblemActivity.this, ProblemListActivity.class), 1);

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

        // 上传问题
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pro_title.getText().length() == 0)
                    Toast.makeText(ProblemActivity.this,"“问题概述”不能为空！", Toast.LENGTH_SHORT).show();
                else if(pro_content.getText().length()==0)
                    Toast.makeText(ProblemActivity.this,"“问题描述”不能为空！", Toast.LENGTH_SHORT).show();
                else if(!has_type)
                    Toast.makeText(ProblemActivity.this,"“问题类型”不能为空！", Toast.LENGTH_SHORT).show();
                else {
                    problem_save(pro_title.getText().toString(),pro_content.getText().toString(),problem_typeID);
                }

            }
        });


    }

    //上传问题的具体实现
    private void problem_save(String Title,String Remark,String TreeInfoId) {

        Requests.NewQuestion_SaveBLL(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject jsonObject) {
                String newId = jsonObject.getString("NewId");
                problem_newId = newId;

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Toast.makeText(ProblemActivity.this,"问题上报成功！", Toast.LENGTH_SHORT).show();
                //如果有图片 将图片上传到服务器，拿到返回的服务器上的pic_path，写到数据库中
                if (has_pic){
                    String base64 = img2base.getBase64(pic_path);
                    System.out.println("base64  -- >" +base64);
                    JSONObject object = Requests.Picture_SaveBLL(base64, "jpg");
                    String pic_path_server = object.getString("Temp");
                    System.out.println("服务器上的pic地址 ： "+pic_path_server);
                    attachment_save("0",pic_path_server,problem_newId);
                }
                if (has_video){
                    attachment_save("2",video_path,problem_newId);
                }

                //上传完成之后进入问题列表
                startActivityForResult(new Intent(ProblemActivity.this,ProblemListActivity.class),1);

            }
        }, App.userId,Title,Remark,TreeInfoId);

    }

    //上传图片视频或者文档的方法
    private void attachment_save(String FileType,String path,String problem_newId){
        String file_name = "未命名";

        //通过路径解析文件名
        if (path != null){
            file_name = path.substring(path.lastIndexOf('/')+1);
        }

        Requests.NewFile_SaveBLL(new Observer<JSONObject>() {
             @Override
             public void onSubscribe(Disposable d) {

             }

             @Override
             public void onNext(JSONObject jsonObject) {

             }

             @Override
             public void onError(Throwable e) {

             }

             @Override
             public void onComplete() {

             }
         }, App.userId,FileType,path,file_name,problem_newId);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，获取问题类型
        if (requestCode == 1 && resultCode == 720) {
            String result = data.getStringExtra("Name");
            problem_typeID = data.getStringExtra("problem_typeID");
            problem_type.setText(result);
            has_type = true;
        }

        //获取拍照图片
        if (requestCode == 1 && resultCode == 101) {
            pic_path = data.getStringExtra("path");

            ImageButton newPic = findViewById(R.id.problemImageButton);
            newPic.setVisibility(View.GONE);

            ImageView image = findViewById(R.id.problemImage);
            image.setVisibility(View.VISIBLE);
            image.setImageURI(Uri.fromFile(new File(pic_path)));

            has_pic = true;

        }

        //获取拍摄视频
        if (requestCode == 1 && resultCode == 102) {
            video_path = data.getStringExtra("path");

            ImageButton newVideo = findViewById(R.id.problemVideoButton);
            newVideo.setVisibility(View.GONE);

            VideoView video = findViewById(R.id.problem_video);
            video.setVisibility(View.VISIBLE);

            video.setMediaController(new MediaController(this));



            video.setVideoPath(video_path);

            video.start();

            has_video = true;

        }

    }
}
