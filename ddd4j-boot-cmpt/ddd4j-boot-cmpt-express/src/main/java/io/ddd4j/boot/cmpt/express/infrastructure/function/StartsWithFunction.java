package io.ddd4j.boot.cmpt.express.infrastructure.function;

import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.function.CustomFunction;

/**
 * 自定义函数：字符串开头判断
 * 硬编码格式的函数，适用于逻辑固定的函数规则
 * 
 * 使用示例：startsWith("hello world", "hello") -> true
 */
public class StartsWithFunction implements CustomFunction {

    @Override
    public Object call(QContext qContext, Parameters parameters) throws Throwable {
        if (parameters == null || parameters.size() < 2) {
            throw new IllegalArgumentException("startsWith函数需要2个参数：source和prefix");
        }
        
        String source = (String) parameters.get(0).getObject(qContext);
        String prefix = (String) parameters.get(1).getObject(qContext);

        boolean result = source != null && source.startsWith(prefix);
        return result;
    }
}

