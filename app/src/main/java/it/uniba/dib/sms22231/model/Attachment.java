package it.uniba.dib.sms22231.model;

import android.net.Uri;

import org.checkerframework.checker.units.qual.A;

import it.uniba.dib.sms22231.config.FileType;

public class Attachment {
    public String id;
    public Uri path;
    private String fileName;
    public FileType fileType;

    public Attachment() {}

    public Attachment(String id, Uri path, String fileName, FileType fileType) {
        this.id = id;
        this.path = path;
        this.fileName = fileName;
        this.fileType = fileType;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;

        int pointIndex = fileName.indexOf(".");
        if (pointIndex != -1) {
            fileType = FileType.getFileTypeByExtension(fileName.substring(pointIndex + 1).toLowerCase());
        } else {
            fileType = FileType.generic;
        }
    }

    public String getFileName() {
        return fileName;
    }
}
