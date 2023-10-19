package it.uniba.dib.sms22231.config;

import it.uniba.dib.sms22231.R;

public enum MessageReferenceType {
    thesis("THESIS", R.string.thesisRef),
    task("TASK", R.string.taskRef);

    private final String name;
    private final int stringRes;

    MessageReferenceType(String name, int stringRes) {
        this.name = name;
        this.stringRes = stringRes;
    }

    public String getName() {
        return name;
    }

    public int getStringRes() {
        return stringRes;
    }
}
