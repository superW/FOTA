package com.autoai.android.fota;

import android.app.Activity;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.autoai.android.fota.api.MFOTAAPIManager;
import com.autoai.android.fota.api.MFOTAListener;
import com.autoai.android.fota.model.FOTADeviceInfo;
import com.autoai.android.fota.model.FOTAModelInfo;
import com.autoai.android.fota.model.ResultInfo;
import com.autoai.android.fota.util.FileUtils;
import com.autoai.android.fota.util.LogManager;
import com.autoai.android.fota.util.SharedPreferencesUtil;
import com.autoai.android.fota.util.ThreadPoolManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity implements MFOTAListener {

    private static final String TAG = "FOTA_DEMO";
    private static final String configTxtFilePath = "/mnt/sdcard/autoai/deviceInfo.txt";

    private TextView textView;
    private ScrollView scrollView;

    private Button collectDeviceInfoButton;
    private Button mobileDownloadButton;
    private Button autoUpdateTextButton;
    // 回滚model名称
    private EditText modelNameEditText;
    // 回滚
    private Button rollbackButton;
    // 回滚布局
    private LinearLayout rollbackLL;
    // 回滚布局是否隐藏
    private boolean isRollbackLayoutHidden = true;

    // 界面显示的文本信息是否自动刷新
    private boolean isAutoUpdateText;
    // 界面显示的行号
    private int lineNum;
    // 是否在使用移动网络数据时可下载
    private boolean isMobileDownload = true;

    // FOTA API接口类
    private MFOTAAPIManager mfotaapiManager;
    // 线程池管理工具类
    private ThreadPoolManager threadPoolManager;
    // 网络监听广播接收器
    private NetworkChangedReceiver networkChangedReceiver;
    // 设备信息（初始化时必传的参数）
    private FOTADeviceInfo mDeviceInfo;
    // 采集model信息的列表
    private List<FOTAModelInfo> colFOTAModelInfoList = new ArrayList<FOTAModelInfo>();
    // 可进行下载的model列表
    private List<FOTAModelInfo> fotaModelInfoDownOkList = new ArrayList<FOTAModelInfo>();
    // 等待安装的model列表
    private List<FOTAModelInfo> fotaModelWaiInstallList = new ArrayList<FOTAModelInfo>();
    // 初始化起始时间
    private long initStartMillis;
    // 初始化结束时间
    private long initEndMillis;
    // FOTA SDK初始化成功与否
    private boolean sdkInitOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());

        findViewById(R.id.startFOTABtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFOTA();
            }
        });

        findViewById(R.id.stopFOTABtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopFOTA();
            }
        });

        collectDeviceInfoButton = (Button) findViewById(R.id.collectModelInfosBtn);
        collectDeviceInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectDeviceInfo();
            }
        });
        collectDeviceInfoButton.setEnabled(sdkInitOk);

        findViewById(R.id.downloadBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadModels(fotaModelInfoDownOkList);
            }
        });

        findViewById(R.id.installBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                installFotaModels(fotaModelWaiInstallList);
            }
        });

        mobileDownloadButton = (Button) findViewById(R.id.setMobileNetworkDownloadBtn);
        mobileDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMobileDownload = !isMobileDownload;
                setMobileDownloadStateBtnText();
                setMobileNetworkDownload(isMobileDownload);
            }
        });
        setMobileDownloadStateBtnText();

        autoUpdateTextButton = (Button) findViewById(R.id.updateTextBtn);
        autoUpdateTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAutoUpdateText = !isAutoUpdateText;
                setAutoUpdateTextBtnText();
            }
        });
        setAutoUpdateTextBtnText();

        findViewById(R.id.rollbackSetBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRollbackLayoutHidden = !isRollbackLayoutHidden;
                optRollbackLayout(isRollbackLayoutHidden);
            }
        });

        rollbackLL = (LinearLayout) findViewById(R.id.rollbackLL);
        optRollbackLayout(isRollbackLayoutHidden);

        modelNameEditText = (EditText) findViewById(R.id.modelNameEditText);

        rollbackButton = (Button) findViewById(R.id.rollbackButton);
        rollbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String modelName = modelNameEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(modelName)) {
                    rollback(modelName);
                    modelNameEditText.setText("");
                    appendText(modelName + " 回滚的请求已提交");
//                    showText("回滚结果在日志中可以看到，日志路径/mnt/sdcrad/autoai/fotadebug/*.log");
                } else {
                    showText("啥也不填~逗我玩呢！");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopFOTA();
        if (LogManager.isLoggable()) {
            LogManager.e(TAG, "onDestroy --> ");
        }
    }

    /**
     * 启动FOTA
     */
    private void startFOTA() {
        threadPoolManager = ThreadPoolManager.newInstance();
        threadPoolManager.prepare();

        regReceiver();

        loadLocalData(); // 加载数据结束initSDK();
    }

    /**
     * 停止FOTA
     */
    private void stopFOTA() {
        if (threadPoolManager != null) {
            threadPoolManager.shutdown();
        }
        if (networkChangedReceiver != null) {
            unRigReceiver();
        }
        if (mfotaapiManager != null) {
            mfotaapiManager.stop();
        }
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
        if (TextUtils.isEmpty(configInfo)) {
            if (LogManager.isLoggable()) {
                LogManager.e(TAG, "config file deviceInfo.txt is null.");
            }
            return;
        }
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
                colFOTAModelInfoList.add(fOTAModelInfo);
            }
            if (LogManager.isLoggable()) {
                LogManager.e(TAG, "analysisConfig --> FOTAModelInfoList=" + colFOTAModelInfoList);
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
        mfotaapiManager = new MFOTAAPIManager(MainActivity.this, MainActivity.this);
        /*901：显示在控制台和文件
        902：显示在控制台，不显示在文件
        903：显示文件中，不显示在控制台
        904：控制台和文件都不存在日志*/
        mfotaapiManager.setSL(902);
        mfotaapiManager.start(mDeviceInfo);
    }

    /**
     * 采集设备model信息
     */
    private void collectDeviceInfo() {
        mfotaapiManager.setAppInfo(colFOTAModelInfoList);
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
     * 用户选择下载
     */
    private void downloadModels(List<FOTAModelInfo> modelInfos) {
        mfotaapiManager.allowDownloadFile(modelInfos);
    }

    /**
     * 用户选择安装
     */
    private void installFotaModels(List<FOTAModelInfo> modelInfos) {
        for (FOTAModelInfo modelInfo : modelInfos) {
            String printLog = "安装完成 model=" + modelInfo.getModelName();
            appendText(printLog);

            // 调用SDK上报接口
            reportUpgradeResult(modelInfo, true, "{}");
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
     * 版本回滚操作
     *
     * @param modelName
     */
    private void rollback(String modelName) {
        FOTAModelInfo fotaModelInfo = new FOTAModelInfo();
        fotaModelInfo.setModelName(modelName);
        mfotaapiManager.setRollBack(fotaModelInfo);
    }

    /**
     * 对回滚操作的布局操作
     *
     * @param hidden true:隐藏;false:显示
     */
    private void optRollbackLayout(boolean hidden) {
        if (hidden) {
            rollbackLL.setVisibility(View.GONE);
        } else {
            rollbackLL.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onInitResult(boolean b, String s) {
        // TODO SDK初始化结果
        initEndMillis = SystemClock.uptimeMillis();
        long time = initEndMillis - initStartMillis;
        String log = "初始化SDK所用时间：" + time + " ms";
        appendText(log);
        String printInitResult = "SDK初始化" + (b ? "成功." : ("失败, 原因:" + s));
        appendText(printInitResult);
        if (LogManager.isLoggable()) {
            LogManager.e(TAG, "onInitResult --> run in " + (isMain() ? "main" : "child") + " thread.");
            LogManager.e(TAG, "onInitResult --> " + log);
            LogManager.e(TAG, "onInitResult --> " + printInitResult);
        }

        sdkInitOk = b;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                collectDeviceInfoButton.setEnabled(sdkInitOk);
            }
        });
    }

    /**
     * 只有有更新的时候才调用
     *
     * @param list
     */
    @Override
    public void onUpload(List<FOTAModelInfo> list) {
        // TODO 返回的升级任务
        if (LogManager.isLoggable()) {
            LogManager.e(TAG, "onUpload --> " + list);
        }
        fotaModelInfoDownOkList = list;

        ArrayList<String> modelList = new ArrayList<String>();
        for (int i = 0; i < fotaModelInfoDownOkList.size(); i++) {
            FOTAModelInfo fotaModelInfo = fotaModelInfoDownOkList.get(i);
            if (fotaModelInfo != null) {
                List<FOTAModelInfo.UpdateModelTaskInfo> updateModelTaskInfos = fotaModelInfo.getUpdateModelTaskInfoList();
                for (FOTAModelInfo.UpdateModelTaskInfo updateModelTaskInfo : updateModelTaskInfos) {
                    String name = updateModelTaskInfo.getModelName();
                    if (!TextUtils.isEmpty(name)) {
                        modelList.add(name);
                    }
                }
            }
        }
        appendText("可进行下载的model列表" + modelList);
    }

    /**
     * 每次检查更新都会回调
     *
     * @param b
     * @param list
     */
    @Override
    public void onUpload(boolean b, List<FOTAModelInfo> list) {
        // TODO 返回的升级任务
        if (LogManager.isLoggable()) {
            LogManager.e(TAG, "onUpload --> bool=" + b + ", list=" + list);
        }
    }

    @Override
    public void onDownloading(FOTAModelInfo fotaModelInfo, float progress) {
        String printProgress = "onDownloading --> modelName=" + fotaModelInfo.getModelName() + ", progress=" + (progress * 100);
        appendText(printProgress);
        if (LogManager.isLoggable()) {
            LogManager.e(TAG, "onDownloading --> progress=" + (progress * 100) + ", modelInfo=" + fotaModelInfo);
        }
    }

    @Override
    public void onDownloadResult(FOTAModelInfo fotaModelInfo, boolean b, String s) {
        // TODO 下载结果（下载文件已经校验过并成功返回结果），可通知应用层进行提示用户进行刷写升级
        if (LogManager.isLoggable()) {
            String log = "onDownloadResult --> model=" + fotaModelInfo.getModelName() +
                    ", 下载及文件校验 " + (b ? ("成功, " + s) : ("失败, failed reason=" + s)) +
                    ", modelInfo=" + fotaModelInfo;
            LogManager.e(TAG, log);
        }

        SharedPreferencesUtil.SharedPreferencesSave_int(this,
                fotaModelInfo.getModelName(), fotaModelInfo.getModelCurrentVersion());

        String modelName = fotaModelInfo.getModelName();
        if (TextUtils.isEmpty(modelName)) {
            modelName = fotaModelInfo.getUploadInfoList().get(0).getModelName();
        }
        appendText(modelName + "下载完成，等待安装");
        fotaModelWaiInstallList.add(fotaModelInfo);
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

    private void setMobileDownloadStateBtnText() {
        String mobileDownload = "移动网络下载" + (isMobileDownload ? "已开启" : "已关闭");
        mobileDownloadButton.setText(mobileDownload);
    }

    private void setAutoUpdateTextBtnText() {
        String auto = "文本自动刷新" + (isAutoUpdateText ? "已开启" : "已关闭");
        autoUpdateTextButton.setText(auto);
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
                textView.append((++lineNum) + " " + text + "\n");
            }
        });
        if (isAutoUpdateText) {
            scrollToBottom();
        }
    }

    public void scrollToBottom() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, textView.getBottom());
            }
        });
    }

    public static boolean isMain() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
}
