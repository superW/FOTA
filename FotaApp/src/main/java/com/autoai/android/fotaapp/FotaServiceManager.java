package com.autoai.android.fotaapp;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.autoai.android.aidl.FotaAidlListener;
import com.autoai.android.aidl.FotaAidlModelInfo;
import com.autoai.android.aidl.IFotaAidlInterface;

import java.util.List;

/**
 * Created by wangyanchao on 2018/8/7.
 */

public class FotaServiceManager {

    private static final String TAG = "FotaServiceManager";

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
                            Log.e(TAG, "onUpgrade--modelInfos=" + modelInfos);
                            if (mFotaListener != null) {
                                mFotaListener.onUpgrade(modelInfos);
                            }
                        }

                        @Override
                        public void onDownloading(FotaAidlModelInfo fotaModelInfo, float progress) throws RemoteException {
                            Log.e(TAG, "onDownloading=" + progress + ", modelInfo=" + fotaModelInfo);
                            if (mFotaListener != null) {
                                mFotaListener.onDownloading(fotaModelInfo, progress);
                            }
                        }

                        @Override
                        public void onFileDownloadSucceed(FotaAidlModelInfo modelInfo) throws RemoteException {
                            Log.e(TAG, "onFileDownloadSucceed--modelInfo=" + modelInfo);
                            if (mFotaListener != null) {
                                mFotaListener.onFileDownloadSucceed(modelInfo);
                            }
                        }

                        @Override
                        public void onProgress(FotaAidlModelInfo modelInfo, float progress) throws RemoteException {
                            Log.e(TAG, "onProgress=" + progress + ", modelInfo=" + modelInfo);
                            if (mFotaListener != null) {
                                mFotaListener.onProgress(modelInfo, progress);
                            }
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "fota aidl is null.");
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
        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        Log.e(TAG, "bind FotaService");
    }

    public void unbindService(Context context) {
        if (isBinding && isServiceRun(context, serviceClassName)) {
            context.unbindService(serviceConnection);
            isBinding = false;
            Log.e(TAG, "unbind FotaService");
        } else {
            Log.e(TAG, "no fota service");
        }
    }

    public void downloadModels(List<FotaAidlModelInfo> models) {
        if (fotaAidl != null) {
            try {
                fotaAidl.download(models);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "fota aidl is null.");
        }
    }

    public void installModels(List<FotaAidlModelInfo> models) {
        if (fotaAidl != null) {
            try {
                fotaAidl.installModels(models);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "fota aidl is null.");
        }
    }

    public void setListener(FotaListener fotaListener) {
        this.mFotaListener = fotaListener;
    }

    public interface FotaListener {
        void onUpgrade(List<FotaAidlModelInfo> modelInfos);

        void onDownloading(FotaAidlModelInfo fotaAidlModelInfo, float progress);

        void onFileDownloadSucceed(FotaAidlModelInfo modelInfo);

        void onProgress(FotaAidlModelInfo fotaAidlModelInfo, float progress);
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
//            Log.e(TAG, i + ",cn=" + cn + ", className=" + className);
            if (cn.equals(className) == true) {
                isRun = true;
                break;
            }
        }
        return isRun;
    }

}
