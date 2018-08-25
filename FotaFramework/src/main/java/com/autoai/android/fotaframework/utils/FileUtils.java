package com.autoai.android.fotaframework.utils;

import com.autoai.android.fota.model.FOTADeviceInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by wangyanchao on 2018/8/25.
 */

public class FileUtils {

    private static final String TAG = "FotaFramework";

    private FOTADeviceInfo mDeviceInfo;

    public static String readTxtFile(String strFilePath) {
        String path = strFilePath;
        String content = ""; //文件内容字符串
        //打开文件
        File file = new File(path);
        if (!file.exists()) {
            if (LogManager.isLoggable()) {
                LogManager.e(TAG, "readTxtFile --> The File doesn't exist.");
            }
            return null;
        }
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            if (LogManager.isLoggable()) {
                LogManager.e(TAG, "readTxtFile --> The File is dir.");
            }
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                //分行读取
                while ((line = buffreader.readLine()) != null) {
                    content += line + "\n";
                }
                instream.close();
            } catch (java.io.FileNotFoundException e) {
                if (LogManager.isLoggable()) {
                    LogManager.e(TAG, "readTxtFile --> The File doesn't exist.", e);
                }
            } catch (IOException e) {
                if (LogManager.isLoggable()) {
                    LogManager.e(TAG, "readTxtFile --> " + e.getMessage(), e);
                }
            }
        }
        return content;
    }

}
