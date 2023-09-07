package it.uniba.dib.sms22231.model;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Message {
    public String id;
    public String chatId;
    public String attachmentId;
    public String text;
    public String senderUID;
    public String taskId;
    public Date dateSent;
    public boolean read;

    public Message() {
    }

    public Message(String id, String chatId, String attachmentId, String text, String senderUID, String taskId, Date dateSent, Boolean read) {
        this.id = id;
        this.chatId = chatId;
        this.attachmentId = attachmentId;
        this.text = text;
        this.senderUID = senderUID;
        this.taskId = taskId;
        this.dateSent = dateSent;
        this.read = read;
    }

    public Message(Map<String, Object> data) {
        id = (String) data.get("id");
        chatId = (String) data.get("chatId");
        attachmentId = (String) data.get("attachmentId");
        text = (String) data.get("text");
        senderUID = (String) data.get("senderUID");
        taskId = (String) data.get("taskId");
        read = Boolean.getBoolean((String) Objects.requireNonNull(data.get("read")));

        try {
            @SuppressLint("SimpleDateFormat") DateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd - HH:mm");
            dateSent = simpleDateFormat.parse((String) Objects.requireNonNull(data.get("dateSent")));
        } catch (ParseException e) {
            //
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("chatId", chatId);
        map.put("attachmentId", attachmentId);
        map.put("text", text);
        map.put("senderUID", senderUID);
        map.put("taskId", taskId);
        map.put("read", read);
        @SuppressLint("SimpleDateFormat") DateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd - HH:mm");
        map.put("dateSent", simpleDateFormat.format(dateSent));
        return map;
    }
}
