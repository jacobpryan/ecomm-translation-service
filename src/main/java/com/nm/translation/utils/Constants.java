package com.nm.translation.utils;

public final class Constants {
    private Constants() {
        throw new AssertionError("Cannot create instance of Constants as it is a final class.");
    }

    public static final String OC_EVENT_TYPE_MESSAGE = "Message";
    public static final String OC_HTML_CONTENT_TYPE = "html";
    public static final String OC_FILE_EVENT_TYPE = "File_transfer";
    public static final String OC_FILE_KEY_DEFAULT = "default/";
    public static final String OC_FILE_KEY_ECOMM = "Ecomm/";
    public static final String OC_USER_TYPE_INITIATOR = "initiator";
    public static final String ECOMM_CONTENT_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String ECOMM_CONTENT_ID_NAME = "EcommContentId";
    public static final String RECON_ID_NAME = "reconciliationId";
    public static final int PUBLISH_RETRY_COUNT = 2;
}
