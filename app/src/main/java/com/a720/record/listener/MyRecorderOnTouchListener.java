package com.a720.record.listener;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.a720.record.comm.Constants;
import com.a720.record.presenter.TaoMediaRecorderPresenter;

/**
 * Created by michaelluo on 17/2/23.
 *
 * @desc 长按录制视频监听事件
 */

public class MyRecorderOnTouchListener implements View.OnTouchListener{

    private Handler mHandler;
    private TaoMediaRecorderPresenter mTaoMediaRecorderPresenter;
    public MyRecorderOnTouchListener(Handler handler, TaoMediaRecorderPresenter taoMediaRecorderPresenter) {
        this.mHandler = handler;
        this.mTaoMediaRecorderPresenter = taoMediaRecorderPresenter;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (!Constants.IS_TOUCH_PRESS) {
                    if (Constants.IS_VIDEO_RECORDING && Constants.ENABLE_CLICK_RECORD) {
                        mTaoMediaRecorderPresenter.stopRecord();
                        return true;
                    }

                    Constants.IS_TOUCH_PRESS = true;
                }
                mHandler.postDelayed(mTaoMediaRecorderPresenter.getmLongPressRunnable(), 250L);
                break;
            case MotionEvent.ACTION_UP:
                if (Constants.IS_TOUCH_PRESS) {
                    mHandler.removeCallbacks(mTaoMediaRecorderPresenter.getmLongPressRunnable());
                    if (Constants.IS_LONG_PRESS) {
                        if (Constants.IS_VIDEO_RECORDING) {
                            mTaoMediaRecorderPresenter.stopRecord();
                        }
                    } else if (!Constants.IS_VIDEO_RECORDING && mTaoMediaRecorderPresenter.getmTaoMediaRecorder() != null && mTaoMediaRecorderPresenter.getmTaoMediaRecorder().canStartRecord() && Constants.ENABLE_CLICK_RECORD) {
                        mTaoMediaRecorderPresenter.startRecord();
                    } else if (mTaoMediaRecorderPresenter.getmTaoMediaRecorder() == null) {
                        mTaoMediaRecorderPresenter.recordError();
                    }
                }
                Constants.IS_TOUCH_PRESS = false;
                Constants.IS_LONG_PRESS = false;
                break;
            default:
                break;
        }
        return true;
    }
}
