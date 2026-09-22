package io.ddd4j.boot.jackson;

import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link DefaultJacksonAutoConfiguration.NullValueSerializerModifier} 行为测试。
 *
 * <p>直接构造 {@link JsonMapper} 并应用 modifier，验证六类 null 值序列化策略：
 * <ul>
 *   <li>array / Collection → {@code []}</li>
 *   <li>CharSequence → {@code ""}</li>
 *   <li>Number / primitive → {@code 0}</li>
 *   <li>boolean / Boolean → {@code false}</li>
 *   <li>Map / Object → {@code {}}</li>
 * </ul>
 *
 * <p>用于填补 commit 0df02765 引入的空壳 NullValueSerializerModifier 的测试覆盖。
 */
class DefaultJacksonAutoConfigurationNullTest {

    /** 测试用 POJO：包含各类属性。 */
    static final class SampleBean {
        public String name;
        public Integer count;
        public boolean active;
        public List<String> tags;
        public Map<String, Object> meta;
        public SampleBean nested;
    }

    private static JsonMapper mapperWith(boolean array, boolean number, boolean string,
                                         boolean date, boolean boolean_, boolean jsonObject) {
        SimpleModule module = new SimpleModule("test");
        module.setSerializerModifier(new DefaultJacksonAutoConfiguration.NullValueSerializerModifier(
                array, number, string, date, boolean_, jsonObject));
        return JsonMapper.builder().addModule(module).build();
    }

    @Test
    void 全开策略_null序列化为类型默认值() throws Exception {
        JsonMapper mapper = mapperWith(true, true, true, true, true, true);
        SampleBean bean = new SampleBean(); // 全部字段 null
        String json = mapper.writeValueAsString(bean);
        assertThat(json)
                .contains("\"name\":\"\"")        // CharSequence → ""
                .contains("\"count\":0")          // Number → 0
                .contains("\"active\":false")     // boolean → false
                .contains("\"tags\":[]")          // List → []
                .contains("\"meta\":{}")          // Map → {}
                .contains("\"nested\":{}");       // Object → {}
    }

    @Test
    void 字符串关闭_其余开启_null字符串序列化为null() throws Exception {
        JsonMapper mapper = mapperWith(true, true, false, true, true, true);
        SampleBean bean = new SampleBean();
        String json = mapper.writeValueAsString(bean);
        assertThat(json)
                .contains("\"name\":null")
                .contains("\"count\":0")
                .contains("\"active\":false")
                .contains("\"tags\":[]")
                .contains("\"meta\":{}");
    }

    @Test
    void 数组关闭_null数组序列化为null() throws Exception {
        JsonMapper mapper = mapperWith(false, true, true, true, true, true);
        SampleBean bean = new SampleBean();
        String json = mapper.writeValueAsString(bean);
        assertThat(json)
                .contains("\"tags\":null")
                .contains("\"name\":\"\"")
                .contains("\"count\":0");
    }

    @Test
    void 全部关闭_null字段序列化为null() throws Exception {
        JsonMapper mapper = mapperWith(false, false, false, false, false, false);
        SampleBean bean = new SampleBean();
        String json = mapper.writeValueAsString(bean);
        // 注：boolean primitive 因 JVM 默认值就是 false，Jackson 读取时拿到 false 而非 null，
        //     所以"active"字段仍是 false（其他引用类型字段都是 JSON null）。
        assertThat(json).contains("\"name\":null")
                .contains("\"count\":null")
                .contains("\"active\":false")
                .contains("\"tags\":null")
                .contains("\"meta\":null");
    }

    @Test
    void 非null字段不被modifier影响() throws Exception {
        JsonMapper mapper = mapperWith(true, true, true, true, true, true);
        SampleBean bean = new SampleBean();
        bean.name = "hello";
        bean.count = 42;
        bean.active = true;
        bean.tags = new ArrayList<>();
        bean.tags.add("a");
        bean.meta = new HashMap<>();
        bean.meta.put("k", "v");
        String json = mapper.writeValueAsString(bean);
        assertThat(json)
                .contains("\"name\":\"hello\"")
                .contains("\"count\":42")
                .contains("\"active\":true")
                .contains("\"tags\":[\"a\"]")
                .contains("\"meta\":{\"k\":\"v\"}");
    }

    enum Status { ACTIVE, INACTIVE }

    static final class ExtendedBean {
        public UUID uuid;
        public Status status;
        public LocalDateTime dateTime;
    }

    @Test
    void 枚举跟随字符串开关() throws Exception {
        // string=true, jsonObject=false → enum 序列化为 ""
        String json = mapperWith(false, false, true, false, false, false)
                .writeValueAsString(new ExtendedBean());
        assertThat(json)
                .contains("\"status\":\"\"")
                .contains("\"uuid\":null")
                .contains("\"dateTime\":null");
    }

    @Test
    void 字符串关闭时枚举不泄漏到对象回退() throws Exception {
        // string=false, jsonObject=true → enum 必须保持 null，不得泄漏为 {}
        String json = mapperWith(false, false, false, false, false, true)
                .writeValueAsString(new ExtendedBean());
        assertThat(json)
                .contains("\"status\":null")
                .contains("\"uuid\":{}");
    }
}
