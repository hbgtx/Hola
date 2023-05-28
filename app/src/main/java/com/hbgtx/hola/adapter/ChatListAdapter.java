package com.hbgtx.hola.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hbgtx.hola.R;
import com.hbgtx.hola.callbacks.ChatItemCallback;
import com.hbgtx.hola.model.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final ChatItemCallback chatItemCallback;
    private final List<Message> messageList;

    public ChatListAdapter(Context context, ChatItemCallback chatItemCallback) {
        this.messageList = new ArrayList<>();
        this.context = context;
        this.chatItemCallback = chatItemCallback;
    }

    public ChatListAdapter(Context context, List<Message> messages, ChatItemCallback chatItemCallback) {
        this.messageList = messages;
        this.context = context;
        this.chatItemCallback = chatItemCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        ViewHolder viewHolder = ((ViewHolder) holder);
        viewHolder.getChatTitleView().setText(message.getSenderId().getId());
        viewHolder.getChatTextView().setText(message.getMessageContent().getContent());
        viewHolder.getChatItemView().setOnClickListener(v -> chatItemCallback.onChatClick(message));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void addMessage(Message message) {
        messageList.add(message);
        notifyItemInserted(messageList.size()-1);
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private final View chatItemView;
        private final TextView chatTitleView;
        private final TextView chatTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chatItemView = (View) itemView.findViewById(R.id.view_chat_item);
            chatTitleView = (TextView) itemView.findViewById(R.id.tv_chat_item_title);
            chatTextView = (TextView) itemView.findViewById(R.id.tv_chat_item_text);
        }

        public TextView getChatTitleView() {
            return chatTitleView;
        }

        public TextView getChatTextView() {
            return chatTextView;
        }

        public View getChatItemView() {
            return chatItemView;
        }
    }
}
