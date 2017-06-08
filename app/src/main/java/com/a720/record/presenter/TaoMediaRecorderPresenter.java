package com.a720.record.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.a720.record.comm.Constants;
import com.a720.record.listener.MyRecorderOnTouchListener;
import com.a720.record.utils.FileUtilx;
import com.a720.record.utils.TimeFormatUtils;
import com.a720.record.view.RecordVideoActivity;
import com.example.lshapp.shortvideodemo.R;
import com.im.av.logic.manage.IMCommitManager;
import com.taobao.av.logic.media.TaoMediaRecorder;
import com.taobao.av.ui.view.recordline.ClipManager;
import com.taobao.av.ui.view.recordline.RecorderTimeline;
import com.taobao.av.ui.view.recordline.VideoBean;

/**
 * Created by michaelluo on 17/2/21.
 *
 * @desc 阿里影音录制处理者
 */

public class TaoMediaRecorderPresenter {

    private Context mContext;
    private Handler mHandler;
    private TaoMediaRecorder mTaoMediaRecorder;//tao影音录制器
    private CheckBox btn_delete_last_clip;
    private CheckBox iv_ok;
    private TextView tv_recordtime;
    private RecorderTimeline mRecorderTimeline;//录制时间线控制
    private ClipManager mClipManager;//裁剪（时间片段）管理者
    private Animation alpahAnimation;
    private Animation scaleAnimation;
    private ImageView iv_Recorderbg;
    private ImageView iv_Recorder;
    private Runnable mLongPressRunnable;
    private Runnable _runnableTimer;


    public TaoMediaRecorderPresenter(Context context, Handler handler,
                                     TextView tv_recordtime, FrameLayout record_timeline,
                                     CheckBox btn_delete_last_clip, CheckBox iv_ok,
                                     ImageView iv_Recorderbg, ImageView iv_Recorder) {
        this.mContext = context;
        this.mHandler = handler;
        this.tv_recordtime = tv_recordtime;
        this.btn_delete_last_clip = btn_delete_last_clip;
        this.iv_ok = iv_ok;
        this.iv_Recorderbg = iv_Recorderbg;
        this.iv_Recorder = iv_Recorder;

        //init clipManager
        this.mClipManager = new ClipManager();
        this.mClipManager.setMaxDuration(Constants.MAX_DURATION);
        this.mClipManager.setMinDuration(Constants.MIN_DURATION);
        //init RecorderTimeline
        this.mRecorderTimeline = new RecorderTimeline(record_timeline, this.mClipManager);
        this.mRecorderTimeline.setItemDrawable(com.taobao.taorecorder.R.drawable.aliwx_sv_recorder_timeline_clip_selector);
        //init Animation
        this.alpahAnimation = AnimationUtils.loadAnimation(mContext, com.taobao.taorecorder.R.anim.taorecorder_alpha_reverse);
        this.scaleAnimation = AnimationUtils.loadAnimation(mContext, com.taobao.taorecorder.R.anim.taorecorder_scale_reverse);
    }

    /**
     * 初始化长按录制-runnable
     */
    public void initLongPressRunnable() {
        this.mLongPressRunnable = new Runnable() {
            public void run() {
                if (!Constants.IS_VIDEO_RECORDING && mTaoMediaRecorder != null && mTaoMediaRecorder.canStartRecord()) {
                    Constants.IS_LONG_PRESS = true;
                    startRecord();
                } else if (mTaoMediaRecorder == null) {
                    recordError();
                }
            }
        };
    }

    /**
     * 初始化时间计时器-runnable
     */
    public void initTimerRunnable() {
        this._runnableTimer = new Runnable() {
            public void run() {
                if (mTaoMediaRecorder != null) {
                    if (Constants.SINGLE_VIDEO_TIME == 0L && !mTaoMediaRecorder.isRecording()) {
                        mHandler.postDelayed(this, 25L);
                    } else {
                        if (Constants.SINGLE_VIDEO_TIME == 0L) {
                            Constants.SINGLE_VIDEO_TIME = System.currentTimeMillis();
                        }
                        mClipManager.onRecordFrame(System.currentTimeMillis() - Constants.SINGLE_VIDEO_TIME);
                        setRecordTime();
                        if (mClipManager.isMinDurationReached()) {
                            iv_ok.setVisibility(View.VISIBLE);
                        } else {
                            iv_ok.setVisibility(View.VISIBLE);
                        }

                        if (mClipManager.isMaxDurationReached()) {
                            Constants.IS_VIDEO_RECORDING = false;
                            stopRecord();
                        } else {
                            mHandler.postDelayed(this, 25L);
                        }
                    }
                }
            }
        };
    }

    /**
     * 初始化触摸录制监听事件
     */
    public void initOnTouchRecordListener() {
        iv_Recorderbg.setOnTouchListener(new MyRecorderOnTouchListener(mHandler, this));
    }

    /**
     * 初始化Tao影音录制器
     */
    public void initTaoMediaRecorder() {
        //init mTaoMediaRecorder object
        if (this.mTaoMediaRecorder == null) {
            this.mTaoMediaRecorder = new TaoMediaRecorder(mContext);
            this.mTaoMediaRecorder.setVideoSource(1);
            this.mTaoMediaRecorder.setAudioSource(0);
            this.mTaoMediaRecorder.setOutputFormat(2);
            this.mTaoMediaRecorder.setAudioEncoder(0);
            this.mTaoMediaRecorder.setVideoEncoder(2);
            this.mTaoMediaRecorder.setVideoSize(Constants.PREVIEW_WIDTH, Constants.PREVIEW_HEIGHT);
//            this.mTaoMediaRecorder.setQuality(mCameraPresenter._quality);
            this.mTaoMediaRecorder.setQuality(1);
        }
        //mCamera对象传入mTaoMediaRecorder中准备录制
        this.mTaoMediaRecorder.prepareCamera(CameraPresenter.mCamera);
        this.mTaoMediaRecorder.setOrientationHintByCameraPostion((Activity) mContext, CameraPresenter.mCamera, Constants.CAMERA_POSITION);
        try {
            this.mTaoMediaRecorder.prepare();
        } catch (Exception var2) {
            var2.printStackTrace();
        }
    }

    /**
     * 开始录制
     */
    public void startRecord() {
        IMCommitManager.commitClick(IMCommitManager.getActivityPageName((Activity) mContext), "Video_Recording");
        btn_delete_last_clip.setVisibility(View.VISIBLE);
        iv_ok.setVisibility(View.VISIBLE);
        resetRecorderState();
        mRecorderTimeline.stopAnim();
        VideoBean videoBean = new VideoBean();
        videoBean.videoFile = FileUtilx.getLastOutputFile();
        mClipManager.onRecordStarted(videoBean);
        Constants.SINGLE_VIDEO_TIME = 0L;
        if (mHandler != null) {
            mHandler.post(_runnableTimer);
        }

        mRecorderTimeline.stopAnim();
        if (Constants.ENABLE_CLICK_RECORD && !Constants.IS_LONG_PRESS) {
            this.iv_Recorderbg.setBackgroundResource(com.taobao.taorecorder.R.drawable.aliwx_sv_recorder_ovalbg_stroke_pause);
        }

        this.iv_Recorderbg.startAnimation(this.scaleAnimation);
        this.iv_Recorder.startAnimation(this.alpahAnimation);
        this.mTaoMediaRecorder.setOutputFile(FileUtilx.getLastOutputFile());
        this.mTaoMediaRecorder.start();
        Constants.IS_VIDEO_RECORDING = true;
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        ((RecordVideoActivity) mContext).showIcon();
        this.btn_delete_last_clip.setEnabled(true);
        this.mClipManager.onRecordPaused();
        if (mHandler != null) {
            mHandler.removeCallbacks(this._runnableTimer);
        }

        if (this.iv_Recorder.isShown() && !this.mClipManager.isMaxDurationReached()) {
            this.mRecorderTimeline.startAnim();
        } else {
            this.mRecorderTimeline.stopAnim();
        }

        if (Constants.ENABLE_CLICK_RECORD && !Constants.IS_LONG_PRESS) {
            if (Constants.PROGRESS_AND_BTN_COLOR_TYPE == 1) {
                this.iv_Recorder.setBackgroundResource(com.taobao.taorecorder.R.drawable.aliwx_sv_st_video_record);
            } else if (Constants.PROGRESS_AND_BTN_COLOR_TYPE == 0) {
                this.iv_Recorder.setBackgroundResource(com.taobao.taorecorder.R.drawable.aliwx_sv_wx_video_record);
            }

            this.iv_Recorderbg.setBackgroundResource(com.taobao.taorecorder.R.drawable.aliwx_sv_recorder_ovalbg_stroke);
        }

        this.iv_Recorderbg.clearAnimation();
        this.iv_Recorder.clearAnimation();
        ++Constants.VIDEO_SEGMENT_INDEX;
        this.mTaoMediaRecorder.stop();
        if (this.mClipManager.isLastClipMinTime()) {
            clipDeleteLastlySegment();
        }

        this.btn_delete_last_clip.setEnabled(this.mClipManager.isUnEmpty());
        Constants.IS_VIDEO_RECORDING = false;
    }

    /**
     * 设置录制时间
     */
    public void setRecordTime() {
        int totaltime = this.mClipManager.getDuration();
        if (totaltime >= 0 && totaltime < Constants.MAX_DURATION) {
            String timeStr = TimeFormatUtils.millisecondToSecond(totaltime);
            this.tv_recordtime.setText(timeStr);
        }
    }

    /**
     * 录制错误
     */
    public void recordError() {
        if (!((RecordVideoActivity) mContext).isFinishing()) {
            Toast.makeText(mContext, mContext.getString(com.taobao.taorecorder.R.string.taorecorder_record_fail), Toast.LENGTH_SHORT).show();
            ((RecordVideoActivity) mContext).finish();
        }
    }

    /**
     * 点击删除（修剪）最后录制片段：MyOnclickLister点击事件中调用
     */
    public void click_delete_last_clip() {
        if (btn_delete_last_clip.isChecked()) {
            mRecorderTimeline.stopAnim();
            mClipManager.setLastClipSelected(true);
            setDeleteSelectedContent();
        } else {
            IMCommitManager.commitClick(IMCommitManager.getActivityPageName((Activity) mContext), "Video_DeleteLast");
            clipDeleteLastlySegment();
            setDeleteNoneSelectedContent();
        }

    }

    /**
     * 设置删除状态：第二次点击事删除录制片段显示
     */
    private void setDeleteSelectedContent() {
        if (btn_delete_last_clip != null) {
            btn_delete_last_clip.setText("删除");
            btn_delete_last_clip.setTextColor(mContext.getResources().getColor(com.taobao.taorecorder.R.color.imrecorder_remove_clip));
        }

    }

    /**
     * 设置回删状态：录制后首次点击显示
     */
    public void setDeleteNoneSelectedContent() {
        if (btn_delete_last_clip != null) {
            btn_delete_last_clip.setText("回删");
            btn_delete_last_clip.setTextColor(mContext.getResources().getColor(R.color.bghuise));
        }

    }

    /**
     * 删除裁剪最新录制片段
     */
    public void clipDeleteLastlySegment() {
        this.mClipManager.removeLastClip();
        --Constants.VIDEO_SEGMENT_INDEX;
        if (Constants.VIDEO_SEGMENT_INDEX <= 0) {
            this.btn_delete_last_clip.setVisibility(View.GONE);
//            this.iv_ok.setVisibility(View.GONE);
            this.iv_Recorderbg.setBackgroundResource(com.taobao.taorecorder.R.drawable.aliwx_sv_recorder_ovalbg_stroke);
            if (Constants.PROGRESS_AND_BTN_COLOR_TYPE == 1) {
                this.iv_Recorder.setBackgroundResource(com.taobao.taorecorder.R.drawable.aliwx_sv_strecorder_record_ovalbg);
            } else if (Constants.PROGRESS_AND_BTN_COLOR_TYPE == 0) {
                this.iv_Recorder.setBackgroundResource(com.taobao.taorecorder.R.drawable.aliwx_sv_recorder_record_ovalbg);
            }
        }

        if (this.mClipManager.isMaxDurationReached()) {
            this.iv_Recorder.setEnabled(false);
            this.iv_Recorder.setAlpha(0.5F);
            this.iv_Recorderbg.setAlpha(0.5F);
        } else {
            this.iv_Recorder.setEnabled(true);
            this.iv_Recorder.setAlpha(1.0F);
            this.iv_Recorderbg.setAlpha(1.0F);
        }

        this.mRecorderTimeline.stopAnim();
        this.mRecorderTimeline.startAnim();
        if (this.mClipManager.isMinDurationReached()) {
            this.iv_ok.setVisibility(View.VISIBLE);
            this.iv_ok.setAlpha(1.0F);
            this.iv_ok.setTextColor(mContext.getResources().getColor(R.color.bghuise));
        } else {
            this.iv_ok.setVisibility(View.VISIBLE);
            this.iv_ok.setAlpha(1.0F);
            this.iv_ok.setTextColor(mContext.getResources().getColor(R.color.bghuise));
        }

        this.btn_delete_last_clip.setEnabled(this.mClipManager.isUnEmpty());
        if (this.mClipManager.isEmpty()) {
            this.btn_delete_last_clip.setAlpha(0.5F);
        } else {
            this.btn_delete_last_clip.setAlpha(1.0F);
        }
        //设置录制时间
        setRecordTime();
    }

    /**
     * 重置录制状态
     */
    public void resetRecorderState() {
        ((RecordVideoActivity) mContext).hideIcon();
        this.btn_delete_last_clip.setEnabled(false);
        mClipManager.setLastClipSelected(false);
    }

    /**
     * 判断限制是否录制最短时间
     */
    public void toastRecordMinTime() {
        if (getmClipManager().getDuration() < Constants.MIN_DURATION) {
            Toast toast = Toast.makeText(mContext, "录制时长不能小于3秒钟", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public TaoMediaRecorder getmTaoMediaRecorder() {
        return mTaoMediaRecorder;
    }

    public void setmTaoMediaRecorder(TaoMediaRecorder mTaoMediaRecorder) {
        this.mTaoMediaRecorder = mTaoMediaRecorder;
    }

    public Runnable getmLongPressRunnable() {
        return mLongPressRunnable;
    }

    public void setmLongPressRunnable(Runnable mLongPressRunnable) {
        this.mLongPressRunnable = mLongPressRunnable;
    }

    public ClipManager getmClipManager() {
        return mClipManager;
    }

    public void setmClipManager(ClipManager mClipManager) {
        this.mClipManager = mClipManager;
    }

    public RecorderTimeline getmRecorderTimeline() {
        return mRecorderTimeline;
    }

    public void setmRecorderTimeline(RecorderTimeline mRecorderTimeline) {
        this.mRecorderTimeline = mRecorderTimeline;
    }

    public Animation getAlpahAnimation() {
        return alpahAnimation;
    }

    public void setAlpahAnimation(Animation alpahAnimation) {
        this.alpahAnimation = alpahAnimation;
    }

    public Animation getScaleAnimation() {
        return scaleAnimation;
    }

    public void setScaleAnimation(Animation scaleAnimation) {
        this.scaleAnimation = scaleAnimation;
    }
}
