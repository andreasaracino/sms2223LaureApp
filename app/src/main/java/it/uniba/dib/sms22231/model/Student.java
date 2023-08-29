package it.uniba.dib.sms22231.model;

import java.util.HashMap;
import java.util.Map;

public class Student {
    public String uid;
    public Thesis[] savedThesisIds;

    public Student() {
    }

    public Student(String uid, Thesis[] savedThesisIds) {
        this.uid = uid;
        this.savedThesisIds = savedThesisIds;
    }

    public Student(Map<String, Object> data) {
        uid = (String) data.get("uid");
        savedThesisIds = (Thesis[]) data.get("thesisId");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("savedThesisIds", savedThesisIds);
        return map;
    }
}
