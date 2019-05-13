package cn.zerone.water.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.cjt2325.cameralibrary.JCameraView;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;
import com.zxing.cameraapplication.CameraActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by zero on 2018/11/30.
 */

public class StepActivity extends Activity {
    GridView gvVideos = null;
    List<File> gvImageList = new ArrayList<>();
    List<File> gvVideoList = new ArrayList<>();
    TextView tvImageSize = null;
    TextView tvVideoSize = null;
    Map<File, String> success = new HashMap<>();
    List<File> fail = new ArrayList<>();
    private GridView gvImages;
    private CommonAdapter<File> gvImagesAdapter;
    private CommonAdapter<File> gvVideoAdapter;
    private String jobId;
    private String stepId;
    private EditText stepText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        gvVideos = findViewById(R.id.step_gv_videos);
        gvImages = findViewById(R.id.step_gv_images);
        tvImageSize = findViewById(R.id.step_tv_image_size);
        tvVideoSize = findViewById(R.id.step_tv_video_size);
        stepText = findViewById(R.id.step_et_text);
        stepId = getIntent().getStringExtra("stepId");
        jobId = getIntent().getStringExtra("jobjId");
        gvVideoList.add(new File(""));
        gvImageList.add(new File(""));
        findViewById(R.id.step_tv_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StepActivity.this, DialogActivity.class);
                intent.putExtra("progress",true);
                intent.putExtra("message","正在发送");
                intent.putExtra("title","发送中");
                startActivity(intent);
                if(gvImageList.size()==1&&gvVideoList.size()==1){
                    send(new ArrayList<String>(), new ArrayList<String>(), stepText.getText().toString());
                    return;
                }
                for (int i = 0; i < gvImageList.size() - 1; i++) {
                    File o = gvImageList.get(i);
//                    Requests.upload(new UploadObserver(o), App.token, o);
                }
                for (int i = 0; i < gvVideoList.size() - 1; i++) {
                    File o = gvVideoList.get(i);
//                    Requests.upload(new UploadObserver(o), App.token, o);
                }

            }
        });
        gvVideoAdapter = new CommonAdapter<File>(this, R.layout.layout_step_gridview_item, gvVideoList) {
            @Override
            protected void convert(ViewHolder holder, final File item, int position) {
                if (position == (gvVideoList.size() - 1)) {
                    holder.setImageResource(R.id.step_item_image, R.drawable.plus);
                    holder.setOnClickListener(R.id.step_item_image, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(StepActivity.this, CameraActivity.class);
                            intent.putExtra("mode", JCameraView.BUTTON_STATE_ONLY_RECORDER);
                            startActivityForResult(intent,400);
                        }
                    });
                } else {
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(item.getAbsolutePath());
                    Bitmap bitmap = mmr.getFrameAtTime();
                    holder.setImageBitmap(R.id.step_item_image, bitmap);
                    holder.setOnClickListener(R.id.step_item_image, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(StepActivity.this, FullVideoActivity.class);
                            intent.putExtra("videoPath", item.getAbsolutePath());
                            startActivity(intent);
                        }
                    });
                }

            }
        };
        gvImagesAdapter = new CommonAdapter<File>(this, R.layout.layout_step_gridview_item, gvImageList) {
            @Override
            protected void convert(ViewHolder holder, final File item, int position) {
                if (position == (gvImageList.size() - 1)) {
                    holder.setOnClickListener(R.id.step_item_image, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(StepActivity.this, CameraActivity.class);
                            intent.putExtra("mode", JCameraView.BUTTON_STATE_ONLY_CAPTURE);
                            startActivityForResult(intent,400);
                        }
                    });
                    holder.setImageResource(R.id.step_item_image, R.drawable.plus);
                } else {
                    Bitmap bitmap = BitmapFactory.decodeFile(item.getAbsolutePath());
                    holder.setImageBitmap(R.id.step_item_image, bitmap);
                    holder.setOnClickListener(R.id.step_item_image, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(StepActivity.this, FullImageActivity.class);
                            intent.putExtra("imagePath", item.getAbsolutePath());
                            startActivity(intent);
                        }
                    });
                }

            }
        };
        gvVideos.setAdapter(gvVideoAdapter);
        gvImages.setAdapter(gvImagesAdapter);
        setResult(200);
    }

    public void addImage(String imagePath) {
        if (imagePath != null && new File(imagePath).exists()) {
            gvImageList.add(0,new File(imagePath));
            gvImagesAdapter.notifyDataSetChanged();
            tvImageSize.setText("图片：" + (gvImageList.size()-1) + "个");
        }
    }

    public void addVideo(String videoPath) {
        if (videoPath != null && new File(videoPath).exists()) {
            gvVideoList.add(0,new File(videoPath));
            gvVideoAdapter.notifyDataSetChanged();
            tvVideoSize.setText("视频：" + (gvVideoList.size()-1) + "个");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String imagePath = null;
        String videoPath = null;
        if (resultCode == 101) {
            imagePath = data.getStringExtra("path");
        } else if (resultCode == 102) {
            videoPath = data.getStringExtra("path");
        }
        System.out.println("videoPath:" + videoPath);
        System.out.println("imagePath:" + imagePath);
        if (imagePath != null && new File(imagePath).exists()) {
            addImage(imagePath);
        } else if (videoPath != null && new File(videoPath).exists()) {
            addVideo(videoPath);
        }
    }

    class UploadObserver implements io.reactivex.Observer<JSONObject> {
        private final File file;

        public UploadObserver(File file) {
            this.file = file;
        }

        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(JSONObject jsonObject) {
            try{
                String url = jsonObject.getString("Url");
                success.put(file, url);
            }catch (Exception e){
                onError(new IOException());
            }
        }

        @Override
        public void onError(Throwable e) {
            fail.add(file);
            System.out.println("uploadError");
            if(fail.size()==(gvImageList.size()+gvVideoList.size()-2)){
                DialogActivity.dismiss();
                Toast.makeText(StepActivity.this, "上传失败", 0).show();
            }else if (success.size() + fail.size() == (gvImageList.size() + gvVideoList.size() - 2)) {
                DialogActivity.dismiss();
                Toast.makeText(StepActivity.this, "上传失败", 0).show();
            }
        }

        @Override
        public void onComplete() {
            System.out.println("uploadOnComplete");
            if (success.size() == (gvImageList.size() + gvVideoList.size() - 2)) {
                List<String> videoSuccess = new ArrayList<>();
                List<String> imageSuccess = new ArrayList<>();
                Iterator<File> iterator = success.keySet().iterator();
                while (iterator.hasNext()) {
                    File successFile = iterator.next();
                    if (gvImageList.contains(successFile)) {
                        imageSuccess.add(success.get(successFile));
                    }
                    if (gvVideoList.contains(successFile)) {
                        videoSuccess.add(success.get(successFile));
                    }
                }
                send(videoSuccess, imageSuccess, stepText.getText().toString());
            } else if (success.size() + fail.size() == (gvImageList.size() + gvVideoList.size() - 2)) {
                DialogActivity.dismiss();
                Toast.makeText(StepActivity.this, "上传失败", 0).show();
            }
        }
    }

    public void send(List<String> videoList, List<String> imageList, String text) {
//        Requests.uploadjobstepinfo(new Observer<String>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(String s) {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Toast.makeText(StepActivity.this, "上传失败", 0).show();
//                DialogActivity.dismiss();
//            }
//
//            @Override
//            public void onComplete() {
//                Toast.makeText(StepActivity.this, "完成上传", 0).show();
//                DialogActivity.dismiss();
//                finish();
//            }
//        }, App.token, jobId, stepId, imageList, videoList, text);
    }
}
