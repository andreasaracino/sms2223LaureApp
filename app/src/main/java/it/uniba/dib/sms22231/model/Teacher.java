package it.uniba.dib.sms22231.model;

import java.util.HashMap;
import java.util.Map;

public class Teacher {
    public String uid;

    public Teacher() {
    }

    public Teacher(String uid) {
        this.uid = uid;
    }

    public Teacher(Map<String, Object> data) {
        if (data == null) return;

        uid = (String) data.get("uid");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        return map;
    }
}
