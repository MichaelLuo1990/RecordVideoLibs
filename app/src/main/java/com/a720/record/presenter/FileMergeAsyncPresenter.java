package com.a720.record.presenter;

import android.content.Intent;
import android.os.AsyncTask;

import com.a720.record.comm.Constants;
import com.a720.record.utils.TimeFormatUtils;
import com.a720.record.view.EditVideoActivity;
import com.a720.record.view.RecordVideoActivity;
import com.a720.record.widget.CircularProgressView;
import com.taobao.av.util.FileUtils;
import com.taobao.media.MediaEncoderMgr;

import java.io.File;
import java.util.UUID;

/**
 * Created by michaelluo on 17/2/28.
 *
 * @desc 文件异步处理者
 */

public class FileMergeAsyncPresenter extends AsyncTask<Void, Void, String> {

    private RecordVideoActivity mRecordVideoActivity;
    private TaoMediaRecorderPresenter mTaoMediaRecorderPresenter;

    private String mTaoRecorderVideoRootPath;//录制与截屏保存根目录
    private String mTargetVideoPath;
    private String mTargetJpgPath;
    private int mVideoSize;
    private String mVideoTime;

    public FileMergeAsyncPresenter(RecordVideoActivity recordVideoActivity, TaoMediaRecorderPresenter mTaoMediaRecorderPresenter) {
        this.mRecordVideoActivity = recordVideoActivity;
        this.mTaoMediaRecorderPresenter = mTaoMediaRecorderPresenter;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mRecordVideoActivity.showIcon();
        CircularProgressView.showProgressDialog();
    }

    @Override
    protected String doInBackground(Void... params) {
        //获取根文件存储目录
        mTaoRecorderVideoRootPath = mTaoMediaRecorderPresenter.getmTaoMediaRecorder().getFileDir();
        //遍历视频文件片段
        String[] input = new String[Constants.VIDEO_SEGMENT_INDEX];
        for (int tempVideoPath = 0; tempVideoPath < Constants.VIDEO_SEGMENT_INDEX; ++tempVideoPath) {
            input[tempVideoPath] = mTaoMediaRecorderPresenter.getmTaoMediaRecorder().getFileDir() + File.separator + "temp_" + tempVideoPath + ".mp4";
        }
        //获取视频文件临时路径
        String tempVideoPath = mTaoMediaRecorderPresenter.getmTaoMediaRecorder().getFileDir() + File.separator + "temp_output.mp4";
        FileUtils.deleteFile(tempVideoPath);//删除原有的视频文件
        MediaEncoderMgr.mergeMp4Files(input, tempVideoPath);//新文件重新编码合并文件
        mTaoMediaRecorderPresenter.getmTaoMediaRecorder().setOutputFile("temp_output.mp4");//重新设置输出文件路径
        String UUIDString = UUID.randomUUID().toString().replaceAll("-", "");
        mTargetVideoPath = mTaoMediaRecorderPresenter.getmTaoMediaRecorder().getFileDir() + File.separator + UUIDString + "_output.mp4";
        mTargetJpgPath = mTaoMediaRecorderPresenter.getmTaoMediaRecorder().getFileDir() + File.separator + UUIDString + "_output.jpg";
        FileUtils.copyFile(tempVideoPath, mTargetVideoPath);//复制保存视频文件
        //获取图片文件临时路径
        String tempJpgPath = mTaoMediaRecorderPresenter.getmTaoMediaRecorder().getJpegFile();
        FileUtils.copyFile(tempJpgPath, mTargetJpgPath);//复制保存图片文件-预览缩略图使用
        //获取视频文件大小
        if (mTargetVideoPath != null && (new File(mTargetVideoPath)).isFile()) {
            File file = new File(mTargetVideoPath);
            mVideoSize = (int) file.length();
        }
        //获取视频录制时间
        int totaltime = mTaoMediaRecorderPresenter.getmClipManager().getDuration();
        mVideoTime = TimeFormatUtils.millisecondToSecond(totaltime);
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        CircularProgressView.dismissProgressDialog();
        Intent intent = new Intent(mRecordVideoActivity, EditVideoActivity.class);
        intent.putExtra("taorecorder_video", mTaoRecorderVideoRootPath);//文件根路径（jar包封装的目录）
        intent.putExtra("videoPath", mTargetVideoPath);//视频路径
        intent.putExtra("framePicPath", mTargetJpgPath);//图片路径
        intent.putExtra("videoDuration", mVideoTime);//录制时间
        intent.putExtra("videoSize", mVideoSize);//录制大小
        mRecordVideoActivity.startActivity(intent);
        mRecordVideoActivity.finish();
    }
}
