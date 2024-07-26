package com.nm.translation.config;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Tomcat Helper Class for determining Spring Profiles
 * Standard Class used across many similar NM Services. Only modify if needed and/or know what you're doing.
 */
public class EmbeddedTomcatConfigConditional implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        String[] profiles = context.getEnvironment().getActiveProfiles();

        //Do not activate configuration if no spring profile is specified.
        if (ArrayUtils.isEmpty(profiles)) {
            return false;
        }

        /*Do not activate configuration if any of the following conditions are met:
         *  1. active spring profile name contains "test"
         *  2. the default profile is active
         */
        for (String profile : profiles) {
            if (profile.toLowerCase().contains("test") || StringUtils.equalsIgnoreCase(profile, "default")) {
                return false;
            }
        }
        return true;
    }
}
