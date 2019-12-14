package com.zpguet.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.google.android.material.bottomappbar.BottomAppBar;
import com.zpguet.database.entry.WordRecordEntry;
import com.zpguet.magiclndicatortest.ChatListAdapter;
import com.zpguet.magiclndicatortest.R;
import com.zpguet.util.InputUtil;
import com.zpguet.viewmodel.WordRecordViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 */

public class SmartChatFragment extends Fragment implements VoiceFragment.Listener {

    private RecyclerView out_communion;
    private EditText input_words;
    private RelativeLayout communion_layout;
//    private ScrollView scrollView_oper;
    private String final_result;
    private Button communion_button;
    private View view;
    private int input_switch = 0;
    private ArrayList<Pair<String,Integer>> list = new ArrayList<Pair<String,Integer>>();
    private Executor executor = new ThreadPoolExecutor(5,10,100, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(5));
    private VoiceFragment voiceFragment = new VoiceFragment();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_smart_chat, null);
        WordRecordViewModel viewModel = new ViewModelProvider(this).get(WordRecordViewModel.class);
        out_communion = view.findViewById(R.id.out_communion);
        input_words = view.findViewById(R.id.input_words);
//        communion_layout = view.findViewById(R.id.communion_layout);
//        scrollView_oper = view.findViewById(R.id.scrollView_oper);
        communion_button = view.findViewById(R.id.communion_button);
        ChatListAdapter chatListAdapter = new ChatListAdapter(list);
        out_communion.setAdapter(chatListAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setStackFromEnd(true);

        out_communion.setLayoutManager(manager);
        out_communion.setItemAnimator(new DefaultItemAnimator());
        chatListAdapter.setOnItemClickListener((item, v) -> {

        });
        chatListAdapter.setOnItemLongClickListener((item, v) -> {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String dateStr = df.format(new Date());
            WordRecordEntry itemEntry = new WordRecordEntry();
            itemEntry.content = item.first;
            itemEntry.contentTime = dateStr;
            executor.execute(() -> {
                viewModel.insertItems(itemEntry);
                new Handler(Looper.getMainLooper()).post(() -> {
                   Toast.makeText(getContext(),itemEntry.content + "已保存到我的语录",Toast.LENGTH_SHORT).show();
                });
            });
        });
        initData();

        input_words.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input_switch == 1)
                {
                    Toast.makeText(getActivity().getApplicationContext(), "请等一下小菲菲回话哦！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        input_words.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    communion_button.setEnabled(true);
                }else {
                    communion_button.setEnabled(false);
                }
            }
        });
        communion_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String myquestion;

                    myquestion = input_words.getText().toString().trim();

                    if (myquestion == null || "".equals(myquestion)) {
                        Toast.makeText(getActivity().getApplicationContext(), "请输入聊天信息",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else
                    {
                        input_words.setFocusableInTouchMode(false);
                        input_switch = 1;
                    }

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm.isActive()&& getActivity().getCurrentFocus()!=null){
                        if (getActivity().getCurrentFocus().getWindowToken()!=null) {
                            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                    list.add(Pair.create( myquestion, 1));
                    out_communion.getAdapter().notifyItemInserted(list.size() - 1);
                    out_communion.scrollToPosition(list.size() -1);
//                    out_communion.append("\nMe:"+myquestion);
                    input_words.setText("");

                    final Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);

                            final_result=(String)msg.obj;
//                            final_result.replaceAll("[{br}]","\n");
//                            out_communion.append("\n小菲菲:"+final_result);
                            list.add(Pair.create( final_result, 0));
                            out_communion.getAdapter().notifyItemInserted(list.size() - 1);
                            out_communion.scrollToPosition(list.size() -1);
                            input_words.setFocusableInTouchMode(true);
                            input_switch = 0;
                        }
                    };

                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub

                            String result = InputUtil.getString(myquestion);
                            result.replace("{br}","\n");
                            Looper.prepare();
                            Message message=new Message();
                            message.what=1;
                            message.obj=result;
                            handler.sendMessage(message);
                            Looper.loop();

                        }
                    }).start();

//                    //scrollView_oper自动滑动到底部
//                    scrollView_oper.post(new Runnable() {
//                        public void run() {
//                            scrollView_oper.fullScroll(ScrollView.FOCUS_DOWN);
//                        }
//                    });

                }
            }
        );

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomAppBar appbar = view.findViewById(R.id.appbarChat);
        appbar.setNavigationOnClickListener((v -> {
            voiceFragment.show(getChildFragmentManager(),"chatFragment");
        }));
    }

    @Override
    public void onSayCancel(int position) {

    }

    @Override
    public void onSayFinish(String content) {
        if(content.startsWith("启动")){
            String appName = content.substring("启动".length(),content.length()).replace("。","");
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> list = view.getContext().getPackageManager().queryIntentActivities(intent,0);
            HashMap<String, android.content.pm.ApplicationInfo> map = new HashMap<String, ApplicationInfo>();
            for (ResolveInfo info : list) {
                ApplicationInfo applicationInfo = info.activityInfo.applicationInfo;
                if (!map.containsKey(applicationInfo.packageName)) {
                    map.put(applicationInfo.packageName,applicationInfo);
                }
            }
            for (ApplicationInfo applicationInfo: map.values()) {
                if (applicationInfo.loadLabel(view.getContext().getPackageManager()).toString().equals(appName)) {
                    startActivity(view.getContext().getPackageManager().getLaunchIntentForPackage(applicationInfo.packageName));
                    return;
                }
            }
        }
        input_words.getEditableText().clear();
        input_words.append(content);
        communion_button.callOnClick();
    }

    public void initData(){

//        out_communion.append("小菲菲:哈喽，咱们来聊天吧！");
        list.add(Pair.create("小菲菲:哈喽，咱们来聊天吧！\ntips:长按气泡可保存至语录",0));
        out_communion.getAdapter().notifyItemInserted(list.size() - 1);
    }
}
