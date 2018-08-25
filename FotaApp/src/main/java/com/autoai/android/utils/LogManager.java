package com.autoai.android.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class LogManager {

    public static final String TAGPENG = "FOTA";
    private final static boolean isLoggable = true;
    private final static boolean isFileLoggable = isLoggable;
    private final static boolean isMonitorLoggable = true;//是否在调试工具上面显示log

    private static final String GLOBAL = "GLOBAL";

    public static boolean isLoggable() {
        return isLoggable;
    }

    public static String debugPath = "/mnt/sdcard/autoai/fotadebug";

    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat mSdf = new SimpleDateFormat("yyyyMMddHH");

    public static void setLogPath(String path) {
        try {
            if (TextUtils.isEmpty(path)) {
                return;
            }
            if (path.endsWith(File.separator)) {
                path = path.substring(0, path.length() - 1);
            }
            debugPath = path + "/mapbar_log/debug";
        } catch (Exception e) {
        }
    }

    public static void writeLog(Context context) {
        try {
            if (!isLoggable())
                return;
            String path = debugPath;
            File file = new File(path);
            if (!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.mkdirs();
            }
            String logPath = path + "log_"
                    + mSdf.format(System.currentTimeMillis()) + ".txt";
            Runtime.getRuntime().exec("logcat -v long -f " + logPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final class FilePrintRunnable implements Runnable {

        private static PrintWriter printWriter = null;

        private int level;
        private String tag;
        private String msg;
        private Throwable t;

        private FilePrintRunnable(int level, String tag, String msg, Throwable t) {
            this.level = level;
            this.tag = tag;
            this.msg = msg;
            this.t = t;
        }

        @Override
        public void run() {

            boolean exception = false;

            try {
                if (null == printWriter) {
                    String path = debugPath;
                    File file = new File(path);
                    if (!file.exists()) {
                        //noinspection ResultOfMethodCallIgnored
                        file.mkdirs();
                    }

                    String logFileName = file.getAbsolutePath() + File.separator + "sdk_ota_msg_" + mSdf.format(System.currentTimeMillis()) + ".log";
                    File logFile = new File(logFileName);
                    if (!logFile.exists()) {
                        //noinspection ResultOfMethodCallIgnored
                        logFile.createNewFile();
                    }
                    printWriter = new PrintWriter(new FileOutputStream(logFile, true));
                }

                // 级别
                printWriter.print("|");
                printWriter.print(levelName(level));

                // 时间
                printWriter.print("|");
                printWriter.print(TIME_FORMAT.format(new Date()));

                // TAG
                printWriter.print("|");
                printWriter.print(tag);

                // 信息
                printWriter.print("|");
                printWriter.print(msg);

                // 异常
                if (null != t) {
                    t.printStackTrace(printWriter);
                }

                printWriter.println();
                printWriter.flush();

            } catch (Exception e) {
                exception = true;
            }

            if (workQueue.size() < 1 || exception) {
                if (null != printWriter) {
                    printWriter.close();
                    printWriter = null;
                }
            }
        }
    }

    public enum CodeLocationStyle {

        /**
         * 第一行
         */
        FIRST(true, true),
        /**
         * 随后的行
         */
        SUBSEQUENT(true, true);

        /**
         * 是否添加at字眼在行首
         */
        private boolean isAt;

        /**
         * 是否使用简单类名
         */
        private boolean isSimpleClassName;

        CodeLocationStyle(boolean isAt, boolean isSimpleClassName) {
            this.isAt = isAt;
            this.isSimpleClassName = isSimpleClassName;
        }

        /**
         * @return the {@link #isAt}
         */
        public boolean isAt() {
            return isAt;
        }

        /**
         * @return the {@link #isSimpleClassName}
         */
        public boolean isSimpleClassName() {
            return isSimpleClassName;
        }

    }

    private static BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
    private static ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, workQueue);

    /**
     * 日志级别
     */
    public static final int VERBOSE = 1;
    /**
     * 日志级别
     */
    public static final int DEBUG = 2;
    /**
     * 日志级别
     */
    public static final int INFO = 3;
    /**
     * 日志级别
     */
    public static final int WARN = 4;
    /**
     * 日志级别
     */
    public static final int ERROR = 5;

    /**
     * 时间格式
     */
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");

    private static void smartPrint(int level, String tag, String msg, Throwable t, boolean printStackTrace) {

        if (!isLoggable(tag, level)) {
            return;
        }

        Thread currentThread = Thread.currentThread();

        StackTraceElement[] stackTrace = currentThread.getStackTrace();

        int i = 4;

        String msgResult = String.valueOf(currentThread.getId()) + //
                "|" + //
                getCodeLocation(CodeLocationStyle.FIRST, null, stackTrace[i]) + //
                "|" + //
                msg;
        print(level, tag, msgResult, t);
        filePrint(level, tag, msgResult, t);

        i++;

        for (; printStackTrace && i < stackTrace.length; i++) {
            String s = getCodeLocation(CodeLocationStyle.SUBSEQUENT, currentThread, stackTrace[i]).toString();
            print(level, tag, s, null);
            filePrint(level, tag, s, null);
        }

    }

    private static void print(int level, String tag, String msg, Throwable t) {
        if (!isMonitorLoggable) {
            return;
        }
        switch (level) {
            case VERBOSE:
                if (t != null) {
                    android.util.Log.v(tag, msg, t);
                } else {
                    android.util.Log.v(tag, msg);
                }
                break;
            case DEBUG:
                if (t != null) {
                    android.util.Log.d(tag, msg, t);
                } else {
                    android.util.Log.d(tag, msg);
                }
                break;
            case INFO:
                if (t != null) {
                    android.util.Log.i(tag, msg, t);
                } else {
                    android.util.Log.i(tag, msg);
                }
                break;
            case WARN:
                if (t != null) {
                    android.util.Log.w(tag, msg, t);
                } else {
                    android.util.Log.w(tag, msg);
                }
                break;
            case ERROR:
                if (t != null) {
                    android.util.Log.e(tag, msg, t);
                } else {
                    android.util.Log.e(tag, msg);
                }
                break;
        }
    }

    private static StringBuilder getCodeLocation(CodeLocationStyle style, Thread currentThread, StackTraceElement stackTraceElement) {
        String className = stackTraceElement.getClassName();
        int lineNumber = stackTraceElement.getLineNumber();
        String methodName = stackTraceElement.getMethodName();
        String fileName = stackTraceElement.getFileName();
        StringBuilder sb = new StringBuilder();
        if (style.isAt()) {
            sb.append("	at ");
        }
        if (style.isSimpleClassName()) {
            sb.append(getSimpleName(className));
        } else {
            sb.append(className);
        }
        sb.append(".").append(methodName).append("(").append(fileName).append(":").append(lineNumber).append(")");
        return sb;
    }

    private static String getSimpleName(String className) {
        String[] split = className.split("\\.");
        return split[split.length - 1];
    }

    /**
     * 是否可以打印日志
     *
     * @param tag   标签
     * @param level 等级
     * @return 控制台是否输出日志
     */
    public static boolean isLoggable(String tag, int level) {
        return isLoggable;
    }


    /**
     * 是否可以打印日志到文件
     *
     * @param tag   标签
     * @param level 等级
     * @return 文件是否输出日志
     */
    public static boolean isFileLoggable(String tag, int level) {
        return isFileLoggable;
    }


    @Deprecated
    public static void v(String tag, String msg) {
        smartPrint(VERBOSE, tag, msg, null, false);
    }

    public static void v(String tag, String msg, Throwable t) {
        smartPrint(VERBOSE, tag, msg, t, false);
    }

    public static void vs(String tag, String msg) {
        smartPrint(VERBOSE, tag, msg, null, true);
    }

    @Deprecated
    public static void d(String tag, String msg) {
        smartPrint(DEBUG, tag, msg, null, false);
    }


    public static void d(String tag, String msg, Throwable t) {
        smartPrint(DEBUG, tag, msg, t, false);
    }

    public static void ds(String tag, String msg) {
        smartPrint(DEBUG, tag, msg, null, true);
    }

    @Deprecated
    public static void i(String tag, String msg) {
        smartPrint(INFO, tag, msg, null, false);
    }

    public static void i(String tag, String msg, Throwable t) {
        smartPrint(INFO, tag, msg, t, false);
    }

    public static void is(String tag, String msg) {
        smartPrint(INFO, tag, msg, null, true);
    }

    @Deprecated
    public static void w(String tag, String msg) {
        smartPrint(WARN, tag, msg, null, false);
    }

    public static void w(String tag, String msg, Throwable t) {
        smartPrint(WARN, tag, msg, t, false);
    }

    public static void ws(String tag, String msg) {
        smartPrint(WARN, tag, msg, null, true);
    }

    public static void e(String msg) {
        e(TAGPENG, msg);
    }

    public static void e(String tag, String msg) {

//		tag = "Collecter";//强制替换tag

        smartPrint(ERROR, tag, msg, null, false);
    }

    public static void e(String tag, String msg, Throwable t) {
        smartPrint(ERROR, tag, msg, t, false);
    }


    public static String levelName(int level) {
        switch (level) {
            case VERBOSE:
                return "VERBOSE";
            case DEBUG:
                return "DEBUG";
            case INFO:
                return "INFO";
            case WARN:
                return "WARN";
            case ERROR:
                return "ERROR";
            default:
                return "DEFAULT";
        }
    }

    public static void registerUncaughtExceptionHandler() {
        final UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                e(GLOBAL, "uncaughtException", ex);
                defaultUncaughtExceptionHandler.uncaughtException(thread, ex);
            }
        });
    }

    public static void filePrint(int level, String tag, String msg, Throwable t) {
        if (!isFileLoggable(tag, level)) {
            return;
        }
        executorService.submit(new FilePrintRunnable(level, tag, msg, t));
    }

}
