package it.uniba.dib.sms22231.model;

import java.util.HashMap;
import java.util.Map;

public class Requirement {
    public String id;
    public String thesisId;
    public String description;
    public String values;

    public Requirement() {
    }

    public Requirement(String id, String thesisId, String description, String values) {
        this.id = id;
        this.thesisId = thesisId;
        this.description = description;
        this.values = values;
    }

    public Requirement(Map<String, Object> data) {
        id = (String) data.get("id");
        thesisId = (String) data.get("thesisId");
        description = (String) data.get("description");
        values = (String) data.get("values");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("thesisId", thesisId);
        map.put("description", description);
        map.put("values", values);
        return map;
    }
}
