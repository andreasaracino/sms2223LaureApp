package it.uniba.dib.sms22231.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import it.uniba.dib.sms22231.config.ApplicationStatus;

public class Application {

    public String id;
    public ApplicationStatus status;
    public String thesisId;
    public String studentUid;
    public List<Requirement> requirements;
    public String thesisTitle;
    public String studentName;

    public Application() {}

    public Application(String id, ApplicationStatus status, String thesisId, String studentUid, List<Requirement> requirements, String thesisTitle, String studentName) {
        this.id = id;
        this.status = status;
        this.thesisId = thesisId;
        this.studentUid = studentUid;
        this.requirements = requirements;
        this.thesisTitle = thesisTitle;
        this.studentName = studentName;
    }

    public Application(Map<String, Object> data) {
        if (data != null) {
            id = (String) data.get("id");
            status = ApplicationStatus.valueOf((String) data.get("status"));
            thesisId = (String) data.get("thesisId");
            studentUid = (String) data.get("studentUid");
            try {
                List<Map<String, Object>> requirements = (List<Map<String, Object>>) data.get("requirements");
                this.requirements = requirements.stream().map(Requirement::new).collect(Collectors.toList());
            } catch (Exception e) {
                requirements = new ArrayList<>();
            }
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("status", status);
        map.put("thesisId", thesisId);
        map.put("studentUid", studentUid);
        return map;
    }
}
