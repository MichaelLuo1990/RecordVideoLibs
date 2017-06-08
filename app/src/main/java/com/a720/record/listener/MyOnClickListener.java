package com.a720.record.listener;

import android.view.View;

import com.a720.record.comm.Constants;
import com.a720.record.presenter.CameraPresenter;
import com.a720.record.presenter.TaoMediaRecorderPresenter;
import com.a720.record.view.RecordVideoActivity;
import com.example.lshapp.shortvideodemo.R;
import com.im.av.logic.manage.IMCommitManager;

/**
 * Created by michaelluo on 17/2/24.
 *
 * @desc 点击事件监听
 */

public class MyOnClickListener implements View.OnClickListener {

    private RecordVideoActivity mRecordVideoActivity;
    private CameraPresenter mCameraPresenter;
    private TaoMediaRecorderPresenter mTaoMediaRecorderPresenter;

    public MyOnClickListener(RecordVideoActivity recordVideoActivity, CameraPresenter cameraPresenter, TaoMediaRecorderPresenter taoMediaRecorderPresenter) {
        this.mRecordVideoActivity = recordVideoActivity;
        this.mCameraPresenter = cameraPresenter;
        this.mTaoMediaRecorderPresenter = taoMediaRecorderPresenter;
    }

    @Override
    public void onClick(View v) {
        if (!Constants.IS_VIDEO_RECORDING) {
            int id = v.getId();
            if (id == R.id.iv_back) {
                IMCommitManager.commitClick(IMCommitManager.getActivityPageName(mRecordVideoActivity), "Video_Return");
                mRecordVideoActivity.showExitDilog();
            } else if (id == R.id.iv_light) {
                IMCommitManager.commitClick(IMCommitManager.getActivityPageName(mRecordVideoActivity), "Video_Flash");
                mCameraPresenter.switchLight();
            } else if (id == R.id.iv_camerarotate) {
                IMCommitManager.commitClick(IMCommitManager.getActivityPageName(mRecordVideoActivity), "Video_FrontBack");
                mCameraPresenter.switchCamera();
            } else if (id == R.id.btn_delete_last_clip) {
                mTaoMediaRecorderPresenter.click_delete_last_clip();
            } else if (id == R.id.iv_ok) {
                IMCommitManager.commitClick(IMCommitManager.getActivityPageName(mRecordVideoActivity), "Video_Confirm");
                if (mTaoMediaRecorderPresenter.getmClipManager().isMinDurationReached() && mTaoMediaRecorderPresenter.getmClipManager().getDuration() >= Constants.MIN_DURATION) {
                    mRecordVideoActivity.toEditVideoActivity();
                } else {
                    mTaoMediaRecorderPresenter.toastRecordMinTime();
                    mRecordVideoActivity.showMinTimeNotice();
                }
            }
        }
    }
}
