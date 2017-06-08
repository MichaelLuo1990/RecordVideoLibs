package com.a720.record.presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.a720.record.comm.Constants;
import com.im.Util;
import com.im.av.logic.manage.IMCommitManager;
import com.taobao.av.util.CameraHelper;
import com.taobao.av.util.SystemUtil;

/**
 * Created by michaelluo on 17/2/21.
 *
 * @desc 相机控制处理者
 */

public class CameraPresenter {

    private Context mContext;
    public static Camera mCamera;
    private ImageView iv_light;
    private ImageView iv_camerarotate;
    private boolean mSurfaceAcquired = false;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private final SurfaceHolder.Callback surfaceCallback;
    private TaoMediaRecorderPresenter mTaoMediaRecorderPresenter;

    public CameraPresenter(Context context, final ImageView iv_light, ImageView iv_camerarotate, TaoMediaRecorderPresenter taoMediaRecorderPresenter, SurfaceView surfaceView) {
        this.mContext = context;
        this.iv_light = iv_light;
        this.iv_camerarotate = iv_camerarotate;
        this.mTaoMediaRecorderPresenter = taoMediaRecorderPresenter;
        this.mSurfaceView = surfaceView;
        this.mSurfaceHolder = this.mSurfaceView.getHolder();
        this.mSurfaceHolder.setType(3);
        this.surfaceCallback = new SurfaceHolder.Callback() {
            public void surfaceCreated(SurfaceHolder holder) {
                mSurfaceAcquired = true;
                startPreview();
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                mSurfaceAcquired = false;
                if (iv_light != null) {
                    iv_light.setImageResource(com.taobao.taorecorder.R.drawable.aliwx_sv_wx_shiny_nor);
                }
                stopPreview();
            }
        };
        addSurfaceCallBack();
    }

    /**
     * 初始化摄像头
     *
     * @return
     */
    public boolean initCamera() {
        try {
            mCamera = CameraHelper.openCamera(Constants.CAMERA_POSITION);
            if (mCamera == null) {
                openCameraError(mContext, mContext.getString(com.taobao.taorecorder.R.string.taorecorder_camera_permission_deny));
                return false;
            } else {
                Camera.Parameters parameters = mCamera.getParameters();
                setPreviewSize(parameters);
                CameraHelper.setFocusArea(parameters, new Rect(-100, -100, 100, 100));
                CameraHelper.setPreviewFrameRate(parameters, 20);
                //isSupportFocusModeChange
                if (!SystemUtil.isMobileInFocusModeBlackList()) {
                    CameraHelper.setFocusMode(parameters);
                }

                CameraHelper.setCameraDisplayOrientation(mContext, Constants.CAMERA_POSITION, mCamera);
                mCamera.setParameters(parameters);
                //checkIsUnSupportFlishLight
                if (!Util.hasFlash(mCamera)) {
                    this.iv_light.setVisibility(View.GONE);
                }
                return true;
            }
        } catch (Exception var2) {
            IMCommitManager.addErrorTrack("@sv", "initCamera", var2);
            return false;
        }
    }

    /**
     * 设置预览尺寸
     *
     * @param camera_params
     */
    private void setPreviewSize(Camera.Parameters camera_params) {
        Camera.Size valid_size = null;
        if (valid_size == null) {
            Camera.Size[] size_list = CameraHelper.choosePreviewSize(camera_params, 480, 480);
            if (size_list.length == 0) {
                valid_size = camera_params.getPreviewSize();
            } else {
                valid_size = size_list[0];
            }
        }

        Constants.PREVIEW_WIDTH = valid_size.width;
        Constants.PREVIEW_HEIGHT = valid_size.height;
        camera_params.setPreviewSize(valid_size.width, valid_size.height);
    }

    /**
     * 开始预览
     */
    public void startPreview() {
        if (mCamera != null && mSurfaceAcquired) {
            try {
                addSurfaceCallBack();
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
            } catch (Exception var2) {
                return;
            }

            Camera.Parameters cameraParams = this.mCamera.getParameters();
            if (cameraParams.getFocusMode() == "auto") {
                this.mCamera.autoFocus((Camera.AutoFocusCallback) null);
            }

            mTaoMediaRecorderPresenter.initTaoMediaRecorder();
//            TaoMediaRecorderPresenter.
        }
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();

        for (int i = 0; i < cameraCount; ++i) {
            Camera.getCameraInfo(i, cameraInfo);
            if (Constants.CAMERA_POSITION == SystemUtil.getCameraFacingBack()) {
                if (cameraInfo.facing == SystemUtil.getCameraFacingFront()) {
                    if (!this.removeHolderAndStopPreview(false)) {
                        openCameraError(mContext, mContext.getString(com.taobao.taorecorder.R.string.taorecorder_camera_permission_deny));
                        return;
                    }

                    Constants.CAMERA_POSITION = SystemUtil.getCameraFacingFront();
                    if (!initCamera()) {
                        openCameraError(mContext, mContext.getString(com.taobao.taorecorder.R.string.taorecorder_camera_permission_deny));
                        return;
                    }

                    this.startPreview();
                    this.iv_light.setVisibility(View.INVISIBLE);
                    if (Constants.PROGRESS_AND_BTN_COLOR_TYPE == 1) {
                        this.iv_camerarotate.setImageResource(com.taobao.taorecorder.R.drawable.aliwx_sv_st_camera_pre);
                    } else if (Constants.PROGRESS_AND_BTN_COLOR_TYPE == 0) {
                        this.iv_camerarotate.setImageResource(com.taobao.taorecorder.R.drawable.aliwx_sv_wx_camera_pre);
                    }
                    break;
                }
            } else if (cameraInfo.facing == SystemUtil.getCameraFacingBack()) {
                if (!this.removeHolderAndStopPreview(false)) {
                    openCameraError(mContext, mContext.getString(com.taobao.taorecorder.R.string.taorecorder_camera_permission_deny));
                    return;
                }

                Constants.CAMERA_POSITION = SystemUtil.getCameraFacingBack();
                if (!initCamera()) {
                    openCameraError(mContext, mContext.getString(com.taobao.taorecorder.R.string.taorecorder_camera_permission_deny));
                    return;
                }

                this.startPreview();
                this.iv_light.setImageResource(com.taobao.taorecorder.R.drawable.aliwx_sv_wx_shiny_nor);
                if (Constants.HAS_FLASH_LIGHT) {
                    this.iv_light.setVisibility(View.VISIBLE);
                }

                this.iv_camerarotate.setImageResource(com.taobao.taorecorder.R.drawable.aliwx_sv_wx_camera_nor);
                break;
            }
        }
    }

    /**
     * 停止预览
     */
    public void stopPreview() {
        boolean stopSuc = true;

        try {
            if (mTaoMediaRecorderPresenter.getmTaoMediaRecorder() != null) {
                mTaoMediaRecorderPresenter.getmTaoMediaRecorder().stop();
                Constants.IS_VIDEO_RECORDING = false;
            }

            this.removeHolderAndStopPreview(true);
        } catch (Exception var3) {
            stopSuc = false;
            IMCommitManager.addErrorTrack("@sv", "stopPreview", var3);
        }

        if (!stopSuc) {
            openCameraError(mContext, mContext.getString(com.taobao.taorecorder.R.string.taorecorder_camera_permission_deny));
        }
    }

    /**
     * 删除surfaceHolder与停止预览
     *
     * @param needLock
     * @return
     */
    private boolean removeHolderAndStopPreview(boolean needLock) {
        try {
            if (this.mCamera != null) {
                if (this.mSurfaceHolder != null) {
                    this.mSurfaceHolder.removeCallback(this.surfaceCallback);
                }

                this.mCamera.setPreviewCallback((Camera.PreviewCallback) null);
                this.mCamera.stopPreview();
                if (needLock) {
                    this.mCamera.lock();
                }

                this.mCamera.release();
                this.mCamera = null;
                return true;
            } else {
                return false;
            }
        } catch (Exception var3) {
            IMCommitManager.addErrorTrack("@sv", "removeHolderAndStopPreview", var3);
            return false;
        }
    }

    /**
     * 切换闪光灯
     */
    public void switchLight() {
        Camera.Parameters parameters = this.mCamera.getParameters();
        if (CameraHelper.getFlashlightOn(parameters)) {
            CameraHelper.setFlashlightMode(parameters, false);
            this.iv_light.setImageResource(com.taobao.taorecorder.R.drawable.aliwx_sv_wx_shiny_nor);
        } else {
            CameraHelper.setFlashlightMode(parameters, true);
            if (Constants.PROGRESS_AND_BTN_COLOR_TYPE == 1) {
                this.iv_light.setImageResource(com.taobao.taorecorder.R.drawable.aliwx_sv_st_shiny_pre);
            } else if (Constants.PROGRESS_AND_BTN_COLOR_TYPE == 0) {
                this.iv_light.setImageResource(com.taobao.taorecorder.R.drawable.aliwx_sv_wx_shiny_pre);
            }
        }

        this.mCamera.setParameters(parameters);
    }

    /**
     * 开启摄像头错误
     *
     * @param context  当前上下文
     * @param errorStr 错误信息
     */
    public void openCameraError(Context context, String errorStr) {
        if (!((Activity) context).isFinishing()) {
            Toast.makeText(context, errorStr, Toast.LENGTH_SHORT).show();
            ((Activity) context).finish();
        }
    }

    /**
     * 添加surface回调
     */
    public void addSurfaceCallBack() {
        this.mSurfaceHolder.removeCallback(this.surfaceCallback);
        this.mSurfaceHolder.addCallback(this.surfaceCallback);
    }
}
