package com.a720.record.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.a720.record.comm.Constants;
import com.a720.record.presenter.FileCleanAsynPresenter;
import com.a720.record.utils.FileUtilx;
import com.bumptech.glide.Glide;
import com.example.lshapp.shortvideodemo.R;

/**
 * Created by michaelluo on 17/2/18.
 *
 * @desc 编辑视频页
 */
public class EditVideoActivity extends Activity {

    private String mTaoRecorderVideoRootPath;
    private String videoPath;
    private String framePicPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editvideo);
        init();
    }

    /**
     * 初始化显示
     */
    private void init() {
        String videoDuration = getIntent().getStringExtra("videoDuration");
        int videoSize = getIntent().getIntExtra("videoSize", 0);
        double videofilesize = FileUtilx.FormetFileSize(videoSize, Constants.SIZETYPE_MB);
        mTaoRecorderVideoRootPath = getIntent().getStringExtra("taorecorder_video");
        videoPath = getIntent().getStringExtra("videoPath");
        framePicPath = getIntent().getStringExtra("framePicPath");
        TextView tvvideosize = (TextView) findViewById(R.id.tvvideosize);
        TextView tvlztime = (TextView) findViewById(R.id.tvlztime);
        tvvideosize.setText("视频大小：" + videofilesize + "MB");
        tvlztime.setText("录制时长：" + videoDuration);
        //加载默认图片
        ImageView imageviewvideo = (ImageView) findViewById(R.id.imageviewvideo);
        Glide.with(EditVideoActivity.this).load(framePicPath).error(R.drawable.jc_play_normal).into(imageviewvideo);
        //缩略图布局点击跳转事件
        FrameLayout framelayoutvideo = (FrameLayout) findViewById(R.id.framelayoutvideo);
        framelayoutvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(videoPath)) {
                    Intent intent = new Intent(EditVideoActivity.this, PreviewVideoActivity.class);
                    intent.putExtra("videoPath", videoPath);
                    startActivity(intent);
                } else {
                    Toast.makeText(EditVideoActivity.this, "视频路径无效", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 顶部返回按键
     *
     * @param view
     */
    public void backBtn(View view) {
        FileCleanAsynPresenter fileCleanAsynPresenter = new FileCleanAsynPresenter(this, mTaoRecorderVideoRootPath);
        fileCleanAsynPresenter.execute();
    }

    /**
     * 物理back键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            FileCleanAsynPresenter fileCleanAsynPresenter = new FileCleanAsynPresenter(this, mTaoRecorderVideoRootPath);
            fileCleanAsynPresenter.execute();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
