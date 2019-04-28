package cn.zerone.water.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.net.URI;

import cn.zerone.water.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullImageActivity extends AppCompatActivity {


    private SimpleDraweeView iv_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_full_image);
        iv_photo = findViewById(R.id.iv_photo);
        String imagePath = getIntent().getStringExtra("imagePath");
        if(imagePath !=null&&!imagePath.toLowerCase().startsWith("http")&&new File(imagePath).exists()){
            iv_photo.setImageURI("file://"+imagePath);
        }else if(imagePath!=null&&imagePath.startsWith("http")){
            iv_photo.setImageURI(Uri.parse(imagePath));
        }else{
            finish();
        }

    }
}
