package com.a720.record.utils;

import com.a720.record.comm.Constants;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by michaelluo on 17/2/28.
 *
 * @desc 文件工具类
 */

public class FileUtilx {
    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    public static double FormetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case Constants.SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case Constants.SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case Constants.SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case Constants.SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    /**
     * 获取最新录制片段目录-录制时片段拼接使用
     *
     * @return
     */
    public static String getLastOutputFile() {
        return "temp_" + Constants.VIDEO_SEGMENT_INDEX + ".mp4";
    }

    /**
     * 删除指定目录下的所有文件-视频文件or截屏图片文件
     *
     * @param file
     */
    public static void deleteAllFiles(File file) {
        File files[] = file.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }

}
