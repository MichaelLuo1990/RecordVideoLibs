package com.a720.record.listener;

import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.taobao.av.ui.view.SizeChangedNotifier;

/**
 * Created by michaelluo on 17/2/23.
 *
 * @desc 预览窗口大小改变通知监听器-implements tb_video_recorder
 */

public class MyRecorderFrameSizeListener implements SizeChangedNotifier.Listener {

    private SurfaceView mSurfaceView;//录制预览surfaceView

    public MyRecorderFrameSizeListener(SurfaceView surfaceView) {
        this.mSurfaceView = surfaceView;
    }

    @Override
    public void onSizeChanged(View view, int w, int h, int oldw, int oldh) {
        float scale_x = (float) w / 480.0F;
        float scale_y = (float) h / 480.0F;
        if (scale_x != 0.0F && scale_y != 0.0F) {
            short rotation = 90;
            short preview_width = 640;
            short preivew_height = 480;
            short surface_width;
            short surface_height;
            switch (rotation) {
                case 90:
                case 270:
                    surface_width = preivew_height;
                    surface_height = preview_width;
                    break;
                default:
                    surface_width = preview_width;
                    surface_height = preivew_height;
            }

            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) this.mSurfaceView.getLayoutParams();
            lp.gravity = 51;
            lp.width = (int) (scale_x * (float) surface_width);
            lp.height = (int) (scale_y * (float) surface_height);
            lp.setMargins(0, 0, 0, 0);
            this.mSurfaceView.setLayoutParams(lp);
        }
    }
}
