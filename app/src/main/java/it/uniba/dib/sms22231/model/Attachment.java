package it.uniba.dib.sms22231.model;

import java.util.HashMap;
import java.util.Map;

public class Attachment {
    public String id;
    public String type;
    public String path;
    public String ownerUid;

    public Attachment() {
    }

    public Attachment(String id, String type, String path, String ownerUid) {
        this.id = id;
        this.type = type;
        this.path = path;
        this.ownerUid = ownerUid;
    }

    public Attachment(Map<String, Object> data) {
        id = (String) data.get("id");
        type = (String) data.get("type");
        path = (String) data.get("path");
        ownerUid = (String) data.get("ownerUid");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("type", type);
        map.put("path", path);
        map.put("ownerUid", ownerUid);
        return map;
    }
}
