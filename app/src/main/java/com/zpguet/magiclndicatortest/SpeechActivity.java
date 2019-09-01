package com.zpguet.magiclndicatortest;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.aip.asrwakeup3.core.recog.MyRecognizer;
import com.baidu.aip.asrwakeup3.core.recog.listener.ChainRecogListener;
import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DigitalDialogInput;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class SpeechActivity extends AppCompatActivity {

    private MyRecognizer myRecognizer;
    private Speech_initActivity.AsyncCallback<String> speechCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Utils.init(this);


        ChainRecogListener chainRecogListener = new ChainRecogListener();
        myRecognizer = new MyRecognizer(this, chainRecogListener);
        BaiduASRDigitalDialog.setInput(new DigitalDialogInput(myRecognizer,
                chainRecogListener,
                new HashMap<String, Object>()));
    }

    protected final void out_speech_word(Speech_initActivity.AsyncCallback<String> callback)
    {
        speechCallback = callback;
        if (NetworkUtils.isConnected())
        {
            Intent intent = new Intent(this, BaiduASRDigitalDialog.class);
            startActivityForResult(intent, 100);
        } else
        {
            if (speechCallback != null)
            {
                callback.onResult(null);
            }
        }
    }

    @Override
    public void finish()
    {
        super.finish();
        BaiduASRDigitalDialog.setInput(null);
        myRecognizer.release();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 100)
        {
            if (speechCallback != null)
            {
                if (resultCode == RESULT_OK)
                {
                    ArrayList<String> results = data.getStringArrayListExtra("results");
                    if (results != null && results.size() > 0)
                    {
                        speechCallback.onResult(results.get(0));
                    }
                } else
                {
                    speechCallback.onResult("");
                }
                //清理引用
                speechCallback = null;
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
