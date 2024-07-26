package com.nm.translation.model.oc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OCRequestBody {
    private RequestInfo requestInfo;

    private ConversationOverview conversationOverview;

    private List<ConversationEvents> conversationEvents;
}
