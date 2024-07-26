package com.nm.translation.client;

import com.nm.translation.config.OpenConnectorTokenManager;
import com.nm.translation.model.oc.OCRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.activation.MimetypesFileTypeMap;

@Slf4j
@Component
public class OpenConnectorClientImp implements OpenConnectorClient {
    private final OpenConnectorTokenManager tokenManager;

    private final RestTemplate restTemplate;

    private final MimetypesFileTypeMap mimetypesMap;

    public OpenConnectorClientImp(OpenConnectorTokenManager tokenManager, RestTemplate restTemplate) {
        this.tokenManager = tokenManager;
        this.restTemplate = restTemplate;
        this.mimetypesMap = new MimetypesFileTypeMap();
    }

    /**
     * Post the supplied OCRequestBody to the /v2/conversation endpoint using a valid Oauth Token.
     */
    @Override
    public String postConversationMessage(OCRequestBody requestBody, String clientId) {
        log.info("Entering Post Conversation Method");
        String token = tokenManager.getOauthAccessToken(clientId);

        String url = "https://conversations.api.globalrelay.com/v2/conversations";

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + token);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        log.info("{}", requestBody);
        HttpEntity<OCRequestBody> request = new HttpEntity<>(requestBody, headers);

        String response = restTemplate.postForObject(url, request, String.class);

        log.info("response: {}", response);

        return response;
    }

    /**
     * Put the supplied binary file data to the /v2/files endpoint using a valid Oauth Token.
     */
    @Override
    public boolean putConversationAttachment(byte[] attachmentBinary, String clientId, String fileName) {
        log.info("Entering Put File Method");
        String token = tokenManager.getOauthAccessToken(clientId);

        String url = "https://conversations.api.globalrelay.com/v2/files";
        String fileContentType = StringUtils.hasText(mimetypesMap.getContentType(fileName)) ? mimetypesMap.getContentType(fileName) : "application/octet-stream";
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        // Need to make dynamic based on file type
        headers.add("Content-Type", fileContentType);
        headers.add("Authorization", "Bearer " + token);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        String ocFileUrl = url + "/default/" + fileName;

        log.info("url with fileKey: {}", ocFileUrl);
        HttpEntity<byte[]> request = new HttpEntity<>(attachmentBinary, headers);
        ResponseEntity<Void> response = restTemplate.exchange(ocFileUrl, HttpMethod.PUT, request, Void.class);

        return response.getStatusCode().is2xxSuccessful();
    }
}
