package com.nm.translation.client;

import com.nm.translation.exception.SqsServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

/**
 * Proxy for SqsClient operations
 */
@Slf4j
@Service
public class OCSqsClient {
    @Autowired
    private SqsClient sqs;

    @Value("${ecomm.translation.sqs.name}")
    private String queueName;
    @Value("${ecomm.translation.sqs.url}")
    private String queueUrl;

    // time defined in seconds
    // must be >= 0 and <= 20
    private final int sqsPollingTime = 20;
    private final int messagesCountPerPoll = 1;

    /**
     * Builds a new SQS Connection which is persists until the maxNumberOfMessage count is met.
     * maxNumberOfMessage is set to 1 as default meaning the connection is being rebuilt for each consumed message.
     */
    public List<Message> receiveMessages() throws SqsServiceException {
        try {
            // maxNumberOfMessages() must be between 1 and 10 or else SqsException is thrown
            ReceiveMessageRequest messageRequest =
                    ReceiveMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .waitTimeSeconds(this.sqsPollingTime) // long polling (cheaper, less chance of empty response)
                            .messageAttributeNames(".*") // used to grab all message attributes
                            .maxNumberOfMessages(this.messagesCountPerPoll) // default is 1
                            .build();

            ReceiveMessageResponse response = sqs.receiveMessage(messageRequest);
            if(response.hasMessages()) {
                return response.messages();
            }

        } catch (Exception exception) {
            throw new SqsServiceException(
                    "Issue occurred when trying to build connection to receive messages from queue.",
                    exception
            );
        }

        return null;
    }

    /**
     * Builds a new SQS connection and deletes the provided message from the queue. The receiptHandle value used for
     * matching messages comes from originally consumed message in the PollingService.
     */
    public void deleteMessage(Message message) throws SqsServiceException {
        try {
            DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                    .queueUrl(queueName)
                    .receiptHandle(message.receiptHandle())
                    .build();

            sqs.deleteMessage(deleteMessageRequest);

        } catch (Exception exception) {
            throw new SqsServiceException(
                    "Issue occurred when trying to delete connection to delete message from queue.",
                    exception
            );
        }
    }

    /**
     * This function will only be used for Postman testing.
     * Builds a new SQS Connection and sends a message to the queue.
     * SQS Connection is destroyed after SQS responds with a successful message ingestion.
     */
    @Profile("!prod")
    public void sendMessage(String message) throws SqsServiceException {
        try {
            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(message)
                    .build();

            sqs.sendMessage(sendMessageRequest);

        } catch (Exception exception) {
            throw new SqsServiceException(
                    "Issue occurred when trying to send message to incoming queue",
                    exception
            );
        }
    }
}
