package com.nm.translation.model.ecomm;

public enum EcommMessageType {
    CHAT("CHAT"),
    GENERAL("GENERAL"),
    GENERAL_HTML("GENERAL_HTML"),
    GENERAL_TEXT("GENERAL_TEXT"),
    EML("EML");

    private String name;

    EcommMessageType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static String valueOfEntity(String name) {
        for (EcommMessageType entity : values()) {
            if (name != null && entity.name.equalsIgnoreCase(name.trim())) {
                return entity.toString();
            }
        }
        return name;
    }

}
