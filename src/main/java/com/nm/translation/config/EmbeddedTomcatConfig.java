package com.nm.translation.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.catalina.connector.Connector;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Sets;

/**
 * Tomcat Class is used for defining the Application's Service Configuration
 * Standard Class used across many similar NM Services. Only modify if needed and/or know what you're doing.
 */
@Configuration
@Conditional(EmbeddedTomcatConfigConditional.class)
public class EmbeddedTomcatConfig {
    @Value("${server.port}")
    private String serverPort;

    @Value("${server.additionalPorts:null}")
    private String additionalPorts;

    /**
     * This bean will run Spring on two ports, SSL and non-SSL, as configured in the application-*.properties file
     */
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        Connector[] additionalConnectors = additionalConnector();
        if (additionalConnectors.length > 0) {
            tomcat.addAdditionalTomcatConnectors(additionalConnectors);
        }
        return tomcat;
    }

    private Connector[] additionalConnector() {
        List<Connector> result = new ArrayList<>();

        if (StringUtils.isBlank(additionalPorts)) {
            return result.toArray(new Connector[] {});
        }

        Set<String> defaultPorts = Sets.newHashSet(serverPort);
        String[] ports = additionalPorts.split(",");

        for (String port : ports) {
            if (!defaultPorts.contains(port)) {
                Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
                connector.setScheme("http");
                connector.setPort(Integer.parseInt(port));
                result.add(connector);
            }
        }
        return result.toArray(new Connector[] {});
    }
}

