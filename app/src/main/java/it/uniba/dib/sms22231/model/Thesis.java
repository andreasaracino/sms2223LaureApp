package it.uniba.dib.sms22231.model;

import java.util.HashMap;
import java.util.Map;

public class Thesis {
    public String id;
    public String title;
    public String description;
    public String teacherId;
    public Attachment[] attachments;

    public Thesis() {
    }

    public Thesis(String id, String title, String description, String teacherId, Attachment[] attachments) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.teacherId = teacherId;
        this.attachments = attachments;
    }

    public Thesis(Map<String, Object> data) {
        id = (String) data.get("id");
        title = (String) data.get("title");
        description = (String) data.get("description");
        teacherId = (String) data.get("teacherId");
        attachments = (Attachment[]) data.get ("attachments");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("title", title);
        map.put("description", description);
        map.put("teacherId", teacherId);
        map.put("attachments", attachments);
        return map;
    }
}
