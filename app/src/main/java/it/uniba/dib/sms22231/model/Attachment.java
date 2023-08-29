package it.uniba.dib.sms22231.model;

import java.util.HashMap;
import java.util.Map;

public class Attachment {
    public String id;
    public String type;
    public String path;
    public User[] authorizedUIDs;

    public Attachment() {
    }

    public Attachment(String id, String type, String path, User[] authorizedUIDs) {
        this.id = id;
        this.type = type;
        this.path = path;
        this.authorizedUIDs = authorizedUIDs;
    }

    public Attachment(Map<String, Object> data) {
        id = (String) data.get("id");
        type = (String) data.get("type");
        path = (String) data.get("path");
        authorizedUIDs = (User[]) data.get("authorizedUIDs");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("type", type);
        map.put("path", path);
        map.put("authorizedUIDs", authorizedUIDs);
        return map;
    }
}
