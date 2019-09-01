package com.zpguet.framelayout;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zpguet.magiclndicatortest.DBAdapter;
import com.zpguet.magiclndicatortest.R;
import com.zpguet.model.Content;

import androidx.fragment.app.Fragment;

/**
 * Created by Android Studio.
 * User: ZP
 * Date: 2019/7/1
 * Time: 17:08
 */
public class TestFragment4 extends Fragment {

    private View view;
    private TextView labelView;
    private Button queryAllButton;
    private TextView displayView;
    private DBAdapter dbAdepter ;
    private EditText idEntry;
    private Button deleteButton;
    private Button deleteAllButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_test_4, null);

        queryAllButton = (Button)view.findViewById(R.id.query_all);
        labelView = (TextView)view.findViewById(R.id.label);
        displayView = (TextView)view.findViewById(R.id.display);
        idEntry = (EditText)view.findViewById(R.id.id_entry);
        deleteButton = (Button)view.findViewById(R.id.delete);
        deleteAllButton = (Button)view.findViewById(R.id.delete_all);

        dbAdepter = new DBAdapter(getContext());
        dbAdepter.open();

        queryAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Content[] contents = dbAdepter.queryAllData();
                if (contents == null){
                    labelView.setText("语录库中没有数据");
                    return;
                }
                labelView.setText("语录库：");
                String msg = "";
                for (int i = 0 ; i<contents.length; i++){
                    msg += contents[i].toString()+"\n";
                }
                idEntry.setText("");
                displayView.setText(msg);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = Integer.parseInt(idEntry.getText().toString());
                id = Integer.parseInt(idEntry.getText().toString());
                long result = dbAdepter.deleteOneData(id);
                String msg = "删除ID为"+idEntry.getText().toString()+"的数据" + (result>0?"成功":"失败");
                idEntry.setText("");
                displayView.setText(msg);
            }
        });

        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbAdepter.deleteAllData();
                String msg = "语录全部删除";
                idEntry.setText("");
                displayView.setText(msg);
            }
        });

        return view;
    }



}
