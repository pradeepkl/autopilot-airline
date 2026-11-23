package com.autopilot.airline.config;

import java.util.Map;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

/**
 * Level 2: an extension point. HibernateJpaAutoConfiguration applies every
 * HibernatePropertiesCustomizer before it builds the EntityManagerFactory.
 * Every SQL statement carries a comment, so a DBA can trace a slow query
 * back to the booking service.
 */
@Component
class SqlTaggingCustomizer implements HibernatePropertiesCustomizer {

    private static final StatementInspector TAG_WITH_SERVICE_NAME =
            sql -> "/* booking-service */ " + sql;

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.STATEMENT_INSPECTOR, TAG_WITH_SERVICE_NAME);
    }
}
