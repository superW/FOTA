package com.autoai.android.aidl;

import com.autoai.android.aidl.FotaAidlModelInfo;

interface FotaAidlListener {

    // 收到升级任务信息
    void onUpgrade(in List<FotaAidlModelInfo> modelInfos);

    // 下载进度
    void onDownloading(in FotaAidlModelInfo fotaModelInfo, float progress);

    // 提示下载完成，用户选择是否安装
    void onFileDownloadSucceed(in FotaAidlModelInfo modelInfo);

    // 升级进度
    void onInstalling(in FotaAidlModelInfo modelInfo, float progress);

    // 升级结果
    void onFinishInstall(in FotaAidlModelInfo modelInfo);
}
