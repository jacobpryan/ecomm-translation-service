package com.nm.translation.model.oc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversationOverview {
    private String conversationId;

    private String conversationType;

    private String initiatorName;

    private String initiatorEmail;

    private String title;

    private String description;

    private List<Participant> initialParticipants = new ArrayList<>();

    public void addInitialParticipant(Participant participant) {
        this.initialParticipants.add(participant);
    }
}
