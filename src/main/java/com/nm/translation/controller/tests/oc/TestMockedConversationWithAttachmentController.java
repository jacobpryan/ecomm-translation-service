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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@Profile({"!prod"})
public class TestMockedConversationWithAttachmentController {
    @Autowired
    private OpenConnectorProxy ocProxy;

    /**
     * PostMapping which resolves to the following endpoint path for postman testing: /api/v1/ecomm-translation-service/oc/mock/v2/conversation/attachment.
     */
    @PostMapping(value = "/oc/mock/v2/conversation/attachment")
    public Map<String, String> publishMockedConversationWithAttachment(@RequestParam(required = false) Integer messageSendCount) {
        String reconcilationId = "";
        try {
            log.info("Entered Controller and sending test message to queue");
            // Mock OC Request Body

            if (messageSendCount == null) {
                reconcilationId = ocProxy.publishMockedConversationAttachmentMessage();
            } else {
                for (int i = 0; i <= messageSendCount; i++) {
                    ocProxy.publishMockedAttachment();
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
