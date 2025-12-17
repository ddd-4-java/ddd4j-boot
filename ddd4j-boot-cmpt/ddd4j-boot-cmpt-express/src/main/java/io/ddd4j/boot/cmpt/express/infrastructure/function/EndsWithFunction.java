package io.ddd4j.boot.cmpt.express.infrastructure.function;

import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.function.CustomFunction;

/**
 * 自定义函数：字符串结尾判断
 * 硬编码格式的函数，适用于逻辑固定的函数规则
 * 
 * 使用示例：endsWith("hello world", "world") -> true
 */
public class EndsWithFunction implements CustomFunction {

    @Override
    public Object call(QContext qContext, Parameters parameters) throws Throwable {
        if (parameters == null || parameters.size() < 2) {
            throw new IllegalArgumentException("endsWith函数需要2个参数：source和suffix");
        }
        
        String source = (String) parameters.get(0).getObject(qContext);
        String suffix = (String) parameters.get(1).getObject(qContext);

        boolean result = source != null && source.endsWith(suffix);
        return result;
    }
}

