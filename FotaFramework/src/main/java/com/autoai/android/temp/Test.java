package com.autoai.android.temp;

import com.autoai.android.fota.model.FOTADeviceInfo;
import com.autoai.android.fota.model.FOTAModelInfo;
import com.autoai.android.fotaframework.utils.LogManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangyanchao on 2018/8/25.
 */

public class Test {

    private static final String TAG = "FOTA_framework";

    public void analysisConfig(String configInfo) {
        try {
            JSONObject jo = new JSONObject(configInfo);

            FOTADeviceInfo mDeviceInfo = new FOTADeviceInfo();
            mDeviceInfo.setDeviceKey(jo.getString("deviceKey")); // LGBM00105
            mDeviceInfo.setDeviceGroupInfo(jo.getString("deviceGroupInfo")); // 11
            mDeviceInfo.setDeviceType(jo.getString("deviceType")); // maiteng
            if (LogManager.isLoggable()) {
                LogManager.e(TAG, "analysisConfig --> FOTADeviceInfo=" + mDeviceInfo.toString());
            }

            List<FOTAModelInfo> mFOTAModelInfoList = new ArrayList<FOTAModelInfo>();
            JSONArray ja = jo.getJSONArray("modelInfos");
            for (int i = 0; i < ja.length(); i ++) {
                JSONObject jsonObject = ja.getJSONObject(i);

                FOTAModelInfo fOTAModelInfo = new FOTAModelInfo();
                fOTAModelInfo.setModelName(jsonObject.getString("modelName")); //testModel1
                fOTAModelInfo.setModelCurrentVersion(jsonObject.getInt("modelCurrentVersion")); // 更新版本
                fOTAModelInfo.setSystemCurrentVersion(jsonObject.getInt("systemCurrentVersion")); //系统版本
                fOTAModelInfo.setModelUpdateTime(System.currentTimeMillis());
                mFOTAModelInfoList.add(fOTAModelInfo);
            }
            if (LogManager.isLoggable()) {
                LogManager.e(TAG, "analysisConfig --> FOTAModelInfoList=" + mFOTAModelInfoList);
            }

        } catch (JSONException e) {
            if (LogManager.isLoggable()) {
                LogManager.e(TAG, "analysisConfig --> ", e);
            }
        }
    }
}
