package it.uniba.dib.sms22231.model;

public class CardData {
    private String title;
    private String name;

    public CardData(String title, String name) {
        this.title = title;
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }
}
