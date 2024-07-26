package com.nm.translation.model.ecomm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class EcommChat {
    @JsonProperty("sender")
    private String sender;
    @JsonProperty("recipients")
    private List<String> recipients;
    @JsonProperty("message")
    private String message;
    @JsonProperty("sendDate")
    private String sendDate;
}
