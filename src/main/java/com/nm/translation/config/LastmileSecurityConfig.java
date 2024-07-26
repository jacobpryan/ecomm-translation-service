package com.nm.translation.config;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Common Configuration Class which configures the Application validate the lastmile security cert.
 * Standard Class used across many similar NM Services. Only modify if needed and/or know what you're doing.
 */
@SuppressWarnings("deprecation")
@Slf4j
@Configuration
@EnableWebSecurity
public class LastmileSecurityConfig {
    @Value("${unprotectedEndpoints}")
    private String unprotectedEndpoints;

    /**
     * Create a WebSecurityConfigurerAdapter that will enable last-mile security.
     *
     * @return WebSecurityConfigurerAdapter that enables last-mile security
     */
    @Bean
    @ConditionalOnExpression("${lastmileSecurity:true}")
    public WebSecurityConfigurerAdapter securedConfig(UserDetailsService serverUserDetailService) {
        return new WebSecurityConfigurerAdapter() {
            @Override
            protected void configure(HttpSecurity http) throws Exception {
                log.info("Configuring lastmile security");
                http.authorizeRequests().requestMatchers(protectedEndpoints()).authenticated().and().x509().subjectPrincipalRegex("CN=(.*?)(?:,|$)").userDetailsService(serverUserDetailService).and()
                        .csrf().disable();
            }
        };
    }

    /**
     * Create a WebSecurityConfigurerAdapter that will allow all requests (no security). **NOTE** This should only be enabled for local testing.
     *
     * @return WebSecurityConfigurerAdapter that does not enable last-mile security
     */
    @Bean
    @ConditionalOnExpression("!${lastmileSecurity:true}")
    public WebSecurityConfigurerAdapter unsecuredConfig() {
        return new WebSecurityConfigurerAdapter() {
            @Override
            protected void configure(HttpSecurity http) throws Exception {
                log.info("Configuring no security");
                http.authorizeRequests().anyRequest().permitAll().and().csrf().disable();
            }
        };
    }

    /**
     * generates a bean to handle determining user details based on a provided username.
     *
     * @return a bean for loading user details
     */
    @Bean
    public UserDetailsService serverUserDetailService() {
        log.info("Configuring user detail service");
        return (String username) -> new User(username, "", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_SERVER"));
    }

    private RequestMatcher protectedEndpoints() {
        return (HttpServletRequest request) -> {
            Pattern unprotected = Pattern.compile("/(" + unprotectedEndpoints + ")$");
            String uri = request.getRequestURI();

            return !unprotected.matcher(uri).find();
        };
    }

}
