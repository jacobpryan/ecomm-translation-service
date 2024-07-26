package com.nm.translation.model;

public class AttachmentDetailsData {

    private String folder;
    private String fileName;

    public AttachmentDetailsData() {
    }

    public AttachmentDetailsData(String folder, String fileName) {
        this.folder = folder;
        this.fileName = fileName;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "AttachmentDetails{" +
                "folder='" + folder + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
