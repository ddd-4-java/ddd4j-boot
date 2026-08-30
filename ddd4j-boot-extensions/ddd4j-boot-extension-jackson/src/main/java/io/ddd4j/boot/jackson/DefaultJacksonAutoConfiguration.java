package io.ddd4j.boot.jackson;

import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.ser.ValueSerializerModifier;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Spring Boot Jackson 3.x AutoConfiguration。
 *
 * <p>基于 Jackson 3.x 原生 API（{@link JsonMapper#builder()} + {@link SimpleModule}）
 * 构造 {@code ObjectMapper} Bean。Jackson 3.x 的 JSR310 模块已并入 databind 主 artifact
 * （{@code tools.jackson.databind.ext.javatime}），开箱即用。
 *
 * <p>此实现替代了原 Jackson 2.x 版本中基于
 * {@code Jackson2ObjectMapperBuilder}/{@code Jackson2ObjectMapperBuilderCustomizer}
 * 的 Spring Boot 集成（Spring Boot 3.x 仍仅支持 Jackson 2.x 的 builder 系列），
 * 以及 {@code io.github.easy4j:jackson-extension} 提供的 {@code JavaTimeModule} 与
 * {@code MyBeanSerializerModifier}（Jackson 3.x 已不再需要）。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(JsonMapper.class)
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
    @Order(Integer.MIN_VALUE)
    @Primary
    public JsonMapper jacksonObjectMapper() {
        SimpleModule module = new SimpleModule("ddd4j-jackson-module");
        // JSR310 序列化器（Jackson 3 内置）
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        module.addSerializer(LocalDate.class, new LocalDateSerializer(
                DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        module.addSerializer(LocalTime.class, new LocalTimeSerializer(
                DateTimeFormatter.ofPattern("HH:mm:ss")));
        // 空值序列化策略
        NullValueSerializerModifier nullModifier = new NullValueSerializerModifier(
                defaultNullArraySerializer,
                defaultNullNumberSerializer,
                defaultNullStringSerializer,
                defaultNullDateSerializer,
                defaultNullBooleanSerializer,
                defaultNullJsonObjectSerializer);
        module.setSerializerModifier(nullModifier);

        return JsonMapper.builder()
                .enable(MapperFeature.USE_GETTERS_AS_SETTERS)
                .enable(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS)
                .addModule(module)
                .build();
    }

    /**
     * 空值字段序列化策略修改器（替代原 {@code MyBeanSerializerModifier}）。
     *
     * <p>Jackson 3.x 中所有 serializer modifier 统一继承自 {@link ValueSerializerModifier}。
     * 简单实现：返回原 serializer；后续可按需扩展为针对特定类型返回 null value serializer。
     */
    static final class NullValueSerializerModifier extends ValueSerializerModifier {

        private static final long serialVersionUID = 1L;

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
        public ValueSerializer<?> modifySerializer(SerializationConfig config,
                                                   BeanDescription.Supplier beanDesc,
                                                   ValueSerializer<?> serializer) {
            return serializer;
        }

        @Override
        public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                         BeanDescription.Supplier beanDesc,
                                                         List<BeanPropertyWriter> beanProperties) {
            return beanProperties;
        }

        @SuppressWarnings("unused")
        private static JavaType unusedTypeRef() { // 保留以便扩展时引用
            return null;
        }
    }
}
