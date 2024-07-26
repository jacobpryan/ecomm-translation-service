package com.nm.translation.config;

import com.nm.translation.jpa.entity.EcommEndpointConfigEntity;
import com.nm.translation.model.oauth.OauthTokenConfigData;
import com.nm.translation.jpa.repository.EcommEndpointConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class OpenConnectorLoginConfig {
    @Autowired
    private EcommEndpointConfigRepository endpointConfigRepository;

    private List<OauthTokenConfigData> oauthTokenConfigList = new ArrayList<>();

    @Autowired
    public OpenConnectorLoginConfig(EcommEndpointConfigRepository endpointConfigRepository) {
        this.endpointConfigRepository = endpointConfigRepository;
        List<EcommEndpointConfigEntity> endpointConfigEntities = endpointConfigRepository.findAll();
        if (!endpointConfigEntities.isEmpty()) {
            for (EcommEndpointConfigEntity configEntity: endpointConfigEntities) {
                try {
                    OauthTokenConfigData tempOcConfigData = new OauthTokenConfigData(configEntity);
                    oauthTokenConfigList.add(tempOcConfigData);
                } catch (Exception ex) {
                    log.error("Exception occurred while loading OauthTokenConfigData: {}", ex);
                }
            }
        } else {
            log.error("EcommEndpointConfigEntities not loaded from DB");
        }
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Scope("prototype")
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService clientService)
    {
        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .refreshToken()
                        .clientCredentials()
                        .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, clientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = new ArrayList<>();
        for (OauthTokenConfigData oauthClientData : oauthTokenConfigList) {
            ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(oauthClientData.getClientId())
                    .tokenUri(oauthClientData.getTokenUri())
                    .clientId(oauthClientData.getClientId())
                    .clientSecret(oauthClientData.getClientSecret())
                    .scope("openid", "write", "conversation")
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .build();
            registrations.add(clientRegistration);
        }
        log.info("Returning ClientCredentials with object count: {}", registrations.size());
        return new InMemoryClientRegistrationRepository(registrations);
    }
}
