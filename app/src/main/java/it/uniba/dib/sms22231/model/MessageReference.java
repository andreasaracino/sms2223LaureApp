package it.uniba.dib.sms22231.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import it.uniba.dib.sms22231.config.MessageReferenceType;

public class MessageReference implements Serializable {
    public MessageReferenceType messageReferenceType;
    public String referenceId;
    public String value;

    public MessageReference() {}

    public MessageReference(Map<String, Object> map) {
        messageReferenceType = MessageReferenceType.valueOf((String) map.get("messageReferenceType"));
        referenceId = (String) map.get("referenceId");
        value = (String) map.get("value");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("messageReferenceType", messageReferenceType);
        map.put("referenceId", referenceId);
        map.put("value", value);
        return map;
    }

}
