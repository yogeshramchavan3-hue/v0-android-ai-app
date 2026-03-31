package com.kali.ai;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ChatHistoryActivity extends AppCompatActivity {

    private RecyclerView historyRecyclerView;
    private HistoryAdapter historyAdapter;
    private DatabaseHelper dbHelper;
    private List<ChatMessage> allMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chat History");
        }

        dbHelper = new DatabaseHelper(this);
        initViews();
        loadHistory();
    }

    private void initViews() {
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton clearAllButton = findViewById(R.id.clearAllButton);
        clearAllButton.setOnClickListener(v -> showClearAllDialog());
    }

    private void loadHistory() {
        allMessages = dbHelper.getAllMessages();
        historyAdapter = new HistoryAdapter(allMessages, this, new HistoryAdapter.HistoryListener() {
            @Override
            public void onEdit(int position, ChatMessage message) {
                showEditDialog(position, message);
            }

            @Override
            public void onDelete(int position, ChatMessage message) {
                showDeleteDialog(position, message);
            }
        });
        historyRecyclerView.setAdapter(historyAdapter);
    }

    private void showEditDialog(int position, ChatMessage message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Message");

        EditText input = new EditText(this);
        input.setText(message.getMessage());
        input.setPadding(48, 32, 48, 32);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newMessage = input.getText().toString().trim();
            if (!newMessage.isEmpty()) {
                dbHelper.updateMessage(message.getId(), newMessage);
                message.setMessage(newMessage);
                historyAdapter.notifyItemChanged(position);
                Toast.makeText(this, "Message updated", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showDeleteDialog(int position, ChatMessage message) {
        new AlertDialog.Builder(this)
            .setTitle("Delete Message")
            .setMessage("Are you sure you want to delete this message?")
            .setPositiveButton("Delete", (dialog, which) -> {
                dbHelper.deleteMessage(message.getId());
                allMessages.remove(position);
                historyAdapter.notifyItemRemoved(position);
                Toast.makeText(this, "Message deleted", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showClearAllDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Clear All History")
            .setMessage("Are you sure you want to delete all chat history? This cannot be undone.")
            .setPositiveButton("Clear All", (dialog, which) -> {
                dbHelper.clearAllMessages();
                allMessages.clear();
                historyAdapter.notifyDataSetChanged();
                Toast.makeText(this, "All history cleared", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
