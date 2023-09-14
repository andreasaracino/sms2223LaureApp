package it.uniba.dib.sms22231.model;

import java.util.HashMap;
import java.util.Map;

public class Application {

    public String id;
    public String status;
    public String thesisId;
    public String studentUid;
    public Requirement[] requirement;
    public String chatId;

    public Application() {}

    public Application(String id, String status, String thesisId, String studentUid, Requirement[] requirement, String chatId) {
        this.id = id;
        this.status = status;
        this.thesisId = thesisId;
        this.studentUid = studentUid;
        this.requirement = requirement;
        this.chatId = chatId;
    }

    public Application(Map<String, Object> data) {
        if (data != null) {
            id = (String) data.get("id");
            status = (String) data.get("status");
            thesisId = (String) data.get("thesisId");
            studentUid = (String) data.get("studentUid");
            chatId = (String) data.get("chatId");
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("status", status);
        map.put("thesisId", thesisId);
        map.put("studentUid", studentUid);
        map.put("chatId", chatId);
        return map;
    }
}
