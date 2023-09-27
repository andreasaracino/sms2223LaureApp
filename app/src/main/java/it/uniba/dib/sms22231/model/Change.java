package it.uniba.dib.sms22231.model;

import it.uniba.dib.sms22231.config.ChangeTypes;

public class Change<T> {
    public T value;
    public ChangeTypes changeType;

    public Change(T value, ChangeTypes changeType) {
        this.value = value;
        this.changeType = changeType;
    }
}
