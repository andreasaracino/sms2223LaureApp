package it.uniba.dib.sms22231.model;

import java.util.HashMap;
import java.util.Map;

public class Task {
    public String id;
    public String status;
    public String title;
    public String description;
    public String applicationId;

    public Task() {
    }

    public Task(String id, String status, String title, String description, String applicationId) {
        this.id = id;
        this.status = status;
        this.title = title;
        this.description = description;
        this.applicationId = applicationId;
    }

    public Task(Map<String, Object> data) {
        id = (String) data.get("id");
        status = (String) data.get("status");
        title = (String) data.get("title");
        description = (String) data.get("description");
        applicationId = (String) data.get("applicationId");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("status", status);
        map.put("title", title);
        map.put("description", description);
        map.put("applicationId", applicationId);
        return map;
    }
}
