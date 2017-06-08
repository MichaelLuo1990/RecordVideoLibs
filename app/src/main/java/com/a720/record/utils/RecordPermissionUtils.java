package com.a720.record.utils;

import android.content.Context;

import com.im.Util;
import com.taobao.av.util.MediaFileUtils;
import com.taobao.av.util.SystemUtil;

/**
 * Created by michaelluo on 17/3/1.
 *
 * @desc 录制相关权限
 */

public class RecordPermissionUtils {

    /**
     * 检测是否支持录制的设备
     * @param context
     * @return
     */
    public static boolean checkIsUnSupportVersion(Context context) {
        return !Util.hasMediaEncodeSo(context) || SystemUtil.isSpecialMobileType() || !MediaFileUtils.checkSDCardAvailable();
    }
}
