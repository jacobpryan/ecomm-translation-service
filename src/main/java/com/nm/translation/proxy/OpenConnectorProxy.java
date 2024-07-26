package com.nm.translation.proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.nm.translation.client.OpenConnectorClient;
import com.nm.translation.jpa.entity.EcommArchivalDataEntity;
import com.nm.translation.jpa.entity.EcommContentEntity;
import com.nm.translation.jpa.entity.EcommEndpointConfigEntity;
import com.nm.translation.jpa.entity.EcommGrErrorsEntity;
import com.nm.translation.jpa.repository.EcommArchivalDataRepository;
import com.nm.translation.jpa.repository.EcommContentRepository;
import com.nm.translation.jpa.repository.EcommEndpointConfigRepository;
import com.nm.translation.jpa.repository.EcommGrErrorsRepository;
import com.nm.translation.model.ecomm.EcommContent;
import com.nm.translation.model.oauth.OauthTokenConfigData;
import com.nm.translation.model.oc.*;
import com.nm.translation.service.ContentDataService;
import com.nm.translation.service.TranslationService;
import com.nm.translation.utils.Constants;
import com.nm.translation.utils.MockOCRequestBodyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Main Translation Service used for routing an Ecomm Message from the SQS, Building the
 * OC Request Body, and publishing the message to OC API.
 */
@Slf4j
@Service
public class OpenConnectorProxy {
    private final OpenConnectorClient ocClient;

    @Autowired
    private MockOCRequestBodyUtil mockedRequestBodyUtil;

    @Autowired
    private EcommContentRepository ecommContentRepository;

    @Autowired
    private EcommEndpointConfigRepository endpointConfigRepository;

    @Autowired
    private EcommGrErrorsRepository grErrorsRepository;

    @Autowired
    EcommArchivalDataRepository archivalDataRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TranslationService translationService;

    @Autowired
    private ContentDataService contentDataService;

    public OpenConnectorProxy(OpenConnectorClient ocClient) {
        this.ocClient = ocClient;
    }

    private final Gson GSON = new GsonBuilder().create();


    /**
     * Objective of this Publisher Method is broadly to:
     *     * take an incoming SQS Message Body and validate it has a corresponding ID in the DB
     *     * Retrieve the DB object's JSON message data and start the OC Request Builder Logic
     *     * Determine if Message has an Attachment
     *          * Note: One method would be to FileStream S3 object data and create as file in memory for /v2/files post
     *     * Use OpenConnectorClient for posting the find RequestBody to OC API
     *          * NOTE: This will have to account for Retry Logic when implemented
     *     * Any DB updates, retry logic, etc. based on the OC API Response
     *     * Return bool (or any pass/fail method) to indicate success status to PollingService
     */
    public void processAndPublishEcommMessage(String ecommContentId, int retryCount) {
        String source = "";
        Long contentId = Long.parseLong(ecommContentId);
        try {
            log.info("Entering OpenConnectorProxy with contentId: {}", ecommContentId);
            log.info("Entering OpenConnectorProxy with contentId: {}", ecommContentId);
            EcommContentEntity ecommContentEntity = contentDataService.findContentData(contentId);
            if (ecommContentEntity != null) {
                EcommContent ecommContent = objectMapper.readValue(ecommContentEntity.getContent(), EcommContent.class);
                if (StringUtils.hasText(ecommContent.getSource())) {
                    source = ecommContent.getSource();
                    log.info("Finding Endpoint Config with source: {}", ecommContent.getSource());
                    log.info("Content Type: {}", ecommContent.getContentType());
                    EcommEndpointConfigEntity endpointConfigEntity = endpointConfigRepository.getEndpointConfigBySource(ecommContent.getSource());
                    OauthTokenConfigData ocConfigData = new OauthTokenConfigData(endpointConfigEntity);

                    log.info("Continuing Translation Service with ClientId: {}", ocConfigData.getClientId());
                    List<String> attachmentKeys = contentDataService.processAttachments(contentId, ocConfigData.getClientId());
                    if (attachmentKeys != null && !attachmentKeys.isEmpty()) {
                        ecommContent.getAttachmentKeyNames().addAll(attachmentKeys);
                    }
                    if (StringUtils.hasText(ocConfigData.getOcDataSubType())) {
                        ecommContent.setOcSubtype(ocConfigData.getOcDataSubType());
                    }
                    OCRequestBody finalRequestBody = translationService.translateEcommContent(ecommContent);
                    log.info("Send Date: " + ecommContent.getSendDate());
                    String reconciliationJson = ocClient.postConversationMessage(finalRequestBody, ocConfigData.getClientId());
                    String reconciliationId = extractJsonValue(reconciliationJson);

                    log.info("Returned ReconciliationId: {}", reconciliationId);
                    EcommArchivalDataEntity archivalDataEntity = new EcommArchivalDataEntity();
                    archivalDataEntity.setEcommContentId(contentId);
                    archivalDataEntity.setSource(source);
                    archivalDataEntity.setSentDate(convertToDate(ecommContent.getSendDate()));
                    archivalDataEntity.setGrRequestId(finalRequestBody.getRequestInfo().getRequestId());
                    archivalDataEntity.setReconId(reconciliationId);
                    archivalDataRepository.save(archivalDataEntity);
                } else {
                    throw new Exception("Error occurred when trying to retrieve json from db row.");
                }
            } else {
                throw new Exception("Error occurred when trying to retrieve row from EcommContent table");
            }
        } catch (Exception ex) {
            if (retryCount != 0) {
                log.info("Error occurred with {} retries remaining", retryCount);
                processAndPublishEcommMessage(ecommContentId, retryCount - 1);
            } else {
                log.error("Exception Occurred While Processing and Publishing Ecomm Content:", ex);
                EcommGrErrorsEntity grErrorsEntity = new EcommGrErrorsEntity();
                grErrorsEntity.setEcommConetentId(contentId);
                grErrorsEntity.setSource(source);
                grErrorsEntity.setErrorMessage(Arrays.toString(ex.getStackTrace()));
                grErrorsRepository.save(grErrorsEntity);
            }
        }
    }

    private String extractJsonValue(String json) {
        JsonObject reconIdJson = GSON.fromJson(json, JsonObject.class);
        if (reconIdJson.has(Constants.RECON_ID_NAME)) {
            String contentId = reconIdJson.getAsJsonPrimitive(Constants.RECON_ID_NAME).getAsString();
            if (StringUtils.hasText(contentId)) {
                return contentId;
            }
        }
        return json;
    }

    /**
     * Just used for posting mocked data from Controller
     */
    public String publishMockedConversationMessage() {
        String reconciliationId = ocClient.postConversationMessage(mockedRequestBodyUtil.getMockedOCRequestBody(), "");
        log.info("Mocked Message reconciliationId: {}", reconciliationId);
        return reconciliationId;
    }

    public String publishMockedConversationAttachmentMessage() {
        try {
            File file = ResourceUtils.getFile("classpath:ts-attachment-test.txt");
            byte[] attachmentBinary = Files.readAllBytes(file.toPath());
            if (ocClient.putConversationAttachment(attachmentBinary, "", file.getName())) {
                String reconciliationId = ocClient.postConversationMessage(mockedRequestBodyUtil.getMockedOCRequestBodyWithAttachment(), "");
                log.info("Mocked Message reconciliationId: {}", reconciliationId);
                return reconciliationId;
            }
        } catch (IOException ex) {
            log.error("Error occurred while loading test attachment file.");
        }
        return "error";
    }

    public boolean publishMockedAttachment() {
        try {
            File file = ResourceUtils.getFile("classpath:ts-attachment-test.txt");
            byte[] attachmentBinary = Files.readAllBytes(file.toPath());
            return ocClient.putConversationAttachment(attachmentBinary, "", file.getName());
        } catch (IOException ex) {
            log.error("Error occurred while loading test attachment file.");
        }
        return false;
    }

    /**
     * This method mocks the basic e2e data flow once the ecommContentId is retrieved
     */
    public String returnEcommContentById(Long ecommContentId) {
        log.info("Retrieving Ecomm Content Using Id");
        EcommContentEntity ecommContentEntity = ecommContentRepository.getReferenceById(ecommContentId);
        try {
            if (ecommContentEntity.getContent() != null) {
                EcommContent ecommContent = objectMapper.readValue(ecommContentEntity.getContent(), EcommContent.class);
                EcommEndpointConfigEntity endpointConfigEntity = endpointConfigRepository.getEndpointConfigBySource("BrightIdea");
                OauthTokenConfigData ocConfigData = new OauthTokenConfigData(endpointConfigEntity);
                //String reconiliationId = ocClient.postConversationMessage(translateEcommToOCRequestBody(ecommContent), ocConfigData.getClientId());
                String reconciliationId = ocClient.postConversationMessage(mockedRequestBodyUtil.getMockedOCRequestBody(), ocConfigData.getClientId());
                return reconciliationId;
            }
        } catch (Exception exception) {

        }
        return  null;
    }

    private Date convertToDate(String dateString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            Instant instant = OffsetDateTime.parse(dateString, formatter).toInstant();
            return Date.from(instant);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}