package io.ddd4j.boot.sample.exception;

import io.ddd4j.core.api.R;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.extension.context.NestedMessageSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BizExceptionCodeResponseTest {
    @Test
    void translationsShouldStayLocalToEachResponse() {
        BizExceptionCode code = BizExceptionCode.SYSTEM_ERROR;
        StaticMessageSource translations = new StaticMessageSource();
        translations.addMessage(code.getI18nKey(), Locale.SIMPLIFIED_CHINESE, "中文错误");
        translations.addMessage(code.getI18nKey(), Locale.ENGLISH, "English error");
        NestedMessageSource messages = new NestedMessageSource(translations);
        String original = code.getReason();
        try {
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            R<Object> chinese = code.asResponse(messages);
            LocaleContextHolder.setLocale(Locale.ENGLISH);
            R<Object> english = code.asResponse(messages);
            assertThat(chinese.getMsg()).isEqualTo("中文错误");
            assertThat(english.getMsg()).isEqualTo("English error");
            assertThat(chinese.getCode()).isEqualTo(code.getCode());
            assertThat(code.getReason()).isEqualTo(original);
            assertThat(code.toResponse().getMsg()).isEqualTo(original);
        } finally {
            LocaleContextHolder.resetLocaleContext();
        }
    }

    @Test
    void numericResponseCodeAndMessageShouldSurviveExceptionConversion() {
        assertThatThrownBy(() -> BizExceptionCode.throwByResponse(new R<>(9998, "远程失败")))
                .isInstanceOf(io.ddd4j.core.exception.BizRuntimeException.class)
                .hasMessage("远程失败")
                .hasFieldOrPropertyWithValue("code", 9998);
    }
}
