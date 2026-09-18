package io.ddd4j.boot.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Jackson 2 空值序列化策略测试。
 */
class DefaultJacksonAutoConfigurationNullTest {

    static final class SampleBean {
        public String name;
        public Integer count;
        public Boolean active;
        public List<String> tags;
        public Map<String, Object> meta;
        public SampleBean nested;
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
}
