package it.uniba.dib.sms22231.model;

import java.util.HashMap;
import java.util.Map;

public class Meeting {
    public String id;
    public String applicationId;
    public String taskId;
    public String date;
    public String time;
    public String title;

    public Meeting() {
    }

    public Meeting(String id, String applicationId, String taskId, String date, String time, String title) {
        this.id = id;
        this.applicationId = applicationId;
        this.taskId = taskId;
        this.date = date;
        this.time = time;
        this.title = title;
    }

    public Meeting(Map<String, Object> data) {
        id = (String) data.get("id");
        applicationId = (String) data.get("applicationId");
        taskId = (String) data.get("taskId");
        date = (String) data.get("date");
        time = (String) data.get("time");
        title = (String) data.get("title");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("applicationId", applicationId);
        map.put("taskId", taskId);
        map.put("date", date);
        map.put("time", time);
        map.put("title", title);
        return map;
    }
}
