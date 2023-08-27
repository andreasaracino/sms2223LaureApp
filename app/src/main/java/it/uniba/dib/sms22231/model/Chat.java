package it.uniba.dib.sms22231.model;

import java.util.HashMap;
import java.util.Map;

public class Chat {
    public String id;
    public String thesisId;
    public String applicationId;
    public String studentId;
    public String teacherId;

    public Chat() {
    }

    public Chat(String id, String thesisId, String applicationId, String studentId, String teacherId) {
        this.id = id;
        this.thesisId = thesisId;
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.teacherId = teacherId;
    }

    public Chat(Map<String, Object> data) {
        id = (String) data.get("id");
        thesisId = (String) data.get("thesisId");
        studentId = (String) data.get("studentId");
        teacherId = (String) data.get("teacherId");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("thesisId", thesisId);
        map.put("studentId", studentId);
        map.put("teacherId", teacherId);
        return map;
    }
}
