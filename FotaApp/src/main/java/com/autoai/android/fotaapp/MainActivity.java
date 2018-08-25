package com.autoai.android.fotaapp;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.autoai.android.aidl.FotaAidlModelInfo;
import com.autoai.android.utils.LogManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = "FotaApp";

    private TextView textView;

    private List<FotaAidlModelInfo> fotaAidlModelInfoList = new ArrayList<FotaAidlModelInfo>();

    private List<FotaAidlModelInfo> fotaModelWaiInstallList = new ArrayList<FotaAidlModelInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);

        findViewById(R.id.bindBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FotaServiceManager.getInstance().bindService(getApplicationContext());
                Toast.makeText(getApplicationContext(), "bind service", Toast.LENGTH_SHORT).show();

                FotaServiceManager.getInstance().setListener(new FotaServiceManager.FotaListener() {
                    @Override
                    public void onUpgrade(List<FotaAidlModelInfo> modelInfos) {
                        fotaAidlModelInfoList = modelInfos;
                        if (LogManager.isLoggable()) {
                            LogManager.e(TAG, "copy list to fotaAidlModelInfoList=" + fotaAidlModelInfoList);
                        }

                        ArrayList<String> modelList = new ArrayList<String>();
                        for (FotaAidlModelInfo fotaAidlModelInfo : fotaAidlModelInfoList) {
                            String name = fotaAidlModelInfo.getModelName();
                            if (TextUtils.isEmpty(name)) {
                                name = fotaAidlModelInfo.getUpdateModelTaskInfoList().get(0).getModelName();
                            }
                            modelList.add(name);
                        }
                        appendText("可进行下载的model列表" + modelList);
                    }

                    @Override
                    public void onDownloading(FotaAidlModelInfo fotaAidlModelInfo, float progress) {
                        String printProgress = "onDownloading --> modelName=" + fotaAidlModelInfo.getModelName() + ", progress=" + (progress*100);
                        appendText(printProgress);
                        if (LogManager.isLoggable()) {
                            LogManager.e(TAG, "onDownloading --> progress=" + (progress*100) + ", modelInfo=" + fotaAidlModelInfo);
                        }
                    }

                    @Override
                    public void onFileDownloadSucceed(final FotaAidlModelInfo modelInfo) {
                        appendText(modelInfo.getModelName() + "下载完成，等待安装");
                        fotaModelWaiInstallList.add(modelInfo);
                    }

                    @Override
                    public void onInstalling(FotaAidlModelInfo fotaAidlModelInfo, float progress) {
                        String printProgress = "onInstalling --> modelName=" + fotaAidlModelInfo.getModelName() + ", progress=" + (progress*100);
                        appendText(printProgress);
                        if (LogManager.isLoggable()) {
                            LogManager.e(TAG, "onInstalling --> progress=" + (progress*100) + ", modelInfo=" + fotaAidlModelInfo);
                        }
                    }
                });
            }
        });

        findViewById(R.id.unBindBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FotaServiceManager.getInstance().unbindService(getApplicationContext());
                Toast.makeText(getApplicationContext(), "unbind service", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.downloadBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FotaServiceManager.getInstance().downloadModels(fotaAidlModelInfoList);
                Toast.makeText(getApplicationContext(), "download models", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.installBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FotaServiceManager.getInstance().installModels(fotaModelWaiInstallList);
                Toast.makeText(getApplicationContext(), "install models", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FotaServiceManager.getInstance().unbindService(getApplicationContext());
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