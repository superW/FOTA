package com.autoai.android.fotaframework;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class RollBackActivity extends Activity {

    private TextView textView;
    private ScrollView scrollView;
    private EditText modelNameEditText;
    private Button rollbackButton;

    private int lineNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roll_back);

        textView = (TextView) findViewById(R.id.textView);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());

        modelNameEditText = (EditText) findViewById(R.id.modelNameEditText);

        rollbackButton = (Button) findViewById(R.id.rollbackButton);
        rollbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String modelName = modelNameEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(modelName)) {
                    rollbackModel(modelName);
                } else {
                    showToast("啥也不填~逗我玩呢！");
                }
            }
        });
    }

    /**
     * model回滚
     */
    private void rollbackModel(String modelName) {
        Intent serviceIntent = getServicentent();
        serviceIntent.putExtra(FotaService.METHOD_EXTRA, FotaService.ROLLBACK_METHOD);
        serviceIntent.putExtra(FotaService.ROLLBACK_MODEL_EXTRA, modelName);
        startService(serviceIntent);
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

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
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
        scrollToBottom();
    }

    public void scrollToBottom() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, textView.getBottom());
            }
        });
    }

}
