package it.uniba.dib.sms22231.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import it.uniba.dib.sms22231.config.TaskStatus;
import it.uniba.dib.sms22231.service.TaskService;
import it.uniba.dib.sms22231.utility.TimeUtils;

public class Task {
    public String id;
    public TaskStatus status;
    public String title;
    public String description;
    public String applicationId;
    public Date dueDate;

    public Task() {
    }

    public Task(String id, TaskStatus status, String title, String description, String applicationId, Date dueDate) {
        this.id = id;
        this.status = status;
        this.title = title;
        this.description = description;
        this.applicationId = applicationId;
        this.dueDate = dueDate;
    }

    public Task(Map<String, Object> data) {
        id = (String) data.get("id");
        status = TaskStatus.valueOf((String) data.get("status"));
        title = (String) data.get("title");
        description = (String) data.get("description");
        applicationId = (String) data.get("applicationId");
        dueDate = TimeUtils.stringToDate((String) data.get("dueDate"), false);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("status", status);
        map.put("title", title);
        map.put("description", description);
        map.put("applicationId", applicationId);
        map.put("dueDate", TimeUtils.dateToString(dueDate, false));
        return map;
    }
}
