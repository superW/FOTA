package com.autoai.android.fotaapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.autoai.android.aidl.FotaAidlModelInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = "FotaAppMainActivity";

    private TextView textView;

    private List<FotaAidlModelInfo> fotaAidlModelInfoList = new ArrayList<FotaAidlModelInfo>();

    private List<FotaAidlModelInfo> fotaModelWaiInstallList = new ArrayList<FotaAidlModelInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);

        findViewById(R.id.bindBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FotaServiceManager.getInstance().bindService(getApplicationContext());
                Toast.makeText(getApplicationContext(), "bind service", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "bind service");
                FotaServiceManager.getInstance().setListener(new FotaServiceManager.FotaListener() {
                    @Override
                    public void onUpgrade(List<FotaAidlModelInfo> modelInfos) {
//                        Log.e(TAG, "onUpgrade--modelInfos=" + modelInfos);
                        fotaAidlModelInfoList = modelInfos;
                        Log.e(TAG, "copy list to fotaAidlModelInfoList=" + fotaAidlModelInfoList);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText("onUpgrade ok, please download all of models.\n");
                            }
                        });
                    }

                    @Override
                    public void onDownloading(FotaAidlModelInfo fotaAidlModelInfo, float progress) {
//                        Log.e(TAG, "onDownloading=" + progress + ", modelInfo=" + fotaAidlModelInfo);
                    }

                    @Override
                    public void onFileDownloadSucceed(final FotaAidlModelInfo modelInfo) {
//                        Log.e(TAG, "onFileDownloadSucceed--modelInfo=" + modelInfo);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.append(modelInfo.getModelName() + " can install.\n");
                            }
                        });
                        fotaModelWaiInstallList.add(modelInfo);
                    }

                    @Override
                    public void onProgress(FotaAidlModelInfo fotaAidlModelInfo, float progress) {
//                        Log.e(TAG, "onProgress=" + progress + ", modelInfo=" + fotaAidlModelInfo);
                    }
                });
                Log.e(TAG, "Set Fota Listener");
            }
        });

        findViewById(R.id.unBindBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FotaServiceManager.getInstance().unbindService(getApplicationContext());
                Toast.makeText(getApplicationContext(), "unbind service", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "unbind service");
            }
        });

        findViewById(R.id.downloadBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "download models", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "download models");

                FotaServiceManager.getInstance().downloadModels(fotaAidlModelInfoList);
            }
        });

        findViewById(R.id.installBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "install models", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "install models");

                FotaServiceManager.getInstance().installModels(fotaModelWaiInstallList);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FotaServiceManager.getInstance().unbindService(getApplicationContext());
    }

}
