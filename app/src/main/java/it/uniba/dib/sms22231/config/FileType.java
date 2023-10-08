package it.uniba.dib.sms22231.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum FileType {
    image("IMAGE", Arrays.asList("jpg", "jpeg", "png", "gif", "svg", "webp", "tif", "tiff", "bmp", "ico")),
    video("VIDEO", Arrays.asList("mp4", "mov", "wmv", "avi", "mkv")),
    document("DOCUMENT", Collections.singletonList("pdf")),
    archive("ARCHIVE", Arrays.asList("zip", "7z", "tar", "gz", "jar", "rar")),
    generic("GENERIC", null);

    private final String fileType;
    private final List<String> extensions;

    FileType(String fileType, List<String> extensions) {
        this.fileType = fileType;
        this.extensions = extensions;
    }

    public String getFileType() {
        return fileType;
    }

    public List<String> getExtensions() {
        return extensions;
    }

    public static FileType getFileTypeByExtension(String extension) {
        if(image.getExtensions().contains(extension)) {
            return image;
        } else if(video.getExtensions().contains(extension)) {
            return video;
        } else if (document.getExtensions().contains(extension)) {
            return document;
        } else if (archive.getExtensions().contains(extension)) {
            return archive;
        } else {
            return generic;
        }
    }
}
