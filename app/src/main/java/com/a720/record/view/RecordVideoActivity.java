package com.a720.record.view;

/**
 * Created by Administrator on 2016/7/27.
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.a720.record.comm.Constants;
import com.a720.record.comm.CustomWidgetStyle;
import com.a720.record.comm.MyExitDialog;
import com.a720.record.listener.MyOnClickListener;
import com.a720.record.listener.MyRecorderFrameSizeListener;
import com.a720.record.presenter.CameraPresenter;
import com.a720.record.presenter.FileMergeAsyncPresenter;
import com.a720.record.presenter.TaoMediaRecorderPresenter;
import com.a720.record.utils.RecordPermissionUtils;
import com.a720.record.widget.CircularProgressView;
import com.example.lshapp.shortvideodemo.R;
import com.im.SoInstallMgr;
import com.im.Util;
import com.im.av.logic.manage.IMCommitManager;
import com.taobao.av.ui.view.SizeChangedNotifier;
import com.taobao.av.util.MediaFileUtils;
import com.taobao.av.util.PermissionUtils;
import com.taobao.av.util.SystemUtil;
import com.taobao.media.MediaEncoder;


/**
 * Created by michaelluo on 17/2/18.
 *
 * @desc 视频录制
 */
public class RecordVideoActivity extends Activity {

    private Handler mHandler;
    private SurfaceView mSurfaceView;//录制预览surfaceView
    private AudioManager mAudioManager;//声音管理者
    private ImageView iv_cancel;//顶部关闭录制窗口
    private ImageView iv_light;//顶部闪光灯按键
    private ImageView iv_camerarotate;//顶部切换摄像头按键
    private CheckBox btn_delete_last_clip;
    private ImageView iv_Recorderbg;//录制按键监听-border
    private ImageView iv_Recorder;//录制按键监听-inner
    private CheckBox iv_ok;
    private View mProgressDialogView;
    private ImageView mProgressView;
    private TextView mProgressTextView;
    private TextView tv_recordtime;
    private FrameLayout record_timeline;
    private ImageView iv_notice_recordlimit;
    private ImageView min_capture_duration_point;
    private TranslateAnimation anim_mintime_notice;
    private final AudioManager.OnAudioFocusChangeListener mAudioFocusListener;
    //current package presenter
    private CameraPresenter mCameraPresenter;
    private TaoMediaRecorderPresenter mTaoMediaRecorderPresenter;

    //录制时间在onCreate方法里设置
    public RecordVideoActivity() {
        this.mHandler = new Handler();

        this.mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case -3:
                    case -2:
                    case -1:
                    case 0:
                    case 1:
                    default:
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(com.taobao.taorecorder.R.layout.aliwx_sv_recorder_activity_recorder);

        if (RecordPermissionUtils.checkIsUnSupportVersion(RecordVideoActivity.this)) {
            Toast.makeText(this, this.getString(com.taobao.taorecorder.R.string.taorecorder_notsupport), Toast.LENGTH_SHORT).show();
            this.finish();
        } else {
            if (MediaEncoder.isLoadMediaEncodeFailed() && SoInstallMgr.loadFileSo("MediaEncode", "", this)) {
                MediaEncoder.setLoadMediaEncodeFailed(false);
            }
            initView();
            //init AudioManager
            this.mAudioManager = (AudioManager) this.getApplication().getSystemService(Context.AUDIO_SERVICE);
            this.mAudioManager.requestAudioFocus(this.mAudioFocusListener, 3, 1);
            initPresenter();
            initOnClickLitener();
            initCircularProgressView();
            CustomWidgetStyle.setStyle(this, iv_Recorder, iv_notice_recordlimit, mTaoMediaRecorderPresenter, min_capture_duration_point);
        }
    }

    /**
     * 初始化-findViewByID
     */
    private void initView() {
        //init surfaceView
        this.mSurfaceView = (SurfaceView) this.findViewById(com.taobao.taorecorder.R.id.camera_view);
        SizeChangedNotifier frame = (SizeChangedNotifier) this.findViewById(com.taobao.taorecorder.R.id.camera_frame);
        frame.setOnSizeChangedListener(new MyRecorderFrameSizeListener(mSurfaceView));
        //init cancel btn of top
        this.iv_cancel = (ImageView) this.findViewById(com.taobao.taorecorder.R.id.iv_back);
        //init record pannel of bottom
        this.iv_light = (ImageView) this.findViewById(com.taobao.taorecorder.R.id.iv_light);
        this.iv_camerarotate = (ImageView) this.findViewById(com.taobao.taorecorder.R.id.iv_camerarotate);
        this.btn_delete_last_clip = (CheckBox) this.findViewById(com.taobao.taorecorder.R.id.btn_delete_last_clip);
        this.btn_delete_last_clip.setAlpha(0.5F);
        this.iv_Recorderbg = (ImageView) this.findViewById(com.taobao.taorecorder.R.id.iv_Recorderbg);
        this.iv_Recorder = (ImageView) this.findViewById(com.taobao.taorecorder.R.id.iv_Recorder);
        this.iv_ok = (CheckBox) this.findViewById(com.taobao.taorecorder.R.id.iv_ok);
        this.iv_ok.setAlpha(1.0F);
        this.iv_ok.setVisibility(View.VISIBLE);
        record_timeline = (FrameLayout) this.findViewById(com.taobao.taorecorder.R.id.record_timeline);
        this.tv_recordtime = (TextView) this.findViewById(com.taobao.taorecorder.R.id.tv_recordtime);
        this.iv_notice_recordlimit = (ImageView) this.findViewById(com.taobao.taorecorder.R.id.iv_notice_recordlimit);
        this.min_capture_duration_point = (ImageView) this.findViewById(com.taobao.taorecorder.R.id.min_capture_duration_point);
        //init loading dialog to edit/Preview Aty.
        mProgressDialogView = findViewById(com.taobao.taorecorder.R.id.view_dialog);
        mProgressView = (ImageView) findViewById(com.taobao.taorecorder.R.id.taorecorder_uik_circularProgress);
        mProgressTextView = (TextView) findViewById(com.taobao.taorecorder.R.id.taorecorder_uik_progressText);
    }

    /**
     * 初始化presenter
     */
    private void initPresenter() {
        //init TaoMediaRecorderPresenter
        mTaoMediaRecorderPresenter = new TaoMediaRecorderPresenter(this, mHandler, tv_recordtime, record_timeline, btn_delete_last_clip, iv_ok, iv_Recorderbg, iv_Recorder);
        mTaoMediaRecorderPresenter.initLongPressRunnable();
        mTaoMediaRecorderPresenter.initTimerRunnable();
        mTaoMediaRecorderPresenter.initOnTouchRecordListener();
        //init CameraPresenter
        mCameraPresenter = new CameraPresenter(RecordVideoActivity.this, iv_light, iv_camerarotate, mTaoMediaRecorderPresenter, mSurfaceView);
    }

    /**
     * 初始化点击监听事件
     */
    private void initOnClickLitener() {
        MyOnClickListener myOnClickListener = new MyOnClickListener(this, mCameraPresenter, mTaoMediaRecorderPresenter);
        this.iv_cancel.setOnClickListener(myOnClickListener);
        this.iv_light.setOnClickListener(myOnClickListener);
        this.iv_camerarotate.setOnClickListener(myOnClickListener);
        this.btn_delete_last_clip.setOnClickListener(myOnClickListener);
        this.iv_ok.setOnClickListener(myOnClickListener);
    }

    /**
     * 初始化加载视图-跳转视频编辑页面
     */
    private void initCircularProgressView() {
        CircularProgressView circularProgressView = new CircularProgressView(this, mProgressTextView, mProgressView, mProgressDialogView);
        circularProgressView.initProgressDialog();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onResume() {
        super.onResume();
        IMCommitManager.pageAppear(this);
        if (!mTaoMediaRecorderPresenter.getmClipManager().isMaxDurationReached()) {
            mTaoMediaRecorderPresenter.getmRecorderTimeline().startAnim();
        }

        mTaoMediaRecorderPresenter.setDeleteNoneSelectedContent();
        if (!mCameraPresenter.initCamera()) {
            mCameraPresenter.openCameraError(RecordVideoActivity.this, RecordVideoActivity.this.getString(com.taobao.taorecorder.R.string.taorecorder_camera_permission_deny));
        } else {
            mCameraPresenter.startPreview();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        IMCommitManager.pageDisappear(this);
        if (Constants.IS_VIDEO_RECORDING) {
            mTaoMediaRecorderPresenter.stopRecord();
        }

        mTaoMediaRecorderPresenter.getmRecorderTimeline().stopAnim();
        this.iv_Recorderbg.clearAnimation();
        this.iv_Recorder.clearAnimation();
        mCameraPresenter.stopPreview();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        if (this.mAudioManager != null) {
            this.mAudioManager.abandonAudioFocus(this.mAudioFocusListener);
        }

        if (mTaoMediaRecorderPresenter.getAlpahAnimation() != null) {
            mTaoMediaRecorderPresenter.getAlpahAnimation().cancel();
            mTaoMediaRecorderPresenter.getAlpahAnimation().reset();
            mTaoMediaRecorderPresenter.setAlpahAnimation(null);
        }

        if (mTaoMediaRecorderPresenter.getScaleAnimation() != null) {
            mTaoMediaRecorderPresenter.getScaleAnimation().cancel();
            mTaoMediaRecorderPresenter.getScaleAnimation().reset();
            mTaoMediaRecorderPresenter.setScaleAnimation(null);
        }

        if (mTaoMediaRecorderPresenter.getmRecorderTimeline() != null) {
            mTaoMediaRecorderPresenter.getmRecorderTimeline().destory();
        }

        if (this.mHandler != null) {
            this.mHandler.removeCallbacksAndMessages((Object) null);
            this.mHandler = null;
        }

        if (!Constants.SUCCESS_BRADCAST) {
            this.sendErrorBroadcast();
        }

        super.onDestroy();
    }

    /**
     * 最小时间点提示
     */
    public void showMinTimeNotice() {
        if (!this.iv_notice_recordlimit.isShown()) {
            if (this.anim_mintime_notice == null) {
                DisplayMetrics metric = new DisplayMetrics();
                this.getWindowManager().getDefaultDisplay().getMetrics(metric);
                int screenwidth = metric.widthPixels;
                int videowidth = this.iv_notice_recordlimit.getWidth();
                float _x = (float) (screenwidth * mTaoMediaRecorderPresenter.getmClipManager().getMinDuration()) / mTaoMediaRecorderPresenter.getmClipManager().getMaxDuration() - (float) (videowidth / 2);
                this.anim_mintime_notice = new TranslateAnimation((float) ((int) _x), (float) ((int) _x), -30.0F, 0.0F);
                this.anim_mintime_notice.setDuration(600L);
                this.anim_mintime_notice.setRepeatCount(3);
                this.anim_mintime_notice.setRepeatMode(2);
                this.anim_mintime_notice.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                    }

                    public void onAnimationEnd(Animation animation) {
                        RecordVideoActivity.this.iv_notice_recordlimit.setVisibility(View.INVISIBLE);
                    }

                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
            this.iv_notice_recordlimit.setVisibility(View.VISIBLE);
            this.iv_notice_recordlimit.startAnimation(this.anim_mintime_notice);
        }
    }

    /**
     * 跳转至视频编辑页面
     */
    public void toEditVideoActivity() {
        FileMergeAsyncPresenter fileMergeAsyncPresenter = new FileMergeAsyncPresenter(RecordVideoActivity.this, mTaoMediaRecorderPresenter);
        fileMergeAsyncPresenter.execute();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (101 == requestCode) {
            if (-1 == resultCode) {
                this.setResult(-1, data);
                this.finish();
                return;
            }

            if (resultCode == 0 && data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    String pubTitle = bundle.getString("pub_title_key");
                }
            }
        }

    }

    /**
     * 显示退出弹窗
     */
    public void showExitDilog() {
        if (!Constants.IS_VIDEO_RECORDING) {
            if (mTaoMediaRecorderPresenter.getmClipManager().isEmpty()) {
                this.finish();
            } else if (Constants.PROGRESS_AND_BTN_COLOR_TYPE == 0) {
                MyExitDialog.showWXStyle(this, mTaoMediaRecorderPresenter.getmTaoMediaRecorder());
            } else if (Constants.PROGRESS_AND_BTN_COLOR_TYPE == 1) {
                MyExitDialog.showTaoStyle(this, mTaoMediaRecorderPresenter.getmTaoMediaRecorder());
            }
        }
    }

    /**
     * 物理bcak按键监听事件
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            if (this.mProgressDialogView != null && this.mProgressDialogView.isShown()) {
                return true;
            } else {
                this.showExitDilog();
                return true;
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void sendErrorBroadcast() {
        Intent intent = new Intent("com.taobao.taorecorder.action.error_action");
        intent.putExtra("errorCode", "2002");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * 隐藏Icon（根据当前的录制状态）
     */
    public void hideIcon() {
        this.iv_cancel.setVisibility(View.INVISIBLE);
        this.iv_light.setVisibility(View.INVISIBLE);
        this.iv_camerarotate.setVisibility(View.INVISIBLE);
        this.btn_delete_last_clip.setAlpha(0.5F);
        this.iv_ok.setAlpha(1.0F);
        this.iv_ok.setTextColor(this.getResources().getColor(R.color.bghuise));
    }

    /**
     * 显示Icon（根据当前的录制状态）
     */
    public void showIcon() {
        this.iv_cancel.setVisibility(View.VISIBLE);
        this.iv_camerarotate.setVisibility(View.VISIBLE);
        this.btn_delete_last_clip.setAlpha(1.0F);
        if (mTaoMediaRecorderPresenter.getmClipManager().isMinDurationReached()) {
            this.iv_ok.setAlpha(1.0F);
            this.iv_ok.setTextColor(this.getResources().getColor(R.color.bghuise));
        } else {
            this.iv_ok.setAlpha(1.0F);
            this.iv_ok.setTextColor(this.getResources().getColor(R.color.bghuise));
        }

        if (Constants.CAMERA_POSITION == SystemUtil.getCameraFacingBack()) {
            if (Constants.HAS_FLASH_LIGHT) {
                this.iv_light.setVisibility(View.VISIBLE);
            }

            this.iv_camerarotate.setImageResource(com.taobao.taorecorder.R.drawable.aliwx_sv_wx_camera_nor);
        } else {
            this.iv_light.setVisibility(View.INVISIBLE);
            if (Constants.PROGRESS_AND_BTN_COLOR_TYPE == 1) {
                this.iv_camerarotate.setImageResource(com.taobao.taorecorder.R.drawable.aliwx_sv_st_camera_pre);
            } else if (Constants.PROGRESS_AND_BTN_COLOR_TYPE == 0) {
                this.iv_camerarotate.setImageResource(com.taobao.taorecorder.R.drawable.aliwx_sv_wx_camera_pre);
            }
        }
    }
}

