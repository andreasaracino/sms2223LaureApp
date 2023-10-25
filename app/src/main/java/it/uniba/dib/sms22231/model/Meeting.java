package it.uniba.dib.sms22231.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms22231.utility.TimeUtils;

public class Meeting {
    public String id;
    public String applicationId;
    public List<String> taskIds;
    public Date date;
    public String title;
    public String subject;

    public Meeting() {
    }

    public Meeting(String id, String applicationId, List<String> taskIds, Date date, String title, String subject) {
        this.id = id;
        this.applicationId = applicationId;
        this.taskIds = taskIds;
        this.date = date;
        this.title = title;
        this.subject = subject;
    }

    public Meeting(Map<String, Object> data) {
        id = (String) data.get("id");
        applicationId = (String) data.get("applicationId");
        taskIds = (List<String>) data.get("taskIds");
        date = TimeUtils.stringToDate((String) data.get("date"), false);
        title = (String) data.get("title");
        subject = (String) data.get("subject");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("applicationId", applicationId);
        map.put("taskIds", taskIds);
        map.put("date", TimeUtils.dateToString(date, false));
        map.put("title", title);
        map.put("subject", subject);
        return map;
    }
}
