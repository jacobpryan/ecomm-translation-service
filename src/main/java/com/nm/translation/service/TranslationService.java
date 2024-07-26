package com.nm.translation.service;

import com.nm.translation.model.ecomm.EcommChat;
import com.nm.translation.model.ecomm.EcommContent;
import com.nm.translation.model.ecomm.EcommMessageType;
import com.nm.translation.model.oc.*;
import com.nm.translation.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class TranslationService {
    /**
     * Use this method to determine how a message is translated to OCRequestBody based on EcommMessageType
     */
    public OCRequestBody translateEcommContent(EcommContent ecommContent) throws Exception {
        OCRequestBody ocRequestBody = new OCRequestBody();
        RequestInfo requestInfo = new RequestInfo();
        ConversationOverview conversationOverview = new ConversationOverview();
        List<ConversationEvents> conversationEvents = new ArrayList<>();

        requestInfo.setRequestId(UUID.randomUUID().toString());
        conversationOverview.setConversationId(UUID.randomUUID().toString());
        conversationOverview.setConversationType(ecommContent.getOcSubtype());
        conversationOverview.setTitle(ecommContent.getSubject());
        conversationOverview.setDescription(ecommContent.getContext());
        String sendDate;
        if (ecommContent.getMessageType().equals(EcommMessageType.CHAT)) {
            sendDate = ecommContent.getChats().get(0).getSendDate();
            ecommContent.setSendDate(sendDate);
        } else {
            sendDate = ecommContent.getSendDate();
        }
        log.info("Final SendDate : " + ecommContent.getSendDate());
        if (ecommContent.getAttachmentKeyNames() != null && !ecommContent.getAttachmentKeyNames().isEmpty()) {
            for (String keyName : ecommContent.getAttachmentKeyNames()) {
                Participant attachInitParticipant = new Participant(ecommContent.getSender(), ecommContent.getSender(), Constants.OC_USER_TYPE_INITIATOR);
                String fileName = keyName.split("/")[1];
                ConversationFileContent fileContent = new ConversationFileContent(fileName, Constants.OC_FILE_KEY_DEFAULT + keyName);
                conversationEvents.add(new ConversationEvents(convertToEpochMillis(sendDate), Constants.OC_FILE_EVENT_TYPE, attachInitParticipant, fileContent));
            }
        }

        if (ecommContent.getMessageType().equals(EcommMessageType.CHAT)) {
            String chatSender = ecommContent.getChats().get(0).getSender();
            Participant initiatorParticipant = new Participant(chatSender, chatSender, Constants.OC_USER_TYPE_INITIATOR);
            conversationOverview.setInitiatorEmail(chatSender);
            conversationOverview.setInitiatorName(chatSender);
            conversationOverview.addInitialParticipant(initiatorParticipant);
            for (EcommChat chat : ecommContent.getChats()) {
                conversationOverview.addInitialParticipant(new Participant(chat.getSender()));
                conversationEvents.add(new ConversationEvents(chat));

            }
        } else if (ecommContent.getMessageType().getName().startsWith(EcommMessageType.GENERAL.getName())) {
            Participant initiatorParticipant = new Participant(ecommContent.getSender(), ecommContent.getSender(), Constants.OC_USER_TYPE_INITIATOR);
            conversationOverview.setInitiatorEmail(ecommContent.getSender());
            conversationOverview.setInitiatorName(ecommContent.getSender());
            conversationOverview.addInitialParticipant(initiatorParticipant);
            if (ecommContent.getRecipients() != null && !ecommContent.getRecipients().isEmpty()) {
                for (String recipient : ecommContent.getRecipients()) {
                    conversationOverview.addInitialParticipant(new Participant(recipient));
                }
            }

            if (ecommContent.getMessageType().equals(EcommMessageType.GENERAL_TEXT)) {
                ConversationContent conversationContent = new ConversationContent(ecommContent.getText());
                conversationEvents.add(new ConversationEvents(convertToEpochMillis(ecommContent.getSendDate()), Constants.OC_EVENT_TYPE_MESSAGE, initiatorParticipant, conversationContent));
            }
            if (ecommContent.getMessageType().equals(EcommMessageType.GENERAL_HTML)) {
                String html;
                if (Base64.isBase64(ecommContent.getHtml())) {
                    html = new String(Base64.decodeBase64(ecommContent.getHtml()));
                } else {
                    html = ecommContent.getHtml();
                }

                ConversationContent conversationContent = new ConversationContent(html, Constants.OC_HTML_CONTENT_TYPE);
                conversationEvents.add(new ConversationEvents(convertToEpochMillis(ecommContent.getSendDate()), Constants.OC_EVENT_TYPE_MESSAGE, initiatorParticipant, conversationContent));
            }
        } else {
            throw new Exception("Ecomm Message was not assigned a MessageType");
        }

        ocRequestBody.setRequestInfo(requestInfo);
        ocRequestBody.setConversationOverview(conversationOverview);
        ocRequestBody.setConversationEvents(conversationEvents);

        return ocRequestBody;
    }

    private long convertToEpochMillis(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        Instant instant = OffsetDateTime.parse(dateString, formatter).toInstant();
        return instant.toEpochMilli();
    }
}
