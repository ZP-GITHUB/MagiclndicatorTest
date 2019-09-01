package com.zpguet.framelayout;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zpguet.magiclndicatortest.R;
import com.zpguet.magiclndicatortest.Speech_initActivity;

import androidx.fragment.app.Fragment;

/**
 * Created by janiszhang on 2016/6/10.
 */

public class TestFragment3 extends Fragment {
    private TextView view_content;
    private ImageButton go_speech;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_test_3, null);

        go_speech = view.findViewById(R.id.go_speak);

        go_speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), Speech_initActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

}
