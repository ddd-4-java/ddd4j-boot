package io.ddd4j.boot.cmpt.express.infrastructure.function;


import com.alibaba.qlexpress4.annotation.QLAlias;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.function.CustomFunction;

@QLAlias("hello")
public class HelloFunction implements CustomFunction {
    @Override
    public Object call(QContext qContext, Parameters parameters)
            throws Throwable {
        String tenant = (String)qContext.attachment().get("tenant");
        return "hello," + tenant;
    }
}