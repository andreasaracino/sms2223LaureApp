package it.uniba.dib.sms22231.model;

public class ListData {
    private int imageId;
    private String text;

    public ListData() {
    }

    public ListData(int imageId, String text) {
        this.imageId = imageId;
        this.text = text;
    }

    public int getImageId() {
        return imageId;
    }

    public String getText() {
        return text;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public void setText(String text) {
        this.text = text;
    }
}