package com.a720.record.comm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.a720.record.view.RecordVideoActivity;
import com.im.av.view.WxAlertDialog;
import com.taobao.av.logic.media.TaoMediaRecorder;
import com.taobao.av.ui.view.NewDialog;
import com.taobao.av.util.FileUtils;

/**
 * Created by michaelluo on 17/2/23.
 *
 * @desc 退出弹窗选择样式(通过Constants.PROGRESS_AND_BTN_COLOR_TYPE判定调用那种弹窗样式)
 */

public class MyExitDialog {

    /**
     * 弹窗样式一：微信样式
     * @param context
     * @param taoMediaRecorder
     */
    public static void showWXStyle(final Context context, final TaoMediaRecorder taoMediaRecorder) {
        WxAlertDialog.Builder newDialog = new WxAlertDialog.Builder(context);
        newDialog.setMessage(context.getResources().getString(com.taobao.taorecorder.R.string.imrecorder_dlg_record_quit_message))
                .setCancelable(false).
                setPositiveButton(com.taobao.taorecorder.R.string.taorecorder_dlg_record_quit_confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                FileUtils.clearTempFiles(taoMediaRecorder.getFileDir());
                ((Activity) context).finish();
            }
        }).setNegativeButton(com.taobao.taorecorder.R.string.taorecorder_dlg_record_quit_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = newDialog.create();
        dialog.show();
    }

    /**
     * 弹窗样式二：淘宝样式
     * @param context
     * @param taoMediaRecorder
     */
    public static void showTaoStyle(final Context context, final TaoMediaRecorder taoMediaRecorder) {
        final NewDialog newDialog = new NewDialog((Activity) context);
        newDialog.setLeftMsg(context.getString(com.taobao.taorecorder.R.string.taorecorder_dlg_record_quit_confirm));
        newDialog.setRightMsg(context.getString(com.taobao.taorecorder.R.string.taorecorder_dlg_record_quit_cancel));
        newDialog.setMsg(context.getString(com.taobao.taorecorder.R.string.imrecorder_dlg_record_quit_message));
        newDialog.setRightMsgListener(new View.OnClickListener() {
            public void onClick(View v) {
                newDialog.dismiss();
            }
        });
        newDialog.setLeftMsgListener(new View.OnClickListener() {
            public void onClick(View v) {
                newDialog.dismiss();
                FileUtils.clearTempFiles(taoMediaRecorder.getFileDir());
                ((Activity) context).finish();
            }
        });
        newDialog.show();
    }

}
