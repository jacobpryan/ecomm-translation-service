package com.nm.translation.controller;

import com.nm.translation.proxy.OpenConnectorProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Slf4j
@RestController
@Profile({"!prod"})
public class TestOCConversationController {
    @Autowired
    private OpenConnectorProxy ocPayloadService;

    /**
     * PostMapping which resolves to the following endpoint path for postman testing: /api/v1/ecomm-translation-service/test-oc-conversation.
     */
    @PostMapping(value = "/oc/test")
    public Map<String, String> publishOCMessage(@RequestParam(required = false) Integer messageSendCount) {
        String reconcilationId = "";
        try {
            log.info("Entered Controller and sending test message to queue");
            // Mock OC Request Body

            if (messageSendCount == null) {
                reconcilationId = ocPayloadService.publishMockedConversationMessage();
            } else {
                for (int i = 0; i <= messageSendCount; i++) {
                    ocPayloadService.publishMockedConversationMessage();
                }
            }

        } catch (Exception exception) {
            log.error("Error sending payload to oc api", exception);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Map<String, String> response = new HashMap<>();
        response.put("reconcilationId:", reconcilationId);
        return response;
    }
}
