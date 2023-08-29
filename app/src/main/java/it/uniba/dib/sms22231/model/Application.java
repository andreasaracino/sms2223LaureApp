package it.uniba.dib.sms22231.model;

import java.util.HashMap;
import java.util.Map;

public class Application {

    public String id;
    public String status;
    public String thesisId;
    public String studentId;
    public Requirement[] requirement;

    public Application() {
    }

    public Application(String id, String status, String thesisId, String studentId, Requirement[] requirement) {
        this.id = id;
        this.status = status;
        this.thesisId = thesisId;
        this.studentId = studentId;
        this.requirement = requirement;
    }

    public Application(Map<String, Object> data) {
        id = (String) data.get("id");
        status = (String) data.get("status");
        thesisId = (String) data.get("thesisId");
        studentId = (String) data.get("studentId");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("status", status);
        map.put("thesisId", thesisId);
        map.put("studentId", studentId);
        return map;
    }
}
