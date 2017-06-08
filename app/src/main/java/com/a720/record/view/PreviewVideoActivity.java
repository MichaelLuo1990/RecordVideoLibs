package com.a720.record.view;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.a720.record.widget.PreviewVideoView;
import com.example.lshapp.shortvideodemo.R;


/**
 * Created by Administrator on 2016/8/8.
 */
public class PreviewVideoActivity extends Activity {

    private String videoPath;
    private PreviewVideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_widthmatchvideo);
        videoPath = getIntent().getStringExtra("videoPath");
        videoView = (PreviewVideoView) findViewById(R.id.common_videoView);
        videoView.start(videoPath);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            videoView.setFullScreen();
        } else {
            videoView.setNormalScreen();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}
