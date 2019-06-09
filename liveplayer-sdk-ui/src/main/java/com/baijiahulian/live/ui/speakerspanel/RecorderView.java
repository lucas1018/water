package com.baijiahulian.live.ui.speakerspanel;

import android.content.Context;

import com.baijia.baijiashilian.liveplayer.CameraGLSurfaceView;


/**
 * Created by Shubo on 2017/6/10.
 */

public class RecorderView extends CameraGLSurfaceView {

    public RecorderView(Context context) {
        super(context);
        init();
    }

    private void init() {
        this.setZOrderMediaOverlay(true);
    }
}
