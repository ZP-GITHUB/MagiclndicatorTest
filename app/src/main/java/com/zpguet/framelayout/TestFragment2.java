package com.zpguet.framelayout;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.zpguet.magiclndicatortest.ChatListAdapter;
import com.zpguet.magiclndicatortest.R;
import com.zpguet.util.inpututils;

import java.util.ArrayList;

/**
 *
 */

public class TestFragment2 extends Fragment {

    private RecyclerView out_communion;
    private EditText input_words;
    private RelativeLayout communion_layout;
//    private ScrollView scrollView_oper;
    private String final_result;
    private Button communion_button;
    private View view;
    private int input_switch = 0;
    private ArrayList<Pair<String,Integer>> list = new ArrayList<Pair<String,Integer>>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_test_2, null);

        out_communion = view.findViewById(R.id.out_communion);
        input_words = view.findViewById(R.id.input_words);
        communion_layout = view.findViewById(R.id.communion_layout);
//        scrollView_oper = view.findViewById(R.id.scrollView_oper);
        communion_button = view.findViewById(R.id.communion_button);

        out_communion.setAdapter(new ChatListAdapter(list));
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setStackFromEnd(true);

        out_communion.setLayoutManager(manager);
        out_communion.setItemAnimator(new DefaultItemAnimator());
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

                            String result = inpututils.getString(myquestion);
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

    public void initData(){

//        out_communion.append("小菲菲:哈喽，咱们来聊天吧！");
        list.add(Pair.create("小菲菲:哈喽，咱们来聊天吧！",0));
        out_communion.getAdapter().notifyItemInserted(list.size() - 1);
    }
}
