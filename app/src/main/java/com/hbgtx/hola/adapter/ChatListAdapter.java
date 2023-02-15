package com.hbgtx.hola.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hbgtx.hola.R;
import com.hbgtx.hola.model.ChatItem;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatItem> chatItemList;
    private final Context context;

    public ChatListAdapter(Context context, List<ChatItem> chatItemList) {
        this.chatItemList = chatItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatItem chatItem = chatItemList.get(position);
        ViewHolder viewHolder = ((ViewHolder) holder);
        viewHolder.getChatTitleView().setText(chatItem.getTitle());
        viewHolder.getChatItemView().setOnClickListener(view ->
                Toast.makeText(context, chatItem.getTitle(), Toast.LENGTH_SHORT).show());

    }

    @Override
    public int getItemCount() {
        return chatItemList.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private final View chatItemView;
        private final TextView chatTitleView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chatItemView = (View) itemView.findViewById(R.id.view_chat_item);
            chatTitleView = (TextView) itemView.findViewById(R.id.tv_chat_item_title);
        }

        public TextView getChatTitleView() {
            return chatTitleView;
        }

        public View getChatItemView() {
            return chatItemView;
        }
    }
}
