package com.zpguet.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.zpguet.database.entry.WordRecordEntry;
import com.zpguet.magiclndicatortest.R;
import com.zpguet.viewmodel.WordRecordViewModel;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by Android Studio.
 * User: ZP
 * Date: 2019/7/1
 * Time: 17:08
 */
public class MyWordRecord extends Fragment {

    private View view;
    private WordRecordViewModel viewModel;
//    private TextView labelView;
//    private Button queryAllButton;
//    private TextView displayView;
//    private DBAdapter dbAdepter ;
//    private EditText idEntry;
//    private Button deleteButton;
//    private Button deleteAllButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_my_word_record, null);

//        queryAllButton = (Button)view.findViewById(R.id.query_all);
//        labelView = (TextView)view.findViewById(R.id.label);
//        displayView = (TextView)view.findViewById(R.id.display);
//        idEntry = (EditText)view.findViewById(R.id.id_entry);
//        deleteButton = (Button)view.findViewById(R.id.delete);
//        deleteAllButton = (Button)view.findViewById(R.id.delete_all);

//        dbAdepter = new DBAdapter(getContext());
//        dbAdepter.open();
//
//        queryAllButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Content[] contents = dbAdepter.queryAllData();
//                if (contents == null){
//                    labelView.setText("语录库中没有数据");
//                    return;
//                }
//                labelView.setText("语录库：");
//                String msg = "";
//                for (int i = 0 ; i<contents.length; i++){
//                    msg += contents[i].toString()+"\n";
//                }
//                idEntry.setText("");
//                displayView.setText(msg);
//            }
//        });
//
//        deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int id = Integer.parseInt(idEntry.getText().toString());
//                id = Integer.parseInt(idEntry.getText().toString());
//                long result = dbAdepter.deleteOneData(id);
//                String msg = "删除ID为"+idEntry.getText().toString()+"的数据" + (result>0?"成功":"失败");
//                idEntry.setText("");
//                displayView.setText(msg);
//            }
//        });
//
//        deleteAllButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dbAdepter.deleteAllData();
//                String msg = "语录全部删除";
//                idEntry.setText("");
//                displayView.setText(msg);
//            }
//        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView list = view.findViewById(R.id.listWordRecorder);
        EditText queryWord = view.findViewById(R.id.queryWord);
        TextInputLayout inputLayout = view.findViewById(R.id.layoutQueryWord);

        list.setItemAnimator(new DefaultItemAnimator());
        list.setLayoutManager(new LinearLayoutManager(view.getContext()));
        viewModel = new ViewModelProvider(this).get(WordRecordViewModel.class);
        viewModel.queryAll().observe(getViewLifecycleOwner(), wordRecordEntries -> {
            list.setAdapter(new WordRecordAdapter(wordRecordEntries));
        });
        inputLayout.addOnEditTextAttachedListener(textInputLayout -> {
            if (queryWord.getEditableText().toString().isEmpty()) {

            }
        });
        queryWord.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.queryByContent(queryWord.getEditableText().toString()).observe(getViewLifecycleOwner(), wordRecordEntries -> {
                    list.setAdapter(new WordRecordAdapter(wordRecordEntries));
                });
                return true;
            }else {
                return false;
            }
        });
        View appbar = view.findViewById(R.id.appbarWordRecord);
        appbar.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        list.setPadding(list.getPaddingLeft(),list.getPaddingTop(),list.getPaddingRight(),list.getPaddingBottom() + appbar.getMeasuredHeight());
    }
    class WordRecordAdapter extends RecyclerView.Adapter<WordRecordItemViewHolder> {
        private List<WordRecordEntry> wordRecordEntries;
        WordRecordAdapter(List<WordRecordEntry> wordRecordEntries) {
            this.wordRecordEntries = wordRecordEntries;
        }

        @Override
        public void onBindViewHolder(@NonNull WordRecordItemViewHolder holder, int position) {
            WordRecordEntry item = wordRecordEntries.get(position);
            holder.textContent.setText(item.content);
            holder.textContentTime.setText(item.contentTime);
            holder.itemView.setOnLongClickListener(v -> {
                SpannableString string = new SpannableString("删除");
                string.setSpan(new ForegroundColorSpan(Color.parseColor("#B71C1C")),0,string.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                new MaterialAlertDialogBuilder(v.getContext())
                        .setTitle("删除语录")
                        .setMessage("确认删除语录：\"" + item.content +"\"吗？")
                        .setPositiveButton(string, (dialog, which) -> {
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    viewModel.deleteItem(item);
                                }
                            }.start();

                        })
                        .setNegativeButton("取消", ((dialog, which) -> {
                            dialog.dismiss();
                        }))
                        .show();
                return true;
            });
        }

        @NonNull
        @Override
        public WordRecordItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new WordRecordItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_word_record_item,parent,false));
        }

        @Override
        public int getItemCount() {
            return wordRecordEntries.size();
        }
    }
    class WordRecordItemViewHolder extends RecyclerView.ViewHolder {
        private TextView textContent;
        private TextView textContentTime;
        WordRecordItemViewHolder(View view) {
            super(view);
            textContent = view.findViewById(R.id.textContent);
            textContentTime = view.findViewById(R.id.textContentTime);
        }

        public TextView getTextContent() {
            return textContent;
        }

        public TextView getTextContentTime() {
            return textContentTime;
        }
    }
}
