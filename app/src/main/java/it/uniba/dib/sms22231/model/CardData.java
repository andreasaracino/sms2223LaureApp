package it.uniba.dib.sms22231.model;

public class CardData {
    private String title;
    private String subtitle;
    private String id;

    public CardData(String title, String subtitle, String id) {
        this.title = title;
        this.subtitle = subtitle;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getId() {
        return id;
    }
}
