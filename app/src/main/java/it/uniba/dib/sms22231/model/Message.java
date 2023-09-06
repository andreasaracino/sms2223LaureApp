package it.uniba.dib.sms22231.model;

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
    public boolean read;

    public Message() {
    }

    public Message(String id, String chatId, String attachmentId, String text, String senderUID, String taskId, Boolean read) {
        this.id = id;
        this.chatId = chatId;
        this.attachmentId = attachmentId;
        this.text = text;
        this.senderUID = senderUID;
        this.taskId = taskId;
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
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("chatId", chatId);
        map.put("attachmentId", attachmentId);
        map.put("text", text);
        map.put("senderUID", senderUID);
        map.put("taskId", taskId);
        map.put("read", read);
        return map;
    }
}
