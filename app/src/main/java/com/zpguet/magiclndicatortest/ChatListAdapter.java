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
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
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
        Pair<String, Integer> item = list.get(i);
        viewHolder.content.setText(item.first);
        int width = viewHolder.content.getResources().getDisplayMetrics().widthPixels;
        viewHolder.content.setMaxWidth(width * 5 / 8);
        viewHolder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(item,v);
            }
        });
        viewHolder.itemView.setOnLongClickListener(v -> {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(item,v);
                return true;
            }else {
                return false;
            }
        });
    }
    public void setOnItemClickListener(OnItemClickListener onClickListener) {
        this.onItemClickListener = onClickListener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }
    public interface OnItemClickListener {
        public void onItemClick(Pair<String,Integer> item, View view);
    }

    public interface OnItemLongClickListener {
        public void onItemLongClick(Pair<String, Integer> item, View view);
    }
}
class ViewHolder extends  RecyclerView.ViewHolder {
    TextView content;
    public ViewHolder(View view) {
        super(view);
        content = (TextView) view.findViewById(R.id.chat_content) ;
    }
}
