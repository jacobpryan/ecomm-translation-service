package com.nm.translation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.*;
import com.nm.translation.exception.SqsServiceException;
import com.nm.translation.proxy.OpenConnectorProxy;
import com.nm.translation.client.OCSqsClient;
import com.nm.translation.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.LinkedList;
import java.util.List;

/**
 * Purpose of the PollingService is to control when a connection request is made to the SQS to consume messages
 */
@Slf4j
@Service
public class PollingService {
    @Autowired
    private OCSqsClient ocSqsClient;

    @Autowired
    private OpenConnectorProxy ocPayloadService;

    @Autowired
    ContentDataService contentDataService;

    private final Gson GSON = new GsonBuilder().create();

    // Control when service is polling queue and catch any thrown exceptions
    public void run() {
        try {
            poll();
        } catch (Exception exception) {
            log.error("An error occurred while polling for messages", exception);
        }
    }

    /**
     * Tells service to create a new connection to the queue and start polling for the next message
     */
    private void poll() {
        List<Message> messages = pullMessages();

        if (messages != null && !messages.isEmpty()) {
            for (Message message : messages) {
                try {
                    log.info("#####: Process Consumed message");
                    processMessage(message);
                } catch (JsonProcessingException exception) {
                    log.error("An error occurred while trying to map the request body from JSON", exception);
                } catch (Exception exception) {
                    log.error("An error occurred while trying to process messages", exception);
                }
            }
        }
    }

    /**
     * Receives a batch of messages from the queue and handles any processing exceptions.
     */
    public List<Message> pullMessages() {
        try {
            return ocSqsClient.receiveMessages();
        } catch (SqsServiceException exception) {
            log.error("An error occurred while trying to pull messages from SQS", exception);
        } catch (Exception exception) {
            log.error("An error occurred while trying to process SQS messages", exception);
        }

        return new LinkedList<>();
    }

    /**
     * The process method is currently just used for null/empty checks on SQS Messages. This is the first point
     * for parsing and checking the Message data, so this may be a good place for any data validation, throttling logic, message filtering, etc.
     */
    public void processMessage(Message message) throws Exception {
        if (message == null || !StringUtils.hasLength(message.body())) {
            throw new IllegalArgumentException("null Message objects cannot be processed");
        }

        String ecommContentId = parseEcommContentId(message.body());
        log.info("Parsed EcommContentId: {}", ecommContentId);

        if (StringUtils.hasLength(ecommContentId)) {
            /** calling contentDataService class
             * to fetch the data from db
             */
            ocSqsClient.deleteMessage(message);
            ocPayloadService.processAndPublishEcommMessage(ecommContentId, Constants.PUBLISH_RETRY_COUNT);
        }
    }

    private String parseEcommContentId(String body) {
        JsonObject sqsMessageJson = GSON.fromJson(body, JsonObject.class);

        if (sqsMessageJson.has(Constants.ECOMM_CONTENT_ID_NAME)) {
            String contentId = sqsMessageJson.getAsJsonPrimitive(Constants.ECOMM_CONTENT_ID_NAME).getAsString();
            if (StringUtils.hasText(contentId)) {
                return contentId;
            }
        }
        return null;
    }
}
