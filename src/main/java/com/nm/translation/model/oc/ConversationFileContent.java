package com.nm.translation.model.oc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversationFileContent {
    private String filename;
    private String fileKey;
    private String fileHref;
    private Boolean isInlined;

    public ConversationFileContent() {}

    public ConversationFileContent(String filename, String fileKey) {
        this.filename = filename;
        this.fileKey = fileKey;
    }
}
