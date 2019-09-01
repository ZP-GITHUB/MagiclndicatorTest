package com.zpguet.magiclndicatortest;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Android Studio.
 * User: ZP
 * Date: 2019/6/29
 * Time: 13:45
 */
public class ChatListAdapter extends RecyclerView.Adapter<ViewHolder> {
    private ArrayList<Pair<String,Integer>> list = new ArrayList<>();
    public ChatListAdapter(ArrayList<Pair<String,Integer>> list) {
        this.list = list ;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == 0)
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_chat_item_left,viewGroup,false));
        else
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_chat_item_right,viewGroup,false));
    }

    @Override
    public int getItemCount() {
        return list.size() ;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).second ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.content.setText(list.get(i).first);
    }
}
class ViewHolder extends  RecyclerView.ViewHolder {
    TextView content;
    public ViewHolder(View view) {
        super(view);
        content = (TextView) view.findViewById(R.id.chat_content) ;
    }
}
