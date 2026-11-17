package io.ddd4j.boot.cola;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.exception.BizException;
import com.alibaba.cola.exception.SysException;
import io.ddd4j.boot.cola.exception.ColaExceptionHandler;
import io.ddd4j.boot.cola.handler.Ddd4jResponseHandler;
import io.ddd4j.core.api.ApiCode;
import io.ddd4j.core.api.R;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UnifiedResponseTest {
    private final Ddd4jResponseHandler handler = new Ddd4jResponseHandler();

    @Test
    void catchlogShouldReturnUnifiedFailureAndRetainColaCompatibility() {
        R<?> unified = (R<?>) handler.handle(R.class, "ORDER_REJECTED", "订单不可支付");
        assertThat(unified.getCode()).isEqualTo(ApiCode.FAIL.getCode());
        assertThat(unified.getMsg()).isEqualTo("订单不可支付");
        assertThat(unified.isOk()).isFalse();
        Response cola = (Response) handler.handle(Response.class, "ORDER_REJECTED", "订单不可支付");
        assertThat(cola.isSuccess()).isFalse();
        assertThat(cola.getErrCode()).isEqualTo("ORDER_REJECTED");
        assertThat(cola.getErrMessage()).isEqualTo("订单不可支付");
        assertThat(handler.handle(String.class, "ORDER_REJECTED", "订单不可支付")).isNull();
    }

    @Test
    void adviceShouldSeparateBusinessFailureFromSystemFailure() {
        ColaExceptionHandler advice = new ColaExceptionHandler();
        assertThat(advice.handleBizException(new BizException("ORDER_REJECTED", "订单不可支付")).getCode())
                .isEqualTo(ApiCode.FAIL.getCode());
        assertThat(advice.handleSysException(new SysException("STORAGE", "存储失败")).getCode())
                .isEqualTo(ApiCode.SERVER_ERROR.getCode());
    }
}
