package com.a720.record.comm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.a720.record.presenter.TaoMediaRecorderPresenter;
import com.example.lshapp.shortvideodemo.R;
import com.im.Util;
import com.taobao.av.util.DensityUtil;

/**
 * Created by michaelluo on 17/2/24.
 *
 * @desc 自定义UI控件样式
 */

public class CustomWidgetStyle {

    public static void setStyle(Context context, ImageView iv_Recorder, ImageView iv_notice_recordlimit, TaoMediaRecorderPresenter mTaoMediaRecorderPresenter, ImageView min_capture_duration_point) {
        TextView tempTextView;
        Bitmap bitmap;
        if (Constants.PROGRESS_AND_BTN_COLOR_TYPE == 1) {
            iv_Recorder.setBackgroundResource(com.taobao.taorecorder.R.drawable.aliwx_sv_st_video_record);
            tempTextView = Util.createTextViewWithBackgroud(context, DensityUtil.dip2px(context, 88.0F), DensityUtil.dip2px(context, 36.0F), DensityUtil.dip2px(context, 2.0F), DensityUtil.dip2px(context, 2.0F), com.taobao.taorecorder.R.drawable.aliwx_sv_st_notification_recordlimit, -1, context.getResources().getColor(R.color.bghuise), 17, context.getResources().getString(com.taobao.taorecorder.R.string.imrecorder_mintimeshow_message));
            bitmap = Util.convertViewToBitmap(tempTextView);
            iv_notice_recordlimit.setImageBitmap(bitmap);
            mTaoMediaRecorderPresenter.getmRecorderTimeline().setItemDrawable(com.taobao.taorecorder.R.drawable.aliwx_sv_strecorder_timeline_clip_selector);
            min_capture_duration_point.setImageDrawable(new ColorDrawable(context.getResources().getColor(com.taobao.taorecorder.R.color.imstrecorder_init_color)));
        } else if (Constants.PROGRESS_AND_BTN_COLOR_TYPE == 0) {
            tempTextView = Util.createTextViewWithBackgroud(context, DensityUtil.dip2px(context, 88.0F), DensityUtil.dip2px(context, 36.0F), DensityUtil.dip2px(context, 2.0F), DensityUtil.dip2px(context, 2.0F), com.taobao.taorecorder.R.drawable.aliwx_sv_notification_recordlimit, -1, context.getResources().getColor(R.color.bghuise), 17, context.getResources().getString(com.taobao.taorecorder.R.string.imrecorder_mintimeshow_message));
            bitmap = Util.convertViewToBitmap(tempTextView);
            iv_notice_recordlimit.setImageBitmap(bitmap);
        }
    }
}
