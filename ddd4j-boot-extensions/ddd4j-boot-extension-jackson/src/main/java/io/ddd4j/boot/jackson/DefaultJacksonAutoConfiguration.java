package io.ddd4j.boot.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
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

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Spring Boot 2 Jackson 自动配置。
 *
 * <p>使用 Jackson 2 原生扩展点配置时间格式和六类空值序列化策略，避免引入
 * 需要 Java 17 的 Jackson 3 或外部扩展制品。
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
        return builder -> builder.simpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .failOnEmptyBeans(false)
                .failOnUnknownProperties(false)
                .featuresToEnable(MapperFeature.USE_GETTERS_AS_SETTERS,
                        MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS);
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    @Primary
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        SimpleModule module = new SimpleModule("ddd4j-jackson-module");
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        module.addSerializer(LocalDate.class, new LocalDateSerializer(
                DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        module.addSerializer(LocalTime.class, new LocalTimeSerializer(
                DateTimeFormatter.ofPattern("HH:mm:ss")));
        module.setSerializerModifier(new NullValueSerializerModifier(
                defaultNullArraySerializer, defaultNullNumberSerializer,
                defaultNullStringSerializer, defaultNullDateSerializer,
                defaultNullBooleanSerializer, defaultNullJsonObjectSerializer));
        objectMapper.registerModule(module);
        return objectMapper;
    }

    static final class NullValueSerializerModifier extends BeanSerializerModifier {

        private final boolean defaultForArray;
        private final boolean defaultForNumber;
        private final boolean defaultForString;
        private final boolean defaultForDate;
        private final boolean defaultForBoolean;
        private final boolean defaultForJsonObject;

        NullValueSerializerModifier(boolean defaultForArray, boolean defaultForNumber,
                                    boolean defaultForString, boolean defaultForDate,
                                    boolean defaultForBoolean, boolean defaultForJsonObject) {
            this.defaultForArray = defaultForArray;
            this.defaultForNumber = defaultForNumber;
            this.defaultForString = defaultForString;
            this.defaultForDate = defaultForDate;
            this.defaultForBoolean = defaultForBoolean;
            this.defaultForJsonObject = defaultForJsonObject;
        }

        @Override
        public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                         BeanDescription beanDescription,
                                                         List<BeanPropertyWriter> properties) {
            for (BeanPropertyWriter writer : properties) {
                Class<?> rawType = writer.getType().getRawClass();
                JsonSerializer<Object> serializer = nullSerializer(rawType);
                if (serializer != null) {
                    writer.assignNullSerializer(serializer);
                }
            }
            return properties;
        }

        private JsonSerializer<Object> nullSerializer(Class<?> rawType) {
            // Primitive fields never receive a null value from Jackson — skip early.
            if (rawType.isPrimitive()) {
                return null;
            }
            // Array / Collection → []
            if (rawType.isArray() || Collection.class.isAssignableFrom(rawType)) {
                return defaultForArray ? NullArraySerializer.INSTANCE : null;
            }
            // String / CharSequence / Character → ""
            if (CharSequence.class.isAssignableFrom(rawType) || Character.class == rawType) {
                return defaultForString ? NullStringSerializer.INSTANCE : null;
            }
            // Number → 0
            if (Number.class.isAssignableFrom(rawType)) {
                return defaultForNumber ? NullNumberSerializer.INSTANCE : null;
            }
            // Boolean → false
            if (Boolean.class == rawType) {
                return defaultForBoolean ? NullBooleanSerializer.INSTANCE : null;
            }
            // Date / Temporal → ""
            if (Date.class.isAssignableFrom(rawType) || Temporal.class.isAssignableFrom(rawType)) {
                return defaultForDate ? NullStringSerializer.INSTANCE : null;
            }
            // Enum → ""
            if (rawType.isEnum()) {
                return defaultForString ? NullStringSerializer.INSTANCE : null;
            }
            // Map → {}
            if (Map.class.isAssignableFrom(rawType)) {
                return defaultForJsonObject ? NullObjectSerializer.INSTANCE : null;
            }
            // Other non-primitive types (custom POJOs) → {}
            return defaultForJsonObject ? NullObjectSerializer.INSTANCE : null;
        }
    }

    private static final class NullArraySerializer extends JsonSerializer<Object> {
        private static final NullArraySerializer INSTANCE = new NullArraySerializer();
        @Override
        public void serialize(Object value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
            generator.writeStartArray();
            generator.writeEndArray();
        }
    }

    private static final class NullStringSerializer extends JsonSerializer<Object> {
        private static final NullStringSerializer INSTANCE = new NullStringSerializer();
        @Override
        public void serialize(Object value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
            generator.writeString("");
        }
    }

    private static final class NullNumberSerializer extends JsonSerializer<Object> {
        private static final NullNumberSerializer INSTANCE = new NullNumberSerializer();
        @Override
        public void serialize(Object value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
            generator.writeNumber(0);
        }
    }

    private static final class NullBooleanSerializer extends JsonSerializer<Object> {
        private static final NullBooleanSerializer INSTANCE = new NullBooleanSerializer();
        @Override
        public void serialize(Object value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
            generator.writeBoolean(false);
        }
    }

    private static final class NullObjectSerializer extends JsonSerializer<Object> {
        private static final NullObjectSerializer INSTANCE = new NullObjectSerializer();
        @Override
        public void serialize(Object value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
            generator.writeStartObject();
            generator.writeEndObject();
        }
    }
}
