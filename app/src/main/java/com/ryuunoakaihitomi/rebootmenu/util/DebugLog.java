package com.ryuunoakaihitomi.rebootmenu.util;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.ryuunoakaihitomi.rebootmenu.MyApplication;
import com.ryuunoakaihitomi.rebootmenu.csc_compat.CrashReport;

import java.io.File;

import androidx.annotation.Nullable;

/**
 * 日志输出调试工具
 * Created by ZQY on 2018/2/11.
 * <p>
 * "不再打印堆栈以提高性能"之类的话deprecated，因为现在
 * 的话输出已经可以由用户控制。而且没有日志实在难以调试。
 * <p>
 * 规范：在必要时：
 * 标注方法名，有时标注类名。
 * 冒号标示参数和返回值，等号标示赋值。
 * 仅有一个不标注名称
 *
 * @author ZQY
 * @version 1.7
 * @see android.util.Log
 */

public class DebugLog {

    /**
     * 外置存储根目录用以打印调试日志的识别标记
     */
    public static final String TOKEN_TAG = "rebootmenuLog";
    /**
     * 全局日志标签
     */
    private static final String TAG = "rebootmenu";

    //总输出开关
    private static final boolean isLog;

    //用配置管理取不到值，所以在这里使用内置存储。而且卸载重装不用重新配置。
    static {
        boolean tokenExists = new File(Environment.getExternalStorageDirectory().getPath() + "/" + TOKEN_TAG).exists();
        isLog = MyApplication.isDebug || tokenExists;
        Log.i(TAG, "DebugLog: isDebug:" + MyApplication.isDebug + " tokenExists:" + tokenExists);
    }

    /**
     * debug级日志输出
     *
     * @param msg 输出内容
     */
    public DebugLog(String msg) {
        if (isLog)
            Log.d(TAG, msg);
        CrashReport.log("-", msg);
    }

    /**
     * 其他等级日志输出
     *
     * @param msg      输出内容
     * @param logLevel 日志等级
     */
    public DebugLog(String msg, LogLevel logLevel) {
        if (isLog)
            switch (logLevel) {
                case V:
                    Log.v(TAG, msg);
                    break;
                case D:
                    new DebugLog(msg);
                    break;
                case I:
                    Log.i(TAG, msg);
                    break;
                case W:
                    Log.w(TAG, msg);
                    break;
                case E:
                    Log.e(TAG, msg);
                    break;
                case WTF:
                    Log.wtf(TAG, msg);
                    break;
                default:
                    Log.w(TAG, "(level?)" + msg);
            }
        CrashReport.log("LogLevel " + logLevel2String(logLevel), msg);
    }

    /**
     * 带标签的日志输出
     * {@link Log#DEBUG}
     *
     * @param subTag 子标签
     * @param msg    输出内容
     */
    public DebugLog(String subTag, String msg) {
        new DebugLog(subTag, msg, null);
    }

    /**
     * 带标签的日志输出
     *
     * @param subTag   子标签
     * @param msg      输出内容
     * @param logLevel 日志等级
     */
    public DebugLog(String subTag, String msg, @Nullable LogLevel logLevel) {
        if (TextUtils.isEmpty(subTag)) new DebugLog(msg, logLevel == null ? LogLevel.D : logLevel);
        else
            new DebugLog(String.format("%s:[%s]", subTag, msg), logLevel != null ? logLevel : LogLevel.D);
        CrashReport.log(subTag + ' ' + logLevel2String(logLevel), msg);
    }

    /**
     * 打印堆栈
     *
     * @param t           待打印堆栈
     * @param label       简述标签
     * @param isImportant 是否是重要堆栈，若是，日志级别设为Error
     *                    重要堆栈指不太可能发生或有待深入研究的异常
     */
    public DebugLog(Throwable t, String label, boolean isImportant) {
        if (isImportant) CrashReport.logNonFatalExceptions(t);
        if (isLog)
            if (isImportant)
                Log.e(TAG, label, t);
            else
                Log.w(TAG, label, t);
    }

    /*
     * 还是用传统的格式方便
     * ---
     * {@link Log#v(String, String),Log#d(String, String),Log#i(String, String),Log#w(String, String),Log#e(String, String)}
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */


    public static void v(String tag, String msg) {
        if (isLog) Log.v(tagF(tag), msg);
        CrashReport.log(tag + ' ' + logLevel2String(LogLevel.V), msg);
    }

    public static void d(String tag, String msg) {
        if (isLog) Log.d(tagF(tag), msg);
        CrashReport.log(tag + ' ' + logLevel2String(LogLevel.D), msg);
    }

    public static void i(String tag, String msg) {
        if (isLog) Log.i(tagF(tag), msg);
        CrashReport.log(tag + ' ' + logLevel2String(LogLevel.I), msg);
    }

    public static void w(String tag, String msg) {
        if (isLog) Log.w(tagF(tag), msg);
        CrashReport.log(tag + ' ' + logLevel2String(LogLevel.W), msg);
    }

    public static void e(String tag, String msg) {
        if (isLog) Log.e(tagF(tag), msg);
        CrashReport.log(tag + ' ' + logLevel2String(LogLevel.E), msg);
    }

    //指定标签格式
    private static String tagF(String subTag) {
        return String.format("%s:%s", TAG, subTag);
    }

    private static String logLevel2String(@Nullable LogLevel level) {
        return level == null ? "" : level.name();
    }

    //日志等级
    public enum LogLevel {
        V, D, I, W, E, WTF
    }
}
