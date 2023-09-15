package it.uniba.dib.sms22231.model;

public class CardData {
    private String title;
    private String subtitle;

    public CardData(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
