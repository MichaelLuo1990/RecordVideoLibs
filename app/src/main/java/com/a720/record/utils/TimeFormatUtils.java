package com.a720.record.utils;

import com.a720.record.view.RecordVideoActivity;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by michaelluo on 17/2/24.
 *
 * @desc 时间格式转化
 */

public class TimeFormatUtils {

    /**
     * 毫秒转化秒
     * @param time
     * @return
     */
    public static String millisecondToSecond (int time) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.setLength(0);
        Formatter formatter = new Formatter(stringBuilder, Locale.getDefault());
        String timeStr = formatter.format("%d.%d 秒", new Object[]{Integer.valueOf(time / 1000), Integer.valueOf(time / 100 % 10)}).toString();
        return timeStr;
    }
}
