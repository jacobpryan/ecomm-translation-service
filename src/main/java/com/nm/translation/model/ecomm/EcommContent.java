package com.nm.translation.model.ecomm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nm.translation.utils.Constants;
import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EcommContent {
    // Used in ALL message types
    @JsonProperty("source")
    private String source;
    @JsonProperty("contentType")
    private String contentType;
    @JsonProperty("context")
    private String context;
    @JsonProperty("requestId")
    private String requestId;


    // Used in both CHAT and GENERAL message types
    @JsonProperty("subject")
    private String subject;

    // Only used for Chat type messages
    @JsonProperty("chats")
    private List<EcommChat> chats;

    // Only used for General message type
    // Will exclusively use html or text field
    @JsonProperty("html")
    private String html;
    @JsonProperty("text")
    private String text;
    @JsonProperty("sendDate")
    private String sendDate;
    @JsonProperty("direction")
    private String direction;
    @JsonProperty("sender")
    private String sender;
    @JsonProperty("recipients")
    private List<String> recipients;

    private List<String> attachmentKeyNames = new ArrayList<>();

    private EcommMessageType messageType;

    private String ocSubtype;

    public void setChats(List<EcommChat> chats) {
        this.chats = chats;
        this.messageType = EcommMessageType.CHAT;
    }

    public void setHtml(String html) {
        this.html = html;
        this.messageType = EcommMessageType.GENERAL_HTML;
    }

    public void setText(String text) {
        this.text = text;
        this.messageType = EcommMessageType.GENERAL_TEXT;
    }

//    public void setSendDate(String sendDate) {
//        try {
//            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.ECOMM_CONTENT_DATE_PATTERN);
//            this.sendDate = dateFormat.parse(sendDate);
//        } catch (ParseException ex) {
//            this.sendDate = Date.from(Instant.now());
//        }
//    }
}
