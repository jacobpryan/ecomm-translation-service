package com.nm.translation.controller.tests;

import com.nm.translation.client.OCSqsClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Profile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller which was created to send test messages to the SQS running in the AWS-LPA-NON account.
 *      This allows the developer to test their changes directly against a live queue in AWS.
 * Controller will not be compiled and endpoint not exposed in Production Environment.
 *      This adds security around our controller and ensures nothing is accidentally deployed to prod.
 * Expect to add more Test controllers like this when implementing features like Message Reprocessing with the DLQ.
 */
@Slf4j
@RestController
@Profile({"!prod"})
public class TestE2EMockedMessageController {
    @Autowired
    private OCSqsClient ocSqsClient;

    /**
     * Test API which resolves to the endpoint: /api/v1/ecomm-translation-service/e2e/mock/sqs/oc
     * Includes an optional parameter for continuously sending the same message payload to the SQS x number of times.
     * If you would like to create more Postman Tests, you can use this as a guide.
     * Having more Postman tests will allow for easier testing during development.
     */
    @PostMapping(value = "/e2e/mock/sqs/oc")
    public Map<String, String> publishE2EMockedMessage(@RequestParam(required = false) Integer messageSendCount, @RequestBody String message) {
        try {
            if (messageSendCount == null) {
                log.info("Entered Controller and sending test message to queue");
                ocSqsClient.sendMessage(message);
            } else {
                log.info("Entered Controller and sending test message to queue");
                for (int i = 0; i <= messageSendCount; i++) {
                    ocSqsClient.sendMessage(message);
                }
            }

        } catch (Exception exception) {
            log.error("Error sending message to incoming queue", exception);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Map<String, String> response = new HashMap<>();
        response.put("status", "looking good");
        return response;
    }
}
