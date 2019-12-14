package com.zpguet.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.aip.asrwakeup3.core.recog.MyRecognizer;
import com.baidu.aip.asrwakeup3.core.recog.listener.ChainRecogListener;
import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DigitalDialogInput;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.Utils;
import com.zpguet.magiclndicatortest.ChatListAdapter;
import com.zpguet.magiclndicatortest.R;
import com.zpguet.magiclndicatortest.Speech_initActivity;
import com.zpguet.util.InputUtil;

import androidx.annotation.UiThread;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by janiszhang on 2016/6/10.
 */

public class TestFragment3 extends Fragment {
    private View view;
    private TextView view_content;

    //    private TextView sp_out_communion;
    private RecyclerView sp_out_communion;
    private ArrayList<Pair<String,Integer>> list = new ArrayList<Pair<String,Integer>>();

    private ImageButton speech_bottom;
    private Button send_to_communion;
    private Button savecontent;
    private MyRecognizer myRecognizer;
    private Speech_initActivity.AsyncCallback<String> speechCallback;
    //    private ScrollView sp_scrollView_oper;
    private String final_result;
    static int times = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_test_3, null);
//
//        go_speech = view.findViewById(R.id.go_speak);
//
//        go_speech.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(getActivity(), Speech_initActivity.class);
//                startActivity(intent);
//            }
//        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utils.init(view.getContext());


        ChainRecogListener chainRecogListener = new ChainRecogListener();
        myRecognizer = new MyRecognizer(view.getContext(), chainRecogListener);
        BaiduASRDigitalDialog.setInput(new DigitalDialogInput(myRecognizer,
                chainRecogListener,
                new HashMap<String, Object>()));
        ArrayList<String> ApplyList = new ArrayList<>();

        String requests[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        for (String request : requests)
        {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat
                    .checkSelfPermission(view.getContext(), request))
            {
                //没有就加
                ApplyList.add(request);
            }
        }

        //在这里请求所缺权限
        if (!ApplyList.isEmpty())
        {
            String[] toApplys = new String[ApplyList.size()];
            ApplyList.toArray(toApplys);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(toApplys,1000);
            }
        }
        view_content = view.findViewById(R.id.speech_content);//TextView
        speech_bottom = view.findViewById(R.id.speech_button);//Bottom
        savecontent = view.findViewById(R.id.savecontent);

        sp_out_communion = view.findViewById(R.id.sp_out_communion);

        sp_out_communion.setAdapter(new ChatListAdapter(list));
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext());
        manager.setStackFromEnd(true);

        sp_out_communion.setLayoutManager(manager);
        sp_out_communion.setItemAnimator(new DefaultItemAnimator());

        send_to_communion = view.findViewById(R.id.send_to_communion);

//        sp_scrollView_oper = findViewById(R.id.sp_scrollView_oper);

        speech_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //触发语音识别并显示在textView上
                out_speech_word(
                        new Speech_initActivity.AsyncCallback<String>(){
                            @Override
                            public void onResult(String s)
                            {
                                view_content.setText(s);
                                if(s.contains("启动")){

                                    if(s.contains("相机")){
                                        Intent intent = new Intent();
//                                        Intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                                        startActivity(intent);
                                    }else if(s.contains("图库")){
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_PICK);
                                        startActivity(intent);
                                    }else if(s.contains("电话")){
                                        String telNumber = "10086";
                                        Intent intent = new Intent();
                                        //设置动作
                                        intent.setAction(Intent.ACTION_CALL);
                                        //设置电话号码
                                        //Uri统一资源标识符
                                        intent.setData(Uri.parse("tel:"+telNumber));
                                        //启动打电话应用
                                        startActivity(intent);
                                    }else if(s.contains("短信")){
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_SENDTO);
                                        intent.setData(Uri.parse("smsto:10086"));
                                        intent.putExtra("sms_body","成功启动短信服务");
                                        startActivity(intent);

                                    }
                                }
                            }
                        }
                );
            }
        });

        savecontent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String word = view_content.getText().toString().trim();

                if(word == null ||  "".equals(word))
                {
                    Toast.makeText(view.getContext().getApplicationContext(), "内容为空，无法保存。",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
//                    DBAdapter dbAdapter = new DBAdapter(view.getContext());
//                    dbAdapter.open();
//                    long colunm = dbAdapter.insert(word);
//                    if (colunm == -1 ){
//                        view_content.setText("");
//                        Toast.makeText(view.getContext().getApplicationContext(), "保存失败",
//                                Toast.LENGTH_SHORT).show();
//                    } else {
//                        view_content.setText("");
//                        Toast.makeText(view.getContext().getApplicationContext(), "保存成功",
//                                Toast.LENGTH_SHORT).show();
//                    }
                }
            }
        });

        send_to_communion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String word = view_content.getText().toString().trim();

                if(word == null ||  "".equals(word))
                {
                    Toast.makeText(view.getContext().getApplicationContext(), "内容为空，无法发送。",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(times == 1)
                    {
                        list.add(Pair.create("小菲菲:哈喽，咱们来聊天吧！",0));
                        if (sp_out_communion.getAdapter() != null) {
                            sp_out_communion.getAdapter().notifyItemInserted(list.size() - 1);
                        }
                        times = 0;
                    }

                    final String myquestion;

                    myquestion = word;

                    if (myquestion == null || "".equals(myquestion)) {
                        Toast.makeText(view.getContext().getApplicationContext(), "请输入聊天信息",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else
                    {
                        send_to_communion.setEnabled(false);
                    }

                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm.isActive()&& getActivity().getCurrentFocus()!=null){
                        if (getActivity().getCurrentFocus().getWindowToken()!=null) {
                            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }

//                    sp_out_communion.append("\nMe:"+myquestion);
                    list.add(Pair.create( myquestion, 1));
                    sp_out_communion.getAdapter().notifyItemInserted(list.size() - 1);
                    sp_out_communion.scrollToPosition(list.size() -1);

                    final Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);

                            final_result=(String)msg.obj;
//                            sp_out_communion.append("\n小菲菲:"+final_result);
                            list.add(Pair.create( final_result, 0));
                            sp_out_communion.getAdapter().notifyItemInserted(list.size() - 1);
                            sp_out_communion.scrollToPosition(list.size() -1);
                            send_to_communion.setEnabled(true);
                            view_content.setText("");
                        }
                    };

                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub

                            String result = InputUtil.getString(myquestion);
                            Looper.prepare();
                            Message message=new Message();
                            message.what=1;
                            message.obj=result;
                            handler.sendMessage(message);
                            Looper.loop();

                        }
                    }).start();

//                    //sp_scrollView_oper自动滑动到底部
//                    sp_scrollView_oper.post(new Runnable() {
//                        public void run() {
//                            sp_scrollView_oper.fullScroll(ScrollView.FOCUS_DOWN);
//                        }
//                    });
                }
            }
        });

    }

    private void out_speech_word(Speech_initActivity.AsyncCallback<String> callback)
    {
        speechCallback = callback;
        if (NetworkUtils.isConnected())
        {
            Intent intent = new Intent(getContext(), BaiduASRDigitalDialog.class);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 100)
        {
            if (speechCallback != null)
            {
                if (resultCode == Activity.RESULT_OK)
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        BaiduASRDigitalDialog.setInput(null);
        myRecognizer.release();
    }

    public interface AsyncCallback<Result>
    {
        @UiThread
        void onResult(Result result);
    }
}
