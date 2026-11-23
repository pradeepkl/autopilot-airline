package com.autopilot.airline.config;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.stereotype.Component;

import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

/**
 * Level 2: an extension point. JacksonAutoConfiguration applies every
 * JsonMapperBuilderCustomizer to the builder before it creates the JsonMapper.
 * Partner systems expect dates as 22-Nov-2026, not ISO-8601.
 */
@Component
class DepartureDateFormatCustomizer implements JsonMapperBuilderCustomizer {

    private static final DateTimeFormatter PARTNER_FORMAT =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);

    @Override
    public void customize(JsonMapper.Builder builder) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDate.class, new LocalDateSerializer(PARTNER_FORMAT));
        builder.addModule(module);
    }
}
