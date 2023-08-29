package it.uniba.dib.sms22231.model;

import java.util.HashMap;
import java.util.Map;

public class Requirement {
    public String id;
    public String thesisId;
    public String name;
    public String values;

    public Requirement() {
    }

    public Requirement(String id, String thesisId, String name, String values) {
        this.id = id;
        this.thesisId = thesisId;
        this.name = name;
        this.values = values;
    }

    public Requirement(Map<String, Object> data) {
        id = (String) data.get("id");
        thesisId = (String) data.get("thesisId");
        name = (String) data.get("name");
        values = (String) data.get("values");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("thesisId", thesisId);
        map.put("name", name);
        map.put("values", values);
        return map;
    }
}
