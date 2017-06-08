package com.a720.record.presenter;

import android.app.Activity;
import android.os.AsyncTask;

import com.a720.record.comm.Constants;
import com.a720.record.utils.FileUtilx;
import com.a720.record.view.EditVideoActivity;

import java.io.File;

/**
 * Created by michaelluo on 17/3/1.
 *
 * @desc 清除视频与图片文件异步线程-taorecord_video文件夹下
 */

public class FileCleanAsynPresenter extends AsyncTask<Void, Void, Void> {

    private EditVideoActivity mEditVideoActivity;
    private String mTaoRecorderVideoRootPath;//文件根路径

    public FileCleanAsynPresenter(EditVideoActivity editVideoActivity, String taoRecorderVideoRootPath) {
        this.mEditVideoActivity = editVideoActivity;
        this.mTaoRecorderVideoRootPath = taoRecorderVideoRootPath;
    }

    @Override
    protected Void doInBackground(Void... params) {
        FileUtilx.deleteAllFiles(new File(mTaoRecorderVideoRootPath));//退出时删除录制的视频和图片
        Constants.VIDEO_SEGMENT_INDEX = 0;
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mEditVideoActivity.finish();
    }
}
