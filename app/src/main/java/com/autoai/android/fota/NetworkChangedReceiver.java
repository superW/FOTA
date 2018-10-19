package com.autoai.android.fota;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import com.autoai.android.fota.util.LogManager;


public class NetworkChangedReceiver extends BroadcastReceiver {

    private final String TAG = "FotaFramework";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (LogManager.isLoggable()) {
            LogManager.i(TAG, "NetworkChangedReceiver -- 网络状态发生变化");
        }

        //检测API是不是小于21，因为到了API 21之后getNetworkInfo(int networkType)方法被弃用
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (LogManager.isLoggable()) {
                LogManager.i(TAG, "NetworkChangedReceiver -- API level 小于21");
            }

            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取ConnectivityManager对象对应的NetworkInfo对象
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //获取移动数据连接的信息
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                if (LogManager.isLoggable()) {
                    LogManager.i(TAG, "NetworkChangedReceiver -- WIFI已连接,移动数据已连接");
                }
                setConn(context);
            } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
                if (LogManager.isLoggable()) {
                    LogManager.i(TAG, "NetworkChangedReceiver -- WIFI已连接,移动数据已断开");
                }
                setConn(context);
            } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                if (LogManager.isLoggable()) {
                    LogManager.i(TAG, "NetworkChangedReceiver -- WIFI已断开,移动数据已连接");
                }
                setConn(context);
            } else {
                if (LogManager.isLoggable()) {
                    LogManager.i(TAG, "NetworkChangedReceiver -- WIFI已断开,移动数据已断开");
                }
                setDisConn(context);
            }
        } else {
            if (LogManager.isLoggable()) {
                LogManager.i(TAG, "NetworkChangedReceiver -- API level 大于21");
            }

            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取所有当前已有连接上状态的网络连接的信息
            Network[] networks = connMgr.getAllNetworks();

            //用于记录最后的网络连接信息
            int result = 0;//mobile false = 1, mobile true = 2, wifi = 4

            //通过循环将网络信息逐个取出来
            for (int i = 0; i < networks.length; i++) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);

                //检测到有数据连接，但是并连接状态未生效，此种状态为wifi和数据同时已连接，以wifi连接优先
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE && !networkInfo.isConnected()) {
                    result += 1;
                }

                //检测到有数据连接，并连接状态已生效，此种状态为只有数据连接，wifi并未连接上
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE && networkInfo.isConnected()) {
                    result += 2;
                }

                //检测到有wifi连接，连接状态必为true
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    result += 4;
                }
            }

            //因为存在上述情况的组合情况，以组合相加的唯一值作为最终状态的判断
            switch (result) {
                case 0:
                    if (LogManager.isLoggable()) {
                        LogManager.i(TAG, "NetworkChangedReceiver -- WIFI已断开,移动数据已断开");
                    }
                    setDisConn(context);
                    break;
                case 2:
                    if (LogManager.isLoggable()) {
                        LogManager.i(TAG, "NetworkChangedReceiver -- WIFI已断开,移动数据已连接");
                    }
                    setConn(context);
                    break;
                case 4:
                    if (LogManager.isLoggable()) {
                        LogManager.i(TAG, "NetworkChangedReceiver -- WIFI已连接,移动数据已断开");
                    }
                    setConn(context);
                    break;
                case 5:
                    if (LogManager.isLoggable()) {
                        LogManager.i(TAG, "NetworkChangedReceiver -- WIFI已连接,移动数据已连接");
                    }
                    setConn(context);
                    break;
            }
        }
    }

    private void setConn(Context context) {
//        Intent serviceIntent = getServicentent();
//        serviceIntent.putExtra(FotaService.METHOD_EXTRA, FotaService.NET_STATE_METHOD);
//        serviceIntent.putExtra(FotaService.NET_STATE_EXTRA, true);
//        context.startService(serviceIntent);
    }

    private void setDisConn(Context context) {
//        Intent serviceIntent = getServicentent();
//        serviceIntent.putExtra(FotaService.METHOD_EXTRA, FotaService.NET_STATE_METHOD);
//        serviceIntent.putExtra(FotaService.NET_STATE_EXTRA, false);
//        context.startService(serviceIntent);
    }

//    private Intent getServicentent() {
//        Intent serviceIntent = new Intent();
//        serviceIntent.setAction("com.autoai.android.action.FotaService");
//        serviceIntent.setPackage("com.autoai.android.fotaframework");
//        return serviceIntent;
//    }

}
