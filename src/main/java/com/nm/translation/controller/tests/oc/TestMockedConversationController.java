package com.nm.translation.controller.tests.oc;

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
public class TestMockedConversationController {
    @Autowired
    private OpenConnectorProxy ocProxy;

    /**
     * PostMapping which resolves to the following endpoint path for postman testing: /api/v1/ecomm-translation-service/oc/mock/v2/conversation.
     */
    @PostMapping(value = "/oc/mock/v2/conversation")
    public Map<String, String> publishMockedConversationNoFile(@RequestParam(required = false) Integer messageSendCount) {
        String reconcilationId = "";
        try {
            log.info("Entered Controller and sending test message to queue");
            // Mock OC Request Body
            if (messageSendCount == null) {
                reconcilationId = ocProxy.publishMockedConversationMessage();
            } else {
                for (int i = 0; i <= messageSendCount; i++) {
                    ocProxy.publishMockedConversationMessage();
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
