package it.uniba.dib.sms22231.model;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.uniba.dib.sms22231.utility.TimeUtils;

public class Chat implements Serializable {
    public String id;
    public String applicationId;
    public String studentId;
    public String teacherId;
    public Date lastUpdated;
    public Integer unreadMessages;
    public String userFullName;
    public Message lastMessage;

    public Chat(String applicationId, String studentId, String teacherId, Date lastUpdated) {
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.lastUpdated = lastUpdated;
    }

    public Chat(Map<String, Object> data) {
        id = (String) data.get("id");
        applicationId = (String) data.get("applicationId");
        studentId = (String) data.get("studentId");
        teacherId = (String) data.get("teacherId");

    }

    public void update(Chat chat) {
        id = chat.id;
        applicationId = chat.applicationId;
        studentId = chat.studentId;
        teacherId = chat.teacherId;
        unreadMessages = chat.unreadMessages;
        userFullName = chat.userFullName;
        lastMessage = chat.lastMessage;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("applicationId", applicationId);
        map.put("studentId", studentId);
        map.put("teacherId", teacherId);
        map.put("lastUpdated", TimeUtils.dateToString(lastUpdated, false));
        return map;
    }
}
