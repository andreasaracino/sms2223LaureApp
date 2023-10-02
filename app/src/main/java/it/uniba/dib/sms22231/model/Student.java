package it.uniba.dib.sms22231.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student {
    public String uid;
    public Map<String, String> savedThesesIds;

    public Student() {
    }

    public Student(String uid, Map<String, String> savedThesesIds) {
        this.uid = uid;
        this.savedThesesIds = savedThesesIds;
    }

    public Student(Map<String, Object> data) {
        if (data == null) return;

        uid = (String) data.get("uid");

        try {
            savedThesesIds = (Map<String, String>) data.get("savedThesesIds");
        } catch (Exception e) {
            savedThesesIds = new HashMap<>();
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        return map;
    }
}
