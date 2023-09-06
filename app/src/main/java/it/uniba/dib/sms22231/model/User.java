package it.uniba.dib.sms22231.model;

import java.util.HashMap;
import java.util.Map;

import it.uniba.dib.sms22231.config.UserTypes;

public class User {
    public String uid;
    public String email;
    public String fullName;
    public String registrationNumber;
    public UserTypes userType;

    public User() {}

    public User(Map<String, Object> data) {
        uid = (String) data.get("uid");
        email = (String) data.get("email");
        fullName = (String) data.get("fullName");
        registrationNumber = (String) data.get("registrationNumber");
        if (data.get("userType") != null) {
            userType = UserTypes.valueOf((String) data.get("userType"));
        }
    }

    public User(String uid, String email, String fullName, String registrationNumber, UserTypes userType) {
        this.uid = uid;
        this.email = email;
        this.fullName = fullName;
        this.registrationNumber = registrationNumber;
        this.userType = userType;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("email", email);
        map.put("fullName", fullName);
        map.put("registrationNumber", registrationNumber);
        map.put("userType", userType);
        return map;
    }
}
