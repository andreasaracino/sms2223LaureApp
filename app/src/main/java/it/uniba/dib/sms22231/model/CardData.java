package it.uniba.dib.sms22231.model;

public class CardData<T> {
    private String title;
    private String subtitle;
    private String id;
    private String rank;
    private T data;

    public CardData(String title, String subtitle, String id, String rank) {
        this.title = title;
        this.subtitle = subtitle;
        this.id = id;
        this.rank = rank;
    }

    public CardData(String title, String subtitle, String id, String rank, T data) {
        this.title = title;
        this.subtitle = subtitle;
        this.id = id;
        this.rank = rank;
        this.data = data;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
