package com.a720.record.comm;

import com.im.Config;
import com.taobao.av.util.SystemUtil;

/**
 * Created by michaelluo on 17/2/21.
 *
 * @desc 常量定义类
 */

public class Constants {
    public static String TAG = "IMRecordVideo";
    public static int PROGRESS_AND_BTN_COLOR_TYPE = 1;//视频录制进度条、按钮动画绘制颜色（0-蓝、1-红）初始化页面按键颜色更改：imrecorder_filter_theme_selected_bg
    public static int PREVIEW_WIDTH = 640;//预览尺寸-宽
    public static int PREVIEW_HEIGHT = 480;//预览尺寸-高
    public static int CAMERA_POSITION = SystemUtil.getCameraFacingBack();//获取摄像头方向（前/后）
    public static boolean IS_VIDEO_RECORDING = false;//是否视频录制
    public static boolean HAS_FLASH_LIGHT = true;//是否有闪光灯？？待测试
    public static int MAX_DURATION = 8000;//最大录制时间周期
    public static int MIN_DURATION = 3000;//最小录制时间周期
    public static boolean IS_TOUCH_PRESS = false;//是否touch录制
    public static boolean IS_LONG_PRESS = false;//是否长按录制
    public static int VIDEO_SEGMENT_INDEX = 0;//视频录制片段index
    public static long SINGLE_VIDEO_TIME = 0L;//单段录制视频时间长度
    public static boolean ENABLE_CLICK_RECORD = false;//是否启用点击录制
    public static boolean SUCCESS_BRADCAST = false;//是否成功发送广播(com.taobao.taorecorder.action.error_action???)
    public static final int SIZETYPE_B = 1;// 获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;// 获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;// 获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;// 获取文件大小单位为GB的double值
}
