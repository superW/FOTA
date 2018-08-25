package com.autoai.android.callback;

import android.os.Bundle;

/**
 * Created by wangyanchao on 2018/8/24.
 */

public interface InnerListener {

    String INIT_RESULT_ACTION = "onInitResult";
    String RESULT_EXTRA = "result_extra";
    String REASON_EXTRA = "reason_extra";

    String UPGRADE_LIST_ACTION = "onUpload";
    String UPGRADE_LIST_EXTRA = "upgrade_extra";

    String DOWNLOADING_ACTION = "onDownloading";
    String DOWNLOADING_MODEL_EXTRA = "downloading_model_extra";
    String DOWNLOADING_PROG_EXTRA = "downloading_prog_extra";

    void onReceive(String action, Bundle bundle);

}
