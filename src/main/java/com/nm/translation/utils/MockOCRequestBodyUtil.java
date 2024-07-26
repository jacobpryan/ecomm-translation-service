package com.nm.translation.utils;

import com.nm.translation.model.oc.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class MockOCRequestBodyUtil {
    public OCRequestBody getMockedOCRequestBody() {
        RequestInfo requestInfo = new RequestInfo();
        ConversationOverview conversationOverview = new ConversationOverview();
        List<ConversationEvents> conversationEvents = new ArrayList<>();
        OCRequestBody ocRequestBody = new OCRequestBody();
        List<Participant> participants = new ArrayList<>();
        Participant initiatalParticipant = new Participant();
        UUID requestId = UUID.randomUUID();
        UUID conversationId = UUID.randomUUID();

        initiatalParticipant.setCorporateEmail("jacobryan@nmcop.com");
        initiatalParticipant.setUserType("initiator");
        initiatalParticipant.setDisplayName("Jacob Ryan");
        participants.add(initiatalParticipant);
        conversationOverview.setInitialParticipants(participants);
        requestInfo.setRequestId(requestId.toString());
        conversationOverview.setConversationType("InMeetingChat-DM");
        conversationOverview.setConversationId(conversationId.toString());
        conversationOverview.setInitiatorEmail("jacobryan@nmcop.com");
        conversationOverview.setInitiatorName("Jacob Ryan");
        conversationEvents.add(mockConversationEventsHelper());
        ocRequestBody.setRequestInfo(requestInfo);
        ocRequestBody.setConversationOverview(conversationOverview);
        ocRequestBody.setConversationEvents(conversationEvents);

        return ocRequestBody;
    }

    public OCRequestBody getMockedOCRequestBodyWithAttachment() {
        RequestInfo requestInfo = new RequestInfo();
        ConversationOverview conversationOverview = new ConversationOverview();
        List<ConversationEvents> conversationEvents = new ArrayList<>();
        OCRequestBody ocRequestBody = new OCRequestBody();
        List<Participant> participants = new ArrayList<>();
        Participant initiatalParticipant = new Participant();
        UUID requestId = UUID.randomUUID();
        UUID conversationId = UUID.randomUUID();

        initiatalParticipant.setCorporateEmail("jacobryan@nmcop.com");
        initiatalParticipant.setUserType("initiator");
        initiatalParticipant.setDisplayName("Jacob Ryan");
        participants.add(initiatalParticipant);
        conversationOverview.setInitialParticipants(participants);
        requestInfo.setRequestId(requestId.toString());
        conversationOverview.setConversationType("InMeetingChat-DM");
        conversationOverview.setConversationId(conversationId.toString());
        conversationOverview.setInitiatorEmail("jacobryan@nmcop.com");
        conversationOverview.setInitiatorName("Jacob Ryan");
        conversationEvents.add(mockConversationEventsHelper());
        conversationEvents.add(mockAttachmentConversationEventHelper());
        ocRequestBody.setRequestInfo(requestInfo);
        ocRequestBody.setConversationOverview(conversationOverview);
        ocRequestBody.setConversationEvents(conversationEvents);

        return ocRequestBody;
    }

    private ConversationEvents mockConversationEventsHelper() {
        ConversationEvents event = new ConversationEvents();
        ConversationContent content = new ConversationContent();

        content.setMessage("This is a test message from the Translation Service");

        event.setParticipants(mockParticipantHelper());
        event.setEventTime(System.currentTimeMillis());
        event.setEventType("Message");
        event.setContent(content);

        return event;
    }

    private ConversationEvents mockAttachmentConversationEventHelper() {
        ConversationEvents event = new ConversationEvents();
        List<ConversationFileContent> fileContentList = new ArrayList<>();
        ConversationFileContent fileContent = new ConversationFileContent();

        fileContent.setFilename("ecomm-test-data.txt");
        fileContent.setFileKey("default/ecomm-test-data.txt");
        fileContent.setFileHref("https://docs.spring.io/spring-security/site/docs/5.2.12.RELEASE/reference/html/images/tip.png");
        fileContent.setIsInlined(true);
        fileContentList.add(fileContent);

        event.setParticipants(mockParticipantHelper());
        event.setEventTime(System.currentTimeMillis());
        event.setEventType("File_transfer");
        event.setFiles(fileContentList);

        return event;
    }

    private List<Participant> mockParticipantHelper() {
        List<Participant> participants = new ArrayList<>();
        Participant initiatorParticipant = new Participant();

        initiatorParticipant.setCorporateEmail("jacobryan@nmcop.com");
        initiatorParticipant.setUserType("initiator");
        initiatorParticipant.setDisplayName("Jacob Ryan");

        participants.add(initiatorParticipant);

        Participant recipientParticipant = new Participant();

        recipientParticipant.setCorporateEmail("gouthamnarra@nmcop.com");
        recipientParticipant.setUserType("recipient");
        recipientParticipant.setDisplayName("Goutham Narra");

        participants.add(recipientParticipant);

        return participants;
    }
}