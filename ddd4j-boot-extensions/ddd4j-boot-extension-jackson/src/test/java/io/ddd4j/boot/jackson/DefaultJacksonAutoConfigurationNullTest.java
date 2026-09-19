package io.ddd4j.boot.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Jackson 2 空值序列化策略测试。
 */
class DefaultJacksonAutoConfigurationNullTest {

    enum Status { ACTIVE, INACTIVE }

    static final class SampleBean {
        public String name;
        public Integer count;
        public Boolean active;
        public List<String> tags;
        public Map<String, Object> meta;
        public SampleBean nested;
    }

    static final class ExtendedBean {
        public UUID uuid;
        public Status status;
        public LocalDateTime dateTime;
    }

    private static ObjectMapper mapperWith(boolean array, boolean number, boolean string,
                                           boolean date, boolean booleanValue, boolean jsonObject) {
        SimpleModule module = new SimpleModule("test");
        module.setSerializerModifier(new DefaultJacksonAutoConfiguration.NullValueSerializerModifier(
                array, number, string, date, booleanValue, jsonObject));
        return new ObjectMapper().registerModule(module);
    }

    @Test
    void enabledStrategiesShouldWriteTypeDefaults() throws Exception {
        String json = mapperWith(true, true, true, true, true, true)
                .writeValueAsString(new SampleBean());

        assertThat(json)
                .contains("\"name\":\"\"")
                .contains("\"count\":0")
                .contains("\"active\":false")
                .contains("\"tags\":[]")
                .contains("\"meta\":{}")
                .contains("\"nested\":{}");
    }

    @Test
    void disabledStrategiesShouldPreserveJsonNull() throws Exception {
        String json = mapperWith(false, false, false, false, false, false)
                .writeValueAsString(new SampleBean());

        assertThat(json)
                .contains("\"name\":null")
                .contains("\"count\":null")
                .contains("\"active\":null")
                .contains("\"tags\":null")
                .contains("\"meta\":null")
                .contains("\"nested\":null");
    }

    @Test
    void specificTypeFlagDisabledShouldNotLeakIntoObjectFallback() throws Exception {
        // array=false, jsonObject=true → arrays must stay null, NOT become {}
        String json = mapperWith(false, false, false, false, false, true)
                .writeValueAsString(new SampleBean());

        assertThat(json)
                .contains("\"tags\":null")       // array flag off → null
                .contains("\"meta\":{}")         // Map → {}
                .contains("\"nested\":{}")       // POJO → {}
                .contains("\"name\":null")       // string flag off → null
                .contains("\"count\":null")      // number flag off → null
                .contains("\"active\":null");    // boolean flag off → null
    }

    @Test
    void enumFollowsStringFlag() throws Exception {
        // string=true, jsonObject=false → enum → "" (via enum→string rule)
        String json = mapperWith(false, false, true, false, false, false)
                .writeValueAsString(new ExtendedBean());

        assertThat(json)
                .contains("\"status\":\"\"")     // enum → string rule
                .contains("\"uuid\":null")       // UUID is not CharSequence/Enum, jsonObject=false → null
                .contains("\"dateTime\":null");  // date flag off → null
    }

    @Test
    void enumReturnsNullWhenStringDisabled() throws Exception {
        // string=false → enum dedicated handler returns null (does NOT leak into object fallback)
        String json = mapperWith(false, false, false, false, false, true)
                .writeValueAsString(new ExtendedBean());

        assertThat(json)
                .contains("\"status\":null")     // enum handler: string flag off → null
                .contains("\"uuid\":{}");        // UUID is uncategorized, jsonObject=true → {}
    }
}
