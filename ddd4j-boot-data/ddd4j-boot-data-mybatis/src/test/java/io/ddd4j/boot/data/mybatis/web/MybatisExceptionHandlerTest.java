package io.ddd4j.boot.data.mybatis.web;

import io.ddd4j.core.api.R;
import org.apache.ibatis.binding.BindingException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class MybatisExceptionHandlerTest {

    @Test
    void returnsCurrentErrorEnvelopeWithServerStatus() {
        ResponseEntity<R<String>> response = new MybatisExceptionHandler()
                .mybatisBindingException(new BindingException("invalid binding"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(500);
        assertThat(response.getBody().getMsg()).isEqualTo("MyBatis:绑定异常");
    }
}
