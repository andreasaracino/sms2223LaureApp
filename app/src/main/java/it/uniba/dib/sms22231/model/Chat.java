package it.uniba.dib.sms22231.model;

import java.util.HashMap;
import java.util.Map;

public class Chat {
    public String id;
    public String applicationId;
    public String studentId;
    public String teacherId;
    public Integer unreadMessages;


    public Chat() {
    }

    public Chat(String id, String applicationId, String studentId, String teacherId) {
        this.id = id;
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.teacherId = teacherId;
    }

    public Chat(Map<String, Object> data) {
        id = (String) data.get("id");
        applicationId = (String) data.get("applicationId");
        studentId = (String) data.get("studentId");
        teacherId = (String) data.get("teacherId");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("applicationId", applicationId);
        map.put("studentId", studentId);
        map.put("teacherId", teacherId);
        return map;
    }
}
