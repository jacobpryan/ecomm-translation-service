package com.nm.translation.model.oc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversationContent {
    private String message;
    private String contentType;

    public ConversationContent() {}

    public ConversationContent(String message) {
        this.message = message;
    }

    public ConversationContent(String message, String contentType) {
        this.message = message.replaceAll("\\\\n", "\n");
        this.contentType = contentType;
    }
}
