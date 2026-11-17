package io.ddd4j.extension.dubbo;

import io.ddd4j.core.api.R;
import io.ddd4j.extension.dubbo.exception.DubboExceptionHandler;
import org.apache.dubbo.rpc.RpcException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UnifiedRpcResponseTest {
    @Test
    void rpcFailuresShouldRetainTheirClassificationAndHideMultilineDetails() {
        int[][] cases = {
                {RpcException.TIMEOUT_EXCEPTION, 504}, {RpcException.LIMIT_EXCEEDED_EXCEPTION, 504},
                {RpcException.TIMEOUT_TERMINATE, 504}, {RpcException.NETWORK_EXCEPTION, 503},
                {RpcException.FORBIDDEN_EXCEPTION, 503}, {RpcException.METHOD_NOT_FOUND, 404},
                {RpcException.NO_INVOKER_AVAILABLE_AFTER_FILTER, 404},
                {RpcException.SERIALIZATION_EXCEPTION, 400}, {RpcException.VALIDATION_EXCEPTION, 400},
                {RpcException.BIZ_EXCEPTION, 500}, {RpcException.UNKNOWN_EXCEPTION, 500}
        };
        DubboExceptionHandler handler = new DubboExceptionHandler();
        for (int[] entry : cases) {
            R<String> result = handler.handleRpcException(new RpcException(entry[0], "调用失败\n内部详情"));
            assertThat(result.getCode()).isEqualTo(entry[1]);
            assertThat(result.getMsg()).isEqualTo("Dubbo: 调用失败");
            assertThat(result.isOk()).isFalse();
        }
    }
}
