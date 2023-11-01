package it.uniba.dib.sms22231.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Thesis {
    public String id;
    public String title;
    public String description;
    public String assistantSupervisor;
    public String teacherId;
    public String teacherFullname;
    public List<String> attachments;
    public List<Requirement> requirements;
    public Integer averageRequirement;

    public Thesis() {
    }

    public Thesis(String id, String title, String description, String assistantSupervisor, String teacherId, String teacherFullname, List<String> attachments, List<Requirement> requirements, Integer averageRequirement) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.assistantSupervisor = assistantSupervisor;
        this.teacherId = teacherId;
        this.teacherFullname = teacherFullname;
        this.attachments = attachments;
        this.requirements = requirements;
        this.averageRequirement = averageRequirement;
    }

    public Thesis(Map<String, Object> data) {
        id = (String) data.get("id");
        title = (String) data.get("title");
        description = (String) data.get("description");
        assistantSupervisor = (String) data.get("assistantSupervisor");
        teacherId = (String) data.get("teacherId");

        Object attachmentsObject = data.get("attachmentIds");

        try {
            attachments = new ArrayList<>((Collection<String>) attachmentsObject);
        } catch (Exception e) {}
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("title", title);
        map.put("description", description);
        map.put("teacherId", teacherId);
        map.put("assistantSupervisor", assistantSupervisor);
        return map;
    }
}
