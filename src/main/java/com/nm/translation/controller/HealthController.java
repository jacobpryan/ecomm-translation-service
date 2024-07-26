package com.nm.translation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Api Endpoint for determining the Application's availability.
 * If the expected response is received, the service is running
 * Not a very functional controller but used for testing and monitoring which is important
 */
@RestController
public class HealthController {
    /**
     * Controller Get Mapping will resolve with the endpoint path: /api/v1/ecomm-translation-service/
     * Always make sure your services are feeling well.
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "What's up... I am");
        return response;
    }
}
