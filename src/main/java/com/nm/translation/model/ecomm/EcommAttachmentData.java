package com.nm.translation.model.ecomm;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EcommAttachmentData {

    @JsonProperty("fileName")
    private String fileName;

    @JsonProperty("fileKey")
    private String fileKey;

    @JsonProperty("fileHref")
    private  String fileHref;

    @JsonProperty("isInlined")
    private boolean isInlined;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getFileHref() {
        return fileHref;
    }

    public void setFileHref(String fileHref) {
        this.fileHref = fileHref;
    }

    public boolean isInlined() {
        return isInlined;
    }

    public void setInlined(boolean inlined) {
        isInlined = inlined;
    }
}
