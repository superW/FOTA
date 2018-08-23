package com.autoai.android.fotaframework;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String TAG = "FotaApp_MainActivity";

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);

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

        findViewById(R.id.setMobileNetworkDownloadBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = getServicentent();
                serviceIntent.putExtra("method", "setNetState");
                serviceIntent.putExtra("mobileDownload", true);
                startService(serviceIntent);
            }
        });

        findViewById(R.id.setMobileNetworkWaitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = getServicentent();
                serviceIntent.putExtra("method", "setNetState");
                serviceIntent.putExtra("mobileDownload", false);
                startService(serviceIntent);
            }
        });

        findViewById(R.id.reportUpgradeResultBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = getServicentent();
                serviceIntent.putExtra("method", "reportUpgradeResult");
                startService(serviceIntent);
            }
        });

    }

    private void stopFotaService() {
        Intent serviceIntent = getServicentent();
        stopService(serviceIntent);
        Log.e(TAG, "fota service stoped");
    }

    private void startFotaService() {
        Intent serviceIntent = getServicentent();
        startService(serviceIntent);
        Log.e(TAG, "fota service started");
    }

    private Intent getServicentent() {
        Intent serviceIntent = new Intent();
        serviceIntent.setAction("com.autoai.android.action.FotaService");
        return serviceIntent;
    }

}
