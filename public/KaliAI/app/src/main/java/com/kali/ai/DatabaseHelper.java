package com.kali.ai;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "kali_ai.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_MESSAGES = "messages";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_IS_USER = "is_user";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String TABLE_CONTACTS = "contacts";
    private static final String COLUMN_CONTACT_NAME = "name";
    private static final String COLUMN_CONTACT_NUMBER = "number";
    private static final String COLUMN_AUTO_REPLY_ENABLED = "auto_reply_enabled";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createMessagesTable = "CREATE TABLE " + TABLE_MESSAGES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MESSAGE + " TEXT NOT NULL, " +
                COLUMN_IS_USER + " INTEGER NOT NULL, " +
                COLUMN_TIMESTAMP + " INTEGER NOT NULL)";
        db.execSQL(createMessagesTable);

        String createContactsTable = "CREATE TABLE " + TABLE_CONTACTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CONTACT_NAME + " TEXT, " +
                COLUMN_CONTACT_NUMBER + " TEXT UNIQUE, " +
                COLUMN_AUTO_REPLY_ENABLED + " INTEGER DEFAULT 1)";
        db.execSQL(createContactsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    public long saveMessage(ChatMessage message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MESSAGE, message.getMessage());
        values.put(COLUMN_IS_USER, message.isUser() ? 1 : 0);
        values.put(COLUMN_TIMESTAMP, message.getTimestamp());
        
        long id = db.insert(TABLE_MESSAGES, null, values);
        db.close();
        return id;
    }

    public List<ChatMessage> getRecentMessages(int limit) {
        List<ChatMessage> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + TABLE_MESSAGES + 
                       " ORDER BY " + COLUMN_TIMESTAMP + " DESC LIMIT " + limit;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String message = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE));
                boolean isUser = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_USER)) == 1;
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));
                
                messages.add(new ChatMessage(message, isUser, timestamp));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        
        Collections.reverse(messages);
        return messages;
    }

    public List<ChatMessage> getAllMessages() {
        List<ChatMessage> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + TABLE_MESSAGES + " ORDER BY " + COLUMN_TIMESTAMP + " ASC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String message = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE));
                boolean isUser = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_USER)) == 1;
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));
                
                ChatMessage chatMessage = new ChatMessage(message, isUser, timestamp);
                chatMessage.setId(id);
                messages.add(chatMessage);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        
        return messages;
    }

    public void deleteMessage(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MESSAGES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateMessage(long id, String newMessage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MESSAGE, newMessage);
        db.update(TABLE_MESSAGES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void clearAllMessages() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MESSAGES, null, null);
        db.close();
    }

    public void addAutoReplyContact(String name, String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTACT_NAME, name);
        values.put(COLUMN_CONTACT_NUMBER, number);
        values.put(COLUMN_AUTO_REPLY_ENABLED, 1);
        db.insertWithOnConflict(TABLE_CONTACTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void removeAutoReplyContact(String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, COLUMN_CONTACT_NUMBER + " = ?", new String[]{number});
        db.close();
    }

    public boolean isAutoReplyEnabled(String number) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTS,
                new String[]{COLUMN_AUTO_REPLY_ENABLED},
                COLUMN_CONTACT_NUMBER + " = ?",
                new String[]{number},
                null, null, null);
        
        boolean enabled = false;
        if (cursor.moveToFirst()) {
            enabled = cursor.getInt(0) == 1;
        }
        cursor.close();
        db.close();
        return enabled;
    }

    public List<String[]> getAutoReplyContacts() {
        List<String[]> contacts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_CONTACTS,
                new String[]{COLUMN_CONTACT_NAME, COLUMN_CONTACT_NUMBER},
                COLUMN_AUTO_REPLY_ENABLED + " = 1",
                null, null, null, COLUMN_CONTACT_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                String number = cursor.getString(1);
                contacts.add(new String[]{name, number});
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contacts;
    }
}
