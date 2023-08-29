package it.uniba.dib.sms22231.model;

import java.util.HashMap;
import java.util.Map;

public class Student {
    public String uid;
    public Thesis[] thesis;

    public Student() {
    }

    public Student(String uid, Thesis[] thesis) {
        this.uid = uid;
        this.thesis = thesis;
    }

    public Student(Map<String, Object> data) {
        uid = (String) data.get("uid");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        return map;
    }
}
