package it.uniba.dib.sms22231.model;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.uniba.dib.sms22231.utility.TimeUtils;

public class Message implements Serializable {
    public String id;
    public String chatId;
    public String text;
    public String senderUID;
    public String thesisId;
    public String taskId;
    public Date dateSent;
    public boolean read;
    public boolean sent;
    public MessageReference messageReference;

    public Message() {
    }



    public Message(String id, String chatId, String text, String senderUID, String taskId, Date dateSent, Boolean read, Boolean sent, MessageReference messageReference) {
        this.id = id;
        this.chatId = chatId;
        this.text = text;
        this.senderUID = senderUID;
        this.taskId = taskId;
        this.dateSent = dateSent;
        this.read = read;
        this.sent = sent;
        this.messageReference = messageReference;
    }

    public Message(Map<String, Object> data) {
        id = (String) data.get("id");
        chatId = (String) data.get("chatId");
        text = (String) data.get("text");
        senderUID = (String) data.get("senderUID");
        thesisId = (String) data.get("thesisId");
        taskId = (String) data.get("taskId");
        read = (Boolean) data.get("read");
        dateSent = TimeUtils.stringToDate((String) data.get("dateSent"), true);
        if (data.containsKey("messageReference")) {
            messageReference = new MessageReference((Map<String, Object>) data.get("messageReference"));
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("chatId", chatId);
        map.put("text", text);
        map.put("senderUID", senderUID);
        map.put("thesisId", thesisId);
        map.put("taskId", taskId);
        map.put("read", read);
        map.put("dateSent", TimeUtils.dateToString(dateSent, true));
        if (messageReference != null) {
            map.put("messageReference", messageReference.toMap());
        }
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id.equals(message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
