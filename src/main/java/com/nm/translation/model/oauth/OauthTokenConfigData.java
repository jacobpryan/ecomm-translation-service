package com.nm.translation.model.oauth;

import com.google.gson.JsonArray;
import com.nm.translation.jpa.entity.EcommEndpointConfigEntity;
import com.nm.translation.jpa.entity.EcommTokenManagementEntity;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Base64;

@Data
public class OauthTokenConfigData {
    private String clientId;
    private String clientSecret;
    private String ecommSourceMatch;
    private String ecommContentTypeMatch;
    private JsonArray ocRequestBodyOverride;
    private Integer rateLimitThreshold;
    private Integer endpointVersion;
    private String endpointPath;
    private String ocDataType;
    private String ocDataSubType;
    private String clientRegistrationName;
    private String grantType;
    private String tokenUri;
    private String serverUrl;

    public OauthTokenConfigData() {}

    public OauthTokenConfigData(EcommEndpointConfigEntity endpointConfigEntity) throws Exception {
        if (endpointConfigEntity != null && endpointConfigEntity.getTokenIdEntity() != null) {
            EcommTokenManagementEntity tokenManagementEntity = endpointConfigEntity.getTokenIdEntity();

            // Set EndpointConfigEntity sourced data
            decryptClientCredentialsHelper(endpointConfigEntity.getClientCredPair());
            this.ecommSourceMatch = endpointConfigEntity.getEcommSourceMatch();
            this.ecommContentTypeMatch = endpointConfigEntity.getEcommContentTypeMatch();
            this.ocDataType = endpointConfigEntity.getDataType();
            this.ocDataSubType = endpointConfigEntity.getDataSubType();
            // This JSON mapping will be added in a future enhancement
            // this.ocRequestBodyOverride = endpointConfigEntity.getOcRequestBodyFieldOverride();

            // Set TokenManagementEntity sourced data
            if (tokenManagementEntity.getRateLimitThreshold() != null && endpointConfigEntity.getRateLimitMultiplier() != null) {
                calculateRateLimitHelper(tokenManagementEntity.getRateLimitThreshold(), Integer.valueOf(endpointConfigEntity.getRateLimitMultiplier()));

            } else {
                this.rateLimitThreshold = 100;
            }
            this.endpointVersion = Integer.valueOf(tokenManagementEntity.getEndpointVersion());
            this.endpointPath = tokenManagementEntity.getEndpointPath();
            this.clientRegistrationName = tokenManagementEntity.getClientRegName();
            this.grantType = tokenManagementEntity.getGrantType();
            this.tokenUri = tokenManagementEntity.getTokenUri();
            this.serverUrl = tokenManagementEntity.getServerUrl();
        } else {
            throw new Exception("Unable to find corresponding endpoint configuration");
        }
    }

    private void decryptClientCredentialsHelper(String encClientCredPair) throws Exception {
        if (StringUtils.hasText(encClientCredPair)) {
            String tempDecryptedClientPair = new String(Base64.getDecoder().decode(encClientCredPair));
            if (StringUtils.hasText(tempDecryptedClientPair) && tempDecryptedClientPair.contains(":")
                        && tempDecryptedClientPair.split(":").length == 2) {
                String tempClientId = tempDecryptedClientPair.split(":")[0];
                String tempClientSecret = tempDecryptedClientPair.split(":")[1];
                if (StringUtils.hasText(tempClientId) && StringUtils.hasText(tempClientSecret)) {
                    this.clientId = tempClientId;
                    this.clientSecret = tempClientSecret;
                } else {
                    throw new Exception("");
                }
            } else {
                throw new Exception("");
            }
        } else {
            throw new Exception("");
        }
    }

    private void calculateRateLimitHelper(Integer baseRateLimit, Integer rateLimitMultiplier) {
        this.rateLimitThreshold = baseRateLimit * rateLimitMultiplier;
    }
}
