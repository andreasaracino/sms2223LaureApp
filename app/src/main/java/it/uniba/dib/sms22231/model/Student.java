package it.uniba.dib.sms22231.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student {
    public String uid;
    public List<String> savedThesesIds;

    public Student() {
    }

    public Student(String uid, List<String> savedThesesIds) {
        this.uid = uid;
        this.savedThesesIds = savedThesesIds;
    }

    public Student(Map<String, Object> data) {
        if (data == null) return;

        uid = (String) data.get("uid");

        Object thesesObject = data.get("savedThesesIds");
        try {
            savedThesesIds = new ArrayList<>((Collection<String>) thesesObject);
        } catch (Exception e) {}
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        return map;
    }
}
