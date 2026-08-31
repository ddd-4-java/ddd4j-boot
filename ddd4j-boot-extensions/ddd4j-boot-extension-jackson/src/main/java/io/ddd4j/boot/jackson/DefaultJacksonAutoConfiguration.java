package io.ddd4j.boot.jackson;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.ser.ValueSerializerModifier;
import tools.jackson.databind.SerializationContext;
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
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
     * null 值默认序列化策略：按属性类型将 {@code null} 序列化为类型默认值而非省略字段。
     *
     * <p>对齐 {@code easy4j:jackson-extension} 的 {@code MyBeanSerializerModifier} 语义。
     *
     * <p>六类开关（{@code spring.jackson.default-null-*-serializer}）：
     * <ul>
     *   <li>array —— 数组/集合 → {@code []}</li>
     *   <li>number —— 数值 → {@code 0}</li>
     *   <li>string —— 字符串 → {@code ""}</li>
     *   <li>date —— 日期/时间 → {@code ""}</li>
     *   <li>boolean —— 布尔 → {@code false}</li>
     *   <li>json-object —— 对象/Map → {@code {}}</li>
     * </ul>
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
        public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                         BeanDescription.Supplier beanDesc,
                                                         List<BeanPropertyWriter> beanProperties) {
            List<BeanPropertyWriter> modified = new java.util.ArrayList<>(beanProperties.size());
            for (BeanPropertyWriter writer : beanProperties) {
                modified.add(wrapIfNeeded(writer));
            }
            return modified;
        }

        @Override
        public ValueSerializer<?> modifySerializer(SerializationConfig config,
                                                   BeanDescription.Supplier beanDesc,
                                                   ValueSerializer<?> serializer) {
            return serializer;
        }

        private BeanPropertyWriter wrapIfNeeded(BeanPropertyWriter writer) {
            if (writer == null) {
                return null;
            }
            JavaType type = writer.getType();
            if (type == null) {
                return writer;
            }
            Class<?> rawType = type.getRawClass();
            if (rawType == null) {
                return writer;
            }
            if (rawType.isArray() || Collection.class.isAssignableFrom(rawType)) {
                return defaultForArray ? new NullValueBeanPropertyWriter(writer, "[]") : writer;
            }
            if (CharSequence.class.isAssignableFrom(rawType) || Character.class == rawType) {
                return defaultForString ? new NullValueBeanPropertyWriter(writer, "\"\"") : writer;
            }
            if (Number.class.isAssignableFrom(rawType)
                    || (rawType.isPrimitive() && rawType != boolean.class && rawType != void.class)) {
                return defaultForNumber ? new NullValueBeanPropertyWriter(writer, "0") : writer;
            }
            if (Date.class.isAssignableFrom(rawType) || Temporal.class.isAssignableFrom(rawType)) {
                return defaultForDate ? new NullValueBeanPropertyWriter(writer, "\"\"") : writer;
            }
            if (rawType == boolean.class || Boolean.class == rawType) {
                return defaultForBoolean ? new NullValueBeanPropertyWriter(writer, "false") : writer;
            }
            if (Map.class.isAssignableFrom(rawType) || Object.class == rawType) {
                return defaultForJsonObject ? new NullValueBeanPropertyWriter(writer, "{}") : writer;
            }
            return defaultForJsonObject ? new NullValueBeanPropertyWriter(writer, "{}") : writer;
        }
    }

    /**
     * {@code null} 属性写为固定 JSON 字面量（如 {@code []} / {@code ""} / {@code 0} / {@code {}}）的
     * {@link BeanPropertyWriter} 装饰器。
     */
    static final class NullValueBeanPropertyWriter extends BeanPropertyWriter {

        private static final long serialVersionUID = 1L;

        private final BeanPropertyWriter delegate;
        private final String nullLiteral;

        NullValueBeanPropertyWriter(BeanPropertyWriter delegate, String nullLiteral) {
            super(delegate);
            this.delegate = delegate;
            this.nullLiteral = nullLiteral;
        }

        @Override
        public void serializeAsProperty(Object bean, JsonGenerator gen, SerializationContext ctxt)
                throws Exception {
            Object value;
            try {
                value = get(bean);
            } catch (Exception e) {
                // JacksonException 的双参数构造器是 protected；此处包成 RuntimeException
                // 让 Jackson 框架继续以统一的异常类型处理。cause 保留原始失败原因。
                throw new RuntimeException("Failed to read property '" + getName() + "'", e);
            }
            if (value == null) {
                gen.writeName(getName());
                gen.writeRawValue(nullLiteral);
                return;
            }
            delegate.serializeAsProperty(bean, gen, ctxt);
        }
    }
}
