package com.nm.translation.model.oc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nm.translation.model.ecomm.EcommChat;
import com.nm.translation.utils.Constants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversationEvents {
    private Long eventTime;

    private String eventType;

    private List<Participant> participants = new ArrayList<>();

    private ConversationContent content;

    private List<ConversationFileContent> files = new ArrayList<>();

    public ConversationEvents() {}

    public ConversationEvents(EcommChat chat) {
        this.eventTime = convertToEpochMillis(chat.getSendDate());
        log.info("Send date in ConversationEvents " + this.eventTime);
        this.eventType = Constants.OC_EVENT_TYPE_MESSAGE;
        this.participants.add(new Participant(chat.getSender(), chat.getSender(), Constants.OC_USER_TYPE_INITIATOR));
        this.content = new ConversationContent(chat.getMessage(), Constants.OC_HTML_CONTENT_TYPE);
        for (String recipient : chat.getRecipients()) {
            this.participants.add(new Participant(recipient));
        }
    }

    public ConversationEvents(Long eventTime, String eventType, Participant participant, ConversationContent content) {
        this.eventTime = eventTime;
        this.eventType = eventType;
        this.participants.add(participant);
        this.content = content;
    }

    public ConversationEvents(Long eventTime, String eventType, Participant participants, ConversationFileContent fileContent) {
        this.eventTime = eventTime;
        this.eventType = eventType;
        this.participants.add(participants);
        this.files.add(fileContent);
    }

    private Long dateToEpochHelper(String date) {
        try {
            log.info("dateToEpochHelper in: {}", date);
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.ECOMM_CONTENT_DATE_PATTERN);
            Date parsedDate = dateFormat.parse(date);
            log.info("dateToEpochHelper out: {}", parsedDate.getTime());
            return parsedDate.getTime();
        } catch (ParseException ex) {
            return System.currentTimeMillis();
        }
    }

    private long convertToEpochMillis(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        Instant instant = OffsetDateTime.parse(dateString, formatter).toInstant();
        return instant.toEpochMilli();
    }
}
