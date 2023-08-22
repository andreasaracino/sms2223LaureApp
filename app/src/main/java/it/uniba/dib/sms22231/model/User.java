package it.uniba.dib.sms22231.model;

import java.util.HashMap;
import java.util.Map;

public class User {
    public String uid;
    public String email;
    public String fullName;
    public String phoneNumber;
    public String registrationNumber;

    public User() {}

    public User(Map<String, Object> data) {
        uid = (String) data.get("uid");
        email = (String) data.get("email");
        fullName = (String) data.get("fullName");
        phoneNumber = (String) data.get("phoneNumber");
        registrationNumber = (String) data.get("registrationNumber");
    }

    public User(String uid, String email, String fullName, String phoneNumber, String registrationNumber) {
        this.uid = uid;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.registrationNumber = registrationNumber;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("email", email);
        map.put("fullName", fullName);
        map.put("phoneNumber", phoneNumber);
        map.put("registrationNumber", registrationNumber);
        return map;
    }
}
