package it.uniba.dib.sms22231.model;

import java.util.HashMap;
import java.util.Map;


import it.uniba.dib.sms22231.config.RequirementTypes;

public class Requirement {
    public String id;
    public String thesisId;
    public RequirementTypes description;
    public String value;

    public Requirement() {
    }

    public Requirement(String id, String thesisId, RequirementTypes description, String value) {
        this.id = id;
        this.thesisId = thesisId;
        this.description = description;
        this.value = value;
    }

    public Requirement(Map<String, Object> data) {
        id = (String) data.get("id");
        thesisId = (String) data.get("thesisId");
        description = RequirementTypes.valueOf((String) data.get("description"));
        value = (String) data.get("value");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("thesisId", thesisId);
        map.put("description", description);
        map.put("value", value);
        return map;
    }
}
