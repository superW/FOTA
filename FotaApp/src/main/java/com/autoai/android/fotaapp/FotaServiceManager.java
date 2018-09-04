package com.autoai.android.fotaapp;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.autoai.android.aidl.FotaAidlListener;
import com.autoai.android.aidl.FotaAidlModelInfo;
import com.autoai.android.aidl.IFotaAidlInterface;
import com.autoai.android.utils.LogManager;

import java.util.List;

/**
 * Created by wangyanchao on 2018/8/7.
 */

public class FotaServiceManager {

    private static final String TAG = "FotaApp";

    private static final String serviceClassName = "com.autoai.android.fotaframework.FotaService";

    private IFotaAidlInterface fotaAidl;

    private FotaListener mFotaListener;

    private boolean isBinding = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            fotaAidl = IFotaAidlInterface.Stub.asInterface(iBinder);
            if (fotaAidl != null) {
                isBinding = true;
                try {
                    fotaAidl.setFotaListener(new FotaAidlListener.Stub() {
                        @Override
                        public void onUpgrade(List<FotaAidlModelInfo> modelInfos) throws RemoteException {
                            if (LogManager.isLoggable()) {
                                LogManager.e(TAG, "onUpgrade --> modelList=" + modelInfos);
                            }
                            if (mFotaListener != null) {
                                mFotaListener.onUpgrade(modelInfos);
                            }
                        }

                        @Override
                        public void onDownloading(FotaAidlModelInfo fotaModelInfo, float progress) throws RemoteException {
                            if (LogManager.isLoggable()) {
                                LogManager.e(TAG, "onDownloading --> progress=" + progress + ", model=" + fotaModelInfo);
                            }
                            if (mFotaListener != null) {
                                mFotaListener.onDownloading(fotaModelInfo, progress);
                            }
                        }

                        @Override
                        public void onFileDownloadSucceed(FotaAidlModelInfo modelInfo) throws RemoteException {
                            if (LogManager.isLoggable()) {
                                LogManager.e(TAG, "onFileDownloadSucceed --> model=" + modelInfo);
                            }
                            if (mFotaListener != null) {
                                mFotaListener.onFileDownloadSucceed(modelInfo);
                            }
                        }

                        @Override
                        public void onInstalling(FotaAidlModelInfo modelInfo, float progress) throws RemoteException {
                            if (LogManager.isLoggable()) {
                                LogManager.e(TAG, "onInstalling --> progress=" + progress + ", model=" + modelInfo);
                            }
                            if (mFotaListener != null) {
                                mFotaListener.onInstalling(modelInfo, progress);
                            }
                        }

                        @Override
                        public void onFinishInstall(FotaAidlModelInfo modelInfo) throws RemoteException {
                            if (LogManager.isLoggable()) {
                                LogManager.e(TAG, "onFinishInstall --> 安装完成 model=" + modelInfo);
                            }
                            if (mFotaListener != null) {
                                mFotaListener.onFinishInstall(modelInfo);
                            }
                        }
                    });
                } catch (RemoteException e) {
                    if (LogManager.isLoggable()) {
                        LogManager.e(TAG, e.getMessage(), e);
                    }
                }
            } else {
                if (LogManager.isLoggable()) {
                    LogManager.e(TAG, "FOTA aidl is null.");
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private static FotaServiceManager INSTANCE;

    private FotaServiceManager() {
    }

    public static FotaServiceManager getInstance() {
        if (INSTANCE == null) {
            synchronized (FotaServiceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FotaServiceManager();
                }
            }
        }
        return INSTANCE;
    }

    public void bindService(Context context) {
        Intent serviceIntent = new Intent();
        serviceIntent.setAction("com.autoai.android.action.FotaService");
        serviceIntent.setPackage("com.autoai.android.fotaframework");
        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (LogManager.isLoggable()) {
            LogManager.e(TAG, "bind FotaService");
        }
    }

    public void unbindService(Context context) {
        if (isBinding && isServiceRun(context, serviceClassName)) {
            context.unbindService(serviceConnection);
            isBinding = false;
            if (LogManager.isLoggable()) {
                LogManager.e(TAG, "unbind FotaService");
            }
        } else {
            if (LogManager.isLoggable()) {
                LogManager.e(TAG, "no FOTA service");
            }
        }
    }

    public void downloadModels(List<FotaAidlModelInfo> models) {
        if (fotaAidl != null) {
            try {
                fotaAidl.download(models);
                if (LogManager.isLoggable()) {
                    LogManager.e(TAG, "download models");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            if (LogManager.isLoggable()) {
                LogManager.e(TAG, "FOTA aidl is null.");
            }
        }
    }

    public void installModels(List<FotaAidlModelInfo> models) {
        if (fotaAidl != null) {
            try {
                fotaAidl.installModels(models);
                if (LogManager.isLoggable()) {
                    LogManager.e(TAG, "install models");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            if (LogManager.isLoggable()) {
                LogManager.e(TAG, "FOTA aidl is null.");
            }
        }
    }

    public void setListener(FotaListener fotaListener) {
        this.mFotaListener = fotaListener;
        if (LogManager.isLoggable()) {
            LogManager.e(TAG, "setListener --> fotaListener=" + fotaListener);
        }
    }

    public interface FotaListener {
        void onUpgrade(List<FotaAidlModelInfo> modelInfos);

        void onDownloading(FotaAidlModelInfo fotaAidlModelInfo, float progress);

        void onFileDownloadSucceed(FotaAidlModelInfo modelInfo);

        void onInstalling(FotaAidlModelInfo fotaAidlModelInfo, float progress);

        void onFinishInstall(FotaAidlModelInfo fotaAidlModelInfo);
    }

    /**
     * 判断服务是否后台运行
     *
     * @param context   Context
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    private static boolean isServiceRun(Context context, String className) {
        boolean isRun = false;
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(40);
        int size = serviceList.size();
        for (int i = 0; i < size; i++) {
            String cn = serviceList.get(i).service.getClassName();
//            if (LogManager.isLoggable()) {
//                LogManager.e(TAG, i + ",cn=" + cn + ", className=" + className);
//            }
            if (cn.equals(className) == true) {
                isRun = true;
                break;
            }
        }
        return isRun;
    }

}
