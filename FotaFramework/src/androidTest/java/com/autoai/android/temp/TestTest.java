package com.autoai.android.temp;

import android.util.Log;

import com.autoai.android.fotaframework.utils.FileUtils;

import org.junit.*;

import static org.junit.Assert.*;

/**
 * Created by wangyanchao on 2018/8/25.
 */
public class TestTest {
    Test test;

    @Before
    public void setUp() throws Exception {
        test = new Test();
    }

    @org.junit.Test
    public void analysisConfig() throws Exception {
        String json = FileUtils.readTxtFile("/mnt/sdcard/autoai/deviceInfo.txt");
        Log.e("FOTA_framework", json);
        test.analysisConfig(json);
    }

}