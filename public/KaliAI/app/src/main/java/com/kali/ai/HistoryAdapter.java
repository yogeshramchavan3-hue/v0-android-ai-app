package com.kali.ai;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final List<ChatMessage> messages;
    private final Context context;
    private final HistoryListener listener;
    private final SimpleDateFormat dateFormat;

    public interface HistoryListener {
        void onEdit(int position, ChatMessage message);
        void onDelete(int position, ChatMessage message);
    }

    public HistoryAdapter(List<ChatMessage> messages, Context context, HistoryListener listener) {
        this.messages = messages;
        this.context = context;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        
        holder.senderText.setText(message.isUser() ? "You" : "Kali");
        holder.messageText.setText(message.getMessage());
        holder.timeText.setText(dateFormat.format(new Date(message.getTimestamp())));

        holder.editButton.setOnClickListener(v -> listener.onEdit(position, message));
        holder.deleteButton.setOnClickListener(v -> listener.onDelete(position, message));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView senderText;
        TextView messageText;
        TextView timeText;
        ImageButton editButton;
        ImageButton deleteButton;

        HistoryViewHolder(View itemView) {
            super(itemView);
            senderText = itemView.findViewById(R.id.senderText);
            messageText = itemView.findViewById(R.id.messageText);
            timeText = itemView.findViewById(R.id.timeText);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
