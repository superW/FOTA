package com.autoai.android.fotaframework.utils;

import android.util.Log;

import com.autoai.android.fota.model.FOTADeviceInfo;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by wangyanchao on 2018/8/25.
 */
public class FileUtilsTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void readTxtFile() throws Exception {
        String json = FileUtils.readTxtFile("/mnt/sdcard/autoai/deviceInfo.txt");
        Log.e("FOTA_framework", json);
    }


}