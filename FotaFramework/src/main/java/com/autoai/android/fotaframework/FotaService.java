package com.autoai.android.fotaframework;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.SystemClock;

import com.autoai.android.aidl.FotaAidlListener;
import com.autoai.android.aidl.FotaAidlModelInfo;
import com.autoai.android.aidl.IFotaAidlInterface;
import com.autoai.android.callback.InnerListener;
import com.autoai.android.callback.ListenerManager;
import com.autoai.android.fota.api.MFOTAAPIManager;
import com.autoai.android.fota.api.MFOTAListener;
import com.autoai.android.fota.constant.FOTACode;
import com.autoai.android.fota.model.FOTADeviceInfo;
import com.autoai.android.fota.model.FOTAModelInfo;
import com.autoai.android.fota.model.ResultInfo;
import com.autoai.android.fotaframework.utils.FileUtils;
import com.autoai.android.fotaframework.utils.FotaModelInfoFormater;
import com.autoai.android.fotaframework.utils.LogManager;
import com.autoai.android.fotaframework.utils.SharedPreferencesUtil;
import com.autoai.android.fotaframework.utils.ThreadPoolManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FotaService extends Service implements MFOTAListener {

    private static final String TAG = "FotaFramework";
    private static final String configTxtFilePath = "/mnt/sdcard/autoai/deviceInfo.txt";

    public static final String METHOD_EXTRA = "method";

    public static final String MOBILE_DOWNLOAD_METHOD = "mobile_download_method";
    public static final String MOBILE_DOWNLOAD_EXTRA = "mobile_download_extra";

    public static final String NET_STATE_METHOD = "net_state_method";
    public static final String NET_STATE_EXTRA = "net_state_extra";

    public static final String COLLECT_DEVICE_INFO_METHOD = "collect_device_info_method";

    public static final String ROLLBACK_METHOD = "rollback_method";
    public static final String ROLLBACK_MODEL_EXTRA = "rollback_model_extra";

    private FotaAidlListener fotaAidlListener;

    private ThreadPoolManager threadPoolManager;

    private MFOTAAPIManager mfotaapiManager;
    private boolean fotaSdkInitResult = false;
    private boolean isInitOk = false;

    private Handler mainHandler;
    private static final int UPDATE_INSTALL_PROGRESS = 1;

    private NetworkChangedReceiver networkChangedReceiver;

    private List<FotaAidlModelInfo> fotaAidlModelInfos;

    private FOTADeviceInfo mDeviceInfo;
    private List<FOTAModelInfo> mFOTAModelInfoList = new ArrayList<FOTAModelInfo>();

    private long initStartMillis;
    private long initEndMillis;

    public FotaService() {
        if (LogManager.isLoggable()) {
            LogManager.e(TAG, "Constructor --> ");
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LogManager.isLoggable()) {
            LogManager.e(TAG, "onCreate --> ");
        }

        init();
    }

    private void init() {
        threadPoolManager = ThreadPoolManager.newInstance();
        threadPoolManager.prepare();

        initHandler();

        regReceiver();

        loadLocalData(); // 加载数据结束initSDK();
    }

    private void initHandler() {
        mainHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_INSTALL_PROGRESS:
                        if (fotaAidlListener != null) {
                            try {
                                FotaAidlModelInfo fotaAidlModelInfo = (FotaAidlModelInfo) msg.obj;
                                fotaAidlListener.onInstalling(fotaAidlModelInfo, msg.arg1);
                                if (msg.arg1 == 100) { // 成功的时候上报服务器
                                    FOTAModelInfo modelInfo = FotaModelInfoFormater.format(fotaAidlModelInfo);
                                    // 调用SDK上报接口
                                    reportUpgradeResult(modelInfo, true, "{}");
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /* 首次startService启动服务，会回调两次onStartCommand，原因是注册了网络变化监听 */
        if (LogManager.isLoggable()) {
            LogManager.e(TAG, "onStartCommand --> " + intent +
                    ", flags=" + flags +
                    ", startId=" + startId);
        }
        if (intent != null) {
            String method = intent.getStringExtra(METHOD_EXTRA);
            if (LogManager.isLoggable()) {
                LogManager.e(TAG, "onStartCommand --> method=" + method);
            }
            if (method != null) {
                if (isInitOk) {
                    if (COLLECT_DEVICE_INFO_METHOD.equals(method)) {
                        if (fotaSdkInitResult) {
                            collectDeviceInfo();
                        } else {
                            if (LogManager.isLoggable()) {
                                LogManager.e(TAG, "初始化失败，未采集设备model信息");
                            }
                        }
                    } else if (MOBILE_DOWNLOAD_METHOD.equals(method)) {
                        boolean mobileDownload = intent.getBooleanExtra(MOBILE_DOWNLOAD_EXTRA, false);
                        setMobileNetworkDownload(mobileDownload);
                    } else if (NET_STATE_METHOD.equals(method)) {
                        boolean netState = intent.getBooleanExtra(NET_STATE_EXTRA, false);
                        setNetState(netState);
                    } else if (ROLLBACK_METHOD.equals(method)) {
                        String modelName = intent.getStringExtra(ROLLBACK_MODEL_EXTRA);
                        rollback(modelName);
                    }
                } else {
                    if (LogManager.isLoggable()) {
                        String log = "SDK未初始化完成";
                        LogManager.e(TAG, log);
                        outLog(log);
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (LogManager.isLoggable()) {
            LogManager.e(TAG, "onBind --> intent=" + intent);
        }
        return fotaAidl;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (LogManager.isLoggable()) {
            LogManager.e(TAG, "onUnbind --> intent=" + intent);
        }
        stopSelf();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        threadPoolManager.shutdown();
        unRigReceiver();
        mfotaapiManager.stop();

        if (LogManager.isLoggable()) {
            LogManager.e(TAG, "onDestroy --> ");
        }
    }

    private IFotaAidlInterface.Stub fotaAidl = new IFotaAidlInterface.Stub() {

        @Override
        public void download(List<FotaAidlModelInfo> modelInfos) throws RemoteException {
            if (LogManager.isLoggable()) {
                LogManager.e(TAG, "download --> " + modelInfos);
            }

            List<FOTAModelInfo> fotaModelInfos = FotaModelInfoFormater.formatAidlList(modelInfos);
            downloadModels(fotaModelInfos);
        }

        @Override
        public void installModels(List<FotaAidlModelInfo> modelInfos) throws RemoteException {
            if (LogManager.isLoggable()) {
                LogManager.e(TAG, "installModels --> " + modelInfos);
            }

            List<FOTAModelInfo> fotaModelInfos = FotaModelInfoFormater.formatAidlList(modelInfos);
            installFotaModels(fotaModelInfos);
        }

        @Override
        public void setFotaListener(FotaAidlListener fotaListener) throws RemoteException {
            if (LogManager.isLoggable()) {
                LogManager.e(TAG, "setFotaListener --> " + fotaListener);
            }

            fotaAidlListener = fotaListener;
            if (fotaAidlListener != null && fotaAidlModelInfos != null) {
                fotaAidlListener.onUpgrade(fotaAidlModelInfos);
            }
        }
    };

    @Override
    public void onInitResult(boolean b, String s) {
        // TODO SDK初始化结果
        if (LogManager.isLoggable()) {
            LogManager.e(TAG, "onInitResult --> ");
        }

        if (!isInitOk) {
            isInitOk = true;
            initEndMillis = SystemClock.uptimeMillis();
            long time = initEndMillis - initStartMillis;
            String log = "初始化SDK所用时间：" + time + " ms";
            outLog(log);
            if (LogManager.isLoggable()) {
                LogManager.e(TAG, log);
            }
        }

        fotaSdkInitResult = b;

        Bundle bundle = new Bundle();
        bundle.putBoolean(InnerListener.RESULT_EXTRA, b);
        bundle.putString(InnerListener.REASON_EXTRA, s);
        ListenerManager.getInstance().sendCall(InnerListener.INIT_RESULT_ACTION, bundle);
    }

    @Override
    public void onUpload(List<FOTAModelInfo> list) {
        // TODO 返回的升级任务
        if (LogManager.isLoggable()) {
            LogManager.e(TAG, "onUpload --> " + list);
        }

        fotaAidlModelInfos = FotaModelInfoFormater.formatList(list);
        if (fotaAidlListener != null) {
            try {
                // 提示用户层显示可以升级的model任务
                fotaAidlListener.onUpgrade(fotaAidlModelInfos);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(InnerListener.UPGRADE_LIST_EXTRA,
                (ArrayList<? extends Parcelable>) fotaAidlModelInfos);
        ListenerManager.getInstance().sendCall(InnerListener.UPGRADE_LIST_ACTION, bundle);
    }

    @Override
    public void onDownloading(FOTAModelInfo fotaModelInfo, float v) {
        // TODO 下载进度提醒
        if (LogManager.isLoggable()) {
            LogManager.e(TAG, "onDownloading --> " + "progress=" + v + ", modelInfo=" + fotaModelInfo);
        }

        FotaAidlModelInfo fotaAidlModelInfo = FotaModelInfoFormater.format(fotaModelInfo);
        if (fotaAidlListener != null) {
            try {
                fotaAidlListener.onDownloading(fotaAidlModelInfo, v);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        Bundle bundle = new Bundle();
        bundle.putParcelable(InnerListener.DOWNLOADING_MODEL_EXTRA, fotaAidlModelInfo);
        bundle.putFloat(InnerListener.DOWNLOADING_PROG_EXTRA, v);
        ListenerManager.getInstance().sendCall(InnerListener.DOWNLOADING_ACTION, bundle);
    }

    @Override
    public void onDownloadResult(FOTAModelInfo fotaModelInfo, boolean b, String s) {
        // TODO 下载结果（下载文件已经校验过并成功返回结果），可通知应用层进行提示用户进行刷写升级
        if (LogManager.isLoggable()) {
            LogManager.e(TAG, "onDownloadResult --> " + b + ", result=" + s + ", modelInfo=" + fotaModelInfo);
        }

        SharedPreferencesUtil.SharedPreferencesSave_int(this,
                fotaModelInfo.getModelName(), fotaModelInfo.getModelCurrentVersion());
        if (fotaAidlListener != null) {
            try {
                FotaAidlModelInfo fotaAidlModelInfo = FotaModelInfoFormater.format(fotaModelInfo);
                if (b) {
                    fotaAidlListener.onFileDownloadSucceed(fotaAidlModelInfo);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onModelInfoIllegal(List<FOTAModelInfo> list) {
        // TODO 采集的软件信息有问题
        if (LogManager.isLoggable()) {
            LogManager.e(TAG, "onModelInfoIllegal --> " + list);
        }

    }

    @Override
    public void onAllowDownloadFileResult(Map<FOTAModelInfo.UpdateModelTaskInfo, ResultInfo> map) {
        // TODO 确认下载的所有文件信息，ResultInfo中code=0表示可以下载，code=1不可以下载，不予下载的文件可以对应用层的用户进行提示
        if (LogManager.isLoggable()) {
            LogManager.e(TAG, "onAllowDownloadFileResult --> " + map);
        }

    }

    /**
     * 加载本地配置文件
     */
    private void loadLocalData() {
        threadPoolManager.addExecuteTask(new Runnable() {
            @Override
            public void run() {
                long b = SystemClock.uptimeMillis();
                String configInfo = FileUtils.readTxtFile(configTxtFilePath);
                analysisConfig(configInfo);
                initStartMillis = SystemClock.uptimeMillis();
                long time = initStartMillis - b;
                if (LogManager.isLoggable()) {
                    LogManager.e(TAG, "读取配置文件并解析所用时间：" + time + " ms");
                }
                initSDK();
            }
        });
    }

    /**
     * 把json转成对象类
     *
     * @param configInfo
     */
    private void analysisConfig(String configInfo) {
        try {
            JSONObject jo = new JSONObject(configInfo);

            mDeviceInfo = new FOTADeviceInfo();
            mDeviceInfo.setDeviceKey(jo.getString("deviceKey")); // LGBM00105
            mDeviceInfo.setDeviceGroupInfo(jo.getString("deviceGroupInfo")); // 11
            mDeviceInfo.setDeviceType(jo.getString("deviceType")); // maiteng
            if (LogManager.isLoggable()) {
                LogManager.e(TAG, "analysisConfig --> FOTADeviceInfo=" + mDeviceInfo.toString());
            }

            JSONArray ja = jo.getJSONArray("modelInfos");
            for (int i = 0; i < ja.length(); i++) {
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

    /**
     * 初始化FOTA SDK
     */
    private void initSDK() {
        mfotaapiManager = new MFOTAAPIManager(this, this);
        mfotaapiManager.start(mDeviceInfo);
    }

    /**
     * 采集设备model信息
     */
    private void collectDeviceInfo() {

        for (int i = 0; i < mFOTAModelInfoList.size(); i++) {
            FOTAModelInfo fotaModelInfo = mFOTAModelInfoList.get(i);
            String modelName = fotaModelInfo.getModelName();
            int modelDefVersion = fotaModelInfo.getModelCurrentVersion();
            int modelCurVersion = SharedPreferencesUtil.SharedPreferencesSelect_int(this,
                    modelName, modelDefVersion);
            if (LogManager.isLoggable()) {
                LogManager.e(TAG, "collectDeviceInfo --> modelName=" + modelName +
                        ", modelCurVersion=" + modelCurVersion +
                        ", modelDefVersion=" + modelDefVersion);
            }
            if (modelCurVersion == modelDefVersion) {
                mFOTAModelInfoList.get(i).setModelCurrentVersion(modelCurVersion);
            }
        }

        mfotaapiManager.setAppInfo(mFOTAModelInfoList);
    }

    /**
     * 用户选择下载
     */
    private void downloadModels(List<FOTAModelInfo> modelInfos) {
        mfotaapiManager.allowDownloadFile(modelInfos);
    }

    /**
     * 用户选择安装
     */
    private void installFotaModels(List<FOTAModelInfo> modelInfos) {
        if (modelInfos != null && modelInfos.size() > 0) {
            for (final FOTAModelInfo fotaModelInfo : modelInfos) {
                threadPoolManager.addExecuteTask(new Runnable() {
                    @Override
                    public void run() {
                        moniData(fotaModelInfo);
                    }
                });
            }
        }
    }

    /***
     * 模拟数据
     * @param fotaModelInfo
     */
    private void moniData(FOTAModelInfo fotaModelInfo) {
        FotaAidlModelInfo fotaAidlModelInfo = FotaModelInfoFormater.format(fotaModelInfo);
        for (int j = 1; j <= 10; j++) {
            try {
                Thread.sleep(1000);
                Message msg = Message.obtain();
                msg.what = UPDATE_INSTALL_PROGRESS;
                msg.arg1 = j * 10;
                msg.obj = fotaAidlModelInfo;
                mainHandler.sendMessage(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 上报升级结果
     *
     * @param fotaModelInfo
     * @param success
     * @param json
     */
    public void reportUpgradeResult(FOTAModelInfo fotaModelInfo, boolean success, String json) {
        mfotaapiManager.setUpdateResult(fotaModelInfo, success, json);
    }

    /**
     * 设置网络状态的连接和断开
     *
     * @param conn
     */
    public void setNetState(boolean conn) {
        if (conn) {
            mfotaapiManager.setNetStatus(FOTACode.STATUS_NET_CONNECTED);
        } else {
            mfotaapiManager.setNetStatus(FOTACode.STATUS_NET_DISCONNECTED);
        }
    }

    private void rollback(String modelName) {
        FOTAModelInfo fotaModelInfo = new FOTAModelInfo();
        fotaModelInfo.setModelName(modelName);
        mfotaapiManager.setRollBack(fotaModelInfo);
    }

    /**
     * 设置移动网络是否可以下载
     *
     * @param canDownload
     */
    public void setMobileNetworkDownload(boolean canDownload) {
        mfotaapiManager.setMobileNetworkDownload(canDownload);
    }

    /**
     * 注册网络监听广播
     */
    private void regReceiver() {
        if (networkChangedReceiver == null) {
            networkChangedReceiver = new NetworkChangedReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangedReceiver, filter);
    }

    /***
     * 取消注册网络监听的广播
     */
    private void unRigReceiver() {
        unregisterReceiver(networkChangedReceiver);
    }

    private void outLog(String log) {
        Bundle bundle = new Bundle();
        bundle.putString(InnerListener.LOG_EXTRA, log);
        ListenerManager.getInstance().sendCall(InnerListener.OUT_LOG_ACTION, bundle);
    }

}
