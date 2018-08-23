package com.autoai.android.fotaframework;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.autoai.android.aidl.FotaAidlListener;
import com.autoai.android.aidl.FotaAidlModelInfo;
import com.autoai.android.aidl.IFotaAidlInterface;
import com.autoai.android.fota.api.MFOTAAPIManager;
import com.autoai.android.fota.api.MFOTAListener;
import com.autoai.android.fota.constant.FOTACode;
import com.autoai.android.fota.model.FOTADeviceInfo;
import com.autoai.android.fota.model.FOTAModelInfo;
import com.autoai.android.fota.model.ResultInfo;
import com.autoai.android.fotaframework.utils.FotaModelInfoFormater;
import com.autoai.android.fotaframework.utils.SharedPreferencesUtil;
import com.autoai.android.fotaframework.utils.ThreadPoolManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FotaService extends Service implements MFOTAListener {

    private static final String TAG = "Framework-FotaService";

    private FotaAidlListener fotaAidlListener;

    private ThreadPoolManager threadPoolManager;

    private MFOTAAPIManager mfotaapiManager;
    private boolean fotaSdkInitResult = false;

    private static final int UPDATE_INSTALL_PROGRESS = 1;

    private Handler mainHandler;

    private NetworkChangedReceiver networkChangedReceiver;

    public FotaService() {
        Log.e(TAG, "Constructor --> ");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate --> ");

        threadPoolManager = ThreadPoolManager.newInstance();
        threadPoolManager.prepare();

        mainHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_INSTALL_PROGRESS:
                        if (fotaAidlListener != null) {
                            try {
                                FotaAidlModelInfo fotaAidlModelInfo = (FotaAidlModelInfo) msg.obj;
                                fotaAidlListener.onProgress(fotaAidlModelInfo, msg.arg1);
                                if (msg.arg1 == 100) { // 成功的时候上报服务器
                                    FOTAModelInfo modelInfo = FotaModelInfoFormater.format(fotaAidlModelInfo);
                                    // 调用SDK上报接口
                                    mfotaapiManager.setUpdateResult(modelInfo, true, "{}");
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
            }
        };

        regReceiver();

        initSDK();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand --> " + intent);
        if (intent != null) {
            String method = intent.getStringExtra("method");
            if ("reportUpgradeResult".equals(method)) {
                FOTAModelInfo fotaModelInfo = new FOTAModelInfo();
                fotaModelInfo.setModelName("testModelName0");
                reportUpgradeResult(fotaModelInfo, true, "升级完成");
            } else if ("setMobileNetworkDownload".equals(method)) {
                boolean mobileDownload = intent.getBooleanExtra("mobileDownload", false);
                setMobileNetworkDownload(mobileDownload);
            } else if ("setNetState".equals(method)) {
                boolean netState = intent.getBooleanExtra("netState", false);
                setNetState(netState);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind --> ");
        return fotaAidl;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind --> ");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy --> ");
        threadPoolManager.shutdown();
        unRigReceiver();
        mfotaapiManager.stop();
    }

    private IFotaAidlInterface.Stub fotaAidl = new IFotaAidlInterface.Stub() {

        @Override
        public void download(List<FotaAidlModelInfo> modelInfos) throws RemoteException {
            Log.e(TAG, "download --> " + modelInfos);
            List<FOTAModelInfo> fotaModelInfos = FotaModelInfoFormater.formatAidlList(modelInfos);
            downloadModels(fotaModelInfos);
        }

        @Override
        public void installModels(List<FotaAidlModelInfo> modelInfos) throws RemoteException {
            Log.e(TAG, "installModels --> " + modelInfos);
            List<FOTAModelInfo> fotaModelInfos = FotaModelInfoFormater.formatAidlList(modelInfos);
            installFotaModels(fotaModelInfos);
        }

        @Override
        public void setFotaListener(FotaAidlListener fotaListener) throws RemoteException {
            Log.e(TAG, "setFotaListener --> " + fotaListener);
            fotaAidlListener = fotaListener;
        }
    };

    @Override
    public void onInitResult(boolean b, String s) {
        // TODO SDK初始化结果
        Log.e(TAG, "onInitResult --> ");

        fotaSdkInitResult = b;
        if (fotaSdkInitResult) {
            Log.e(TAG, "collectDeviceInfo >> ");
            collectDeviceInfo();
        } else {
            Log.e(TAG, "fota sdk init failed reason is " + s);
        }
    }

    @Override
    public void onUpload(List<FOTAModelInfo> list) {
        // TODO 返回的升级任务
        Log.e(TAG, "onUpload --> " + list);

        if (fotaAidlListener != null) {
            List<FotaAidlModelInfo> fotaAidlModelInfos = FotaModelInfoFormater.formatList(list);
            try {
                // 提示用户层显示可以升级的model任务
                fotaAidlListener.onUpgrade(fotaAidlModelInfos);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDownloading(FOTAModelInfo fotaModelInfo, float v) {
        // TODO 下载进度提醒
        Log.e(TAG, "onDownloading --> " + "progress=" + v + ", modelInfo=" + fotaModelInfo);

        if (fotaAidlListener != null) {
            try {
                FotaAidlModelInfo fotaAidlModelInfo = FotaModelInfoFormater.format(fotaModelInfo);
                fotaAidlListener.onDownloading(fotaAidlModelInfo, v);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDownloadResult(FOTAModelInfo fotaModelInfo, boolean b, String s) {
        // TODO 下载结果（下载文件已经校验过并成功返回结果），可通知应用层进行提示用户进行刷写升级
        Log.e(TAG, "onDownloadResult --> " + b + ", result=" + s + ", modelInfo=" + fotaModelInfo);
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
        Log.e(TAG, "onModelInfoIllegal --> " + list.toString());

    }

    @Override
    public void onAllowDownloadFileResult(Map<FOTAModelInfo.UpdateModelTaskInfo, ResultInfo> map) {
        // TODO 确认下载的所有文件信息，ResultInfo中code=0表示可以下载，code=1不可以下载，不予下载的文件可以对应用层的用户进行提示
        Log.e(TAG, "onAllowDownloadFileResult --> " + map);

    }

    private void initSDK() {
        mfotaapiManager = new MFOTAAPIManager(this, this);
        FOTADeviceInfo deviceInfo = new FOTADeviceInfo();
        deviceInfo.setDeviceKey("LGBM00105");
        deviceInfo.setDeviceGroupInfo("11");
        deviceInfo.setDeviceType("maiteng");
        mfotaapiManager.start(deviceInfo);
    }

    private void collectDeviceInfo() {
        List<FOTAModelInfo> mFOTAModelInfoList = new ArrayList<FOTAModelInfo>();

        int mv1 = SharedPreferencesUtil.SharedPreferencesSelect_int(this, "testModel1", 10);
        int mv2 = SharedPreferencesUtil.SharedPreferencesSelect_int(this, "sunxi2", 10);
        int mv3 = SharedPreferencesUtil.SharedPreferencesSelect_int(this, "sunxi3", 10);
        Log.e(TAG, "collectDeviceInfo: modelVersion1=" + mv1 +
                ", modelVersion2=" + mv2 +
                ", modelVersion3=" + mv3
        );

        FOTAModelInfo mFOTAModelInfo = new FOTAModelInfo();
        mFOTAModelInfo.setModelName("testModel1"); //testModel1
        mFOTAModelInfo.setModelCurrentVersion(mv1); // 更新版本
        mFOTAModelInfo.setModelUpdateTime(System.currentTimeMillis());
        mFOTAModelInfo.setSystemCurrentVersion(2); //系统版本
        mFOTAModelInfoList.add(mFOTAModelInfo);

        FOTAModelInfo mFOTAModelInfo1 = new FOTAModelInfo();
        mFOTAModelInfo1.setModelName("sunxi2"); //model名
        mFOTAModelInfo1.setModelCurrentVersion(mv2); // 更新版本
        mFOTAModelInfo1.setModelUpdateTime(System.currentTimeMillis());
        mFOTAModelInfo1.setSystemCurrentVersion(2); //系统版本
        mFOTAModelInfoList.add(mFOTAModelInfo1);

        FOTAModelInfo mFOTAModelInfo2 = new FOTAModelInfo();
        mFOTAModelInfo2.setModelName("sunxi3"); //model名
        mFOTAModelInfo2.setModelCurrentVersion(mv3); // 更新版本
        mFOTAModelInfo2.setModelUpdateTime(System.currentTimeMillis());
        mFOTAModelInfo2.setSystemCurrentVersion(2); //系统版本
        mFOTAModelInfoList.add(mFOTAModelInfo2);

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

    public void reportUpgradeResult(FOTAModelInfo fotaModelInfo, boolean success, String json) {
        mfotaapiManager.setUpdateResult(fotaModelInfo, success, json);
    }

    public void setNetState(boolean conn) {
        if (conn) {
            mfotaapiManager.setNetStatus(FOTACode.STATUS_NET_CONNECTED);
        } else {
            mfotaapiManager.setNetStatus(FOTACode.STATUS_NET_DISCONNECTED);
        }
    }

    public void setMobileNetworkDownload(boolean canDownload) {
        mfotaapiManager.setMobileNetworkDownload(canDownload);
    }

    private void regReceiver() {
        if (networkChangedReceiver == null) {
            networkChangedReceiver = new NetworkChangedReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangedReceiver, filter);
    }

    private void unRigReceiver() {
        unregisterReceiver(networkChangedReceiver);
    }

}
