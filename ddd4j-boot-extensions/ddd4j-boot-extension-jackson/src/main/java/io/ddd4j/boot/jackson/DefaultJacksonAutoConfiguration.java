package io.ddd4j.boot.jackson;

import cn.hutool.core.date.DatePattern;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer;
import hitool.core.lang3.time.DateFormats;
import io.github.easy4j.jackson.JavaTimeModule;
import io.github.easy4j.jackson.ser.MyBeanSerializerModifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Spring Boot Jackson AutoConfiguration。
 * <p>
 * 依赖库侧 {@code io.github.easy4j:jackson-extension} 提供的权威实现
 * （{@link JavaTimeModule}、{@link MyBeanSerializerModifier} 等），本类仅负责
 * Spring Boot 自动装配：向 {@link Jackson2ObjectMapperBuilder} 注入默认配置，
 * 并注册 {@code @Primary} 的 {@link ObjectMapper} Bean。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ObjectMapper.class, Jackson2ObjectMapperBuilder.class})
@AutoConfigureBefore(JacksonAutoConfiguration.class)
public class DefaultJacksonAutoConfiguration {

    @Value("${spring.jackson.default-null-array-serializer:true}")
    private boolean defaultNullArraySerializer;
    @Value("${spring.jackson.default-null-number-serializer:false}")
    private boolean defaultNullNumberSerializer;
    @Value("${spring.jackson.default-null-string-serializer:true}")
    private boolean defaultNullStringSerializer;
    @Value("${spring.jackson.default-null-date-serializer:true}")
    private boolean defaultNullDateSerializer;
    @Value("${spring.jackson.default-null-boolean-serializer:false}")
    private boolean defaultNullBooleanSerializer;
    @Value("${spring.jackson.default-null-json-object-serializer:true}")
    private boolean defaultNullJsonObjectSerializer;

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer defaultJacksonObjectMapperBuilderCustomizer() {
        return builder -> {
            builder.simpleDateFormat(DateFormats.DATE_LONGFORMAT)
                    .failOnEmptyBeans(false)
                    .failOnUnknownProperties(false)
                    .featuresToEnable(MapperFeature.USE_GETTERS_AS_SETTERS, MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS)
                    .modules(new JavaTimeModule());
        };
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    @Primary
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN))); // "yyyy-MM-dd"
        module.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN))); // "HH:mm:ss"
        objectMapper.registerModule(module);
        MyBeanSerializerModifier myBeanSerializerModifier = new MyBeanSerializerModifier(defaultNullArraySerializer,
                defaultNullNumberSerializer, defaultNullStringSerializer,
                defaultNullDateSerializer, defaultNullBooleanSerializer, defaultNullJsonObjectSerializer);
        objectMapper.setSerializerFactory(objectMapper.getSerializerFactory().withSerializerModifier(myBeanSerializerModifier));
        return objectMapper;
    }

}
