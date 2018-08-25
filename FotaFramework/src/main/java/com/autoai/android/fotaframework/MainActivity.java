package com.autoai.android.fotaframework;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.autoai.android.aidl.FotaAidlModelInfo;
import com.autoai.android.callback.InnerListener;
import com.autoai.android.callback.ListenerManager;
import com.autoai.android.fotaframework.utils.LogManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = "FotaFramework";

    private TextView textView;
    private ScrollView scrollView;
    private Button collectDeviceInfoButton;
    private Button mobileDownloadButton;

    private boolean isMobileDownload = true;

    private boolean sdkInitResult;

    private static boolean isQuit = false;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isQuit = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        findViewById(R.id.startServiceBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFotaService();
            }
        });

        findViewById(R.id.stopServiceBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopFotaService();
            }
        });

        collectDeviceInfoButton = (Button) findViewById(R.id.collectModelInfosBtn);
        collectDeviceInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectDeviceInfo();
            }
        });
        collectDeviceInfoButton.setEnabled(sdkInitResult);

        mobileDownloadButton = (Button) findViewById(R.id.setMobileNetworkDownloadBtn);
        mobileDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMobileDownload = !isMobileDownload;
                setMobileDownloadStateBtnText();
                setMobileDownloadState();
            }
        });
        setMobileDownloadStateBtnText();

        findViewById(R.id.updateTextBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.rollbackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        setCallBack();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ListenerManager.getInstance().removeAllListener();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isQuit) {
                isQuit = true;
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                // 利用handler延迟发送更改状态信息
                mHandler.sendEmptyMessageDelayed(0, 2000);
                return false;
            } else {
                finish();
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 服务的回调结果监听器设置，用来显示在界面上
     */
    private void setCallBack() {
        ListenerManager.getInstance().addListener(new InnerListener() {
            @Override
            public void onReceive(String action, Bundle bundle) {
                if (!TextUtils.isEmpty(action)) {
                    if (InnerListener.INIT_RESULT_ACTION.equals(action)) {
                        /* 初始化结果 */
                        if (bundle != null) {
                            boolean result = bundle.getBoolean(InnerListener.RESULT_EXTRA);
                            String reason = bundle.getString(InnerListener.REASON_EXTRA);
                            String printInitResult = "SDK初始化" + (result ? "成功" : ("失败, 原因：" + reason));
                            sdkInitResult = result;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    collectDeviceInfoButton.setEnabled(sdkInitResult);
                                }
                            });
                            appendText(printInitResult);
                            if (LogManager.isLoggable()) {
                                LogManager.e(TAG, printInitResult);
                            }
                        }
                    } else if (InnerListener.UPGRADE_LIST_ACTION.equals(action)) {
                        /* 升级列表信息 */
                        if (bundle != null) {
                            List<FotaAidlModelInfo> fotaAidlModelInfoList =
                                    bundle.getParcelableArrayList(InnerListener.UPGRADE_LIST_EXTRA);
                            List<String> modelNames = new ArrayList<String>();
                            for (FotaAidlModelInfo.UpdateModelTaskInfo updateModelTaskInfo :
                                    fotaAidlModelInfoList.get(0).getUpdateModelTaskInfoList()) {
                                modelNames.add(updateModelTaskInfo.getModelName());
                            }
                            String printDownloadModels = "可进行下载的model列表" + modelNames.toString();
                            appendText(printDownloadModels);
                            if (LogManager.isLoggable()) {
                                LogManager.e(TAG, printDownloadModels);
                            }
                        }
                    } else if (InnerListener.DOWNLOADING_ACTION.equals(action)) {
                        /* model下载进度 */
                        if (bundle != null) {
                            FotaAidlModelInfo fotaAidlModelInfo = bundle.getParcelable(InnerListener.DOWNLOADING_MODEL_EXTRA);
                            float progress = bundle.getFloat(InnerListener.DOWNLOADING_PROG_EXTRA);
                            String printModelDownloadProg = "modelName=" + fotaAidlModelInfo.getModelName() + ", onDownloading=" + (progress * 100);
                            appendText(printModelDownloadProg);
                            if (LogManager.isLoggable()) {
                                LogManager.e(TAG, printModelDownloadProg);
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 收集model信息，并上报，检查model更新
     */
    private void collectDeviceInfo() {
        Intent serviceIntent = getServicentent();
        serviceIntent.putExtra(FotaService.METHOD_EXTRA, FotaService.COLLECT_DEVICE_INFO_METHOD);
        startService(serviceIntent);
    }

    private void setMobileDownloadStateBtnText() {
        String mobileDownload = "移动网络下载" + (isMobileDownload ? "已开启" : "已关闭");
        mobileDownloadButton.setText(mobileDownload);
    }

    /**
     * 设置移动网络数据是否可以进行下载
     */
    private void setMobileDownloadState() {
        Intent serviceIntent = getServicentent();
        serviceIntent.putExtra(FotaService.METHOD_EXTRA, FotaService.MOBILE_DOWNLOAD_METHOD);
        serviceIntent.putExtra(FotaService.MOBILE_DOWNLOAD_EXTRA, isMobileDownload);
        startService(serviceIntent);
    }

    /**
     * 停止服务
     */
    private void stopFotaService() {
        Intent serviceIntent = getServicentent();
        stopService(serviceIntent);
        appendText("FOTA service stoped");
    }

    /**
     * 开启服务
     */
    private void startFotaService() {
        Intent serviceIntent = getServicentent();
        startService(serviceIntent);
        appendText("FOTA service started");

    }

    /**
     * 获取 fota服务 intent
     *
     * @return
     */
    private Intent getServicentent() {
        Intent serviceIntent = new Intent();
        serviceIntent.setAction("com.autoai.android.action.FotaService");
        return serviceIntent;
    }

    /**
     * TextView显示文字
     *
     * @param text
     */
    private synchronized void showText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(text + "\n");
            }
        });
    }

    /**
     * TextView追加文字
     *
     * @param text
     */
    private synchronized void appendText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.append(text + "\n");
            }
        });
    }

}
