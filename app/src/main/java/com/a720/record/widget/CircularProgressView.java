package com.a720.record.widget;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.taobao.av.ui.view.CircularProgressDrawable;
import com.taobao.av.util.DensityUtil;

/**
 * Created by michaelluo on 17/2/24.
 *
 * @desc 跳转编辑预览加载进度
 */

public class CircularProgressView {

    private Context mContext;
    private static CircularProgressDrawable mProgressDrawable;//圆形进度处理弹窗（跳转预览时显示）
    private ImageView mProgressView;
    private TextView mProgressTextView;
    private static View mProgressDialogView;

    public CircularProgressView(Context mContext, TextView mProgressTextView, ImageView mProgressView , View mProgressDialogView) {
        this.mContext = mContext;
        this.mProgressTextView = mProgressTextView;
        this.mProgressView = mProgressView;
        this.mProgressDialogView = mProgressDialogView;
    }

    /**
     * 初始化处理弹窗（点击确认后跳转编辑页面）
     */
    public void initProgressDialog() {
        int ringWidth = DensityUtil.dip2px(mContext, 2.0F);
        mProgressDrawable = new CircularProgressDrawable(-1, (float) ringWidth);
        mProgressView.setImageDrawable(mProgressDrawable);
        mProgressTextView.setText(mContext.getString(com.taobao.taorecorder.R.string.taorecorder_doing));
    }

    /**
     * 显示处理弹窗
     */
    public static void showProgressDialog() {
        if (mProgressDialogView != null && !mProgressDialogView.isShown()) {
            mProgressDrawable.start();
            mProgressDialogView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 取消处理弹窗
     */
    public static void dismissProgressDialog() {
        if (mProgressDialogView != null && mProgressDialogView.isShown()) {
            mProgressDialogView.setVisibility(View.GONE);
            mProgressDrawable.stop();
        }
    }
}
