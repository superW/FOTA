// IFotaAidlInterface.aidl
package com.autoai.android.aidl;

import com.autoai.android.aidl.FotaAidlListener;
import com.autoai.android.aidl.FotaAidlModelInfo;

interface IFotaAidlInterface {

    // 用户选择下载
    void download(in List<FotaAidlModelInfo> modelInfos);

    // 用户选择安装
    void installModels(in List<FotaAidlModelInfo> modelInfos);

    void setFotaListener(FotaAidlListener fotaListener);

}
