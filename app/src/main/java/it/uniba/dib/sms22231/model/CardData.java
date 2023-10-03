package it.uniba.dib.sms22231.model;

public class CardData {
    private String title;
    private String subtitle;
    private String id;
    private String rank;

    public CardData(String title, String subtitle, String id, String rank) {
        this.title = title;
        this.subtitle = subtitle;
        this.id = id;
        this.rank = rank;
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

    public String getRank(){return rank;}

    public void setRank(String rank) {
        this.rank = rank;
    }
}
