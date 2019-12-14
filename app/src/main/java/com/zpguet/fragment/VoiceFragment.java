package com.zpguet.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zpguet.magiclndicatortest.R;

import org.json.JSONObject;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     VoiceFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 * <p>You activity (or fragment) needs to implement {@link VoiceFragment.Listener}.</p>
 */
public class VoiceFragment extends BottomSheetDialogFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_COUNT = "item_count";
    private Listener mListener;
    private EventManager asr;
    private String content = "";
    private Thread thread;
    private TextView userSayingText;
    private EventListener asrListener = (s, s1, bytes, i, i1) -> {
        try {
            if (s.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
                JSONObject jsonObject = new JSONObject(s1);
                content = jsonObject.getString("best_result");
                userSayingText.setText(content);
            }else if (s.equals(SpeechConstant.CALLBACK_EVENT_ASR_EXIT)) {
                thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Thread.sleep(1000);
                            new Handler(Looper.getMainLooper()).post(() -> {
                                if (mListener != null) {
                                    mListener.onSayFinish(content);
                                }
                                dismiss();
                            });

                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    };

    // TODO: Customize parameters
    public static VoiceFragment newInstance(int itemCount) {
        final VoiceFragment fragment = new VoiceFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_ITEM_COUNT, itemCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_voice, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        userSayingText = view.findViewById(R.id.userSayingText);
//        final RecyclerView recyclerView = (RecyclerView) view;
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(new ItemAdapter(getArguments().getInt(ARG_ITEM_COUNT)));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            asr = EventManagerFactory.create(context, "asr");
            asr.registerListener(asrListener);
            JSONObject json = new JSONObject();
            json.put(SpeechConstant.PID, 1536);
            asr.send(SpeechConstant.ASR_START, json.toString(),null,0,0);
            final Fragment parent = getParentFragment();
            if (parent != null) {
                mListener = (Listener) parent;
            } else {
                mListener = (Listener) context;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        asr.unregisterListener(asrListener);
        if (thread != null) {
            thread.interrupt();
        }
        super.onDetach();
    }

    public interface Listener {
//        void onItemClicked(int position);
        void onSayCancel(int position);
        void onSayFinish(String content);
    }

}
