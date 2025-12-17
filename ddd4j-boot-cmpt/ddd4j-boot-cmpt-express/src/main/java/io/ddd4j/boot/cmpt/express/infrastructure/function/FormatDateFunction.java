package io.ddd4j.boot.cmpt.express.infrastructure.function;

import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.function.CustomFunction;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 自定义函数：日期格式化
 * 硬编码格式的函数，适用于逻辑固定的函数规则
 * 
 * 使用示例：formatDate(date, "yyyy-MM-dd HH:mm:ss")
 * 支持 Date 和 LocalDateTime 类型
 */
public class FormatDateFunction implements CustomFunction {

    @Override
    public Object call(QContext qContext, Parameters parameters) throws Throwable {
        if (parameters == null || parameters.size() < 2) {
            throw new IllegalArgumentException("formatDate函数需要2个参数：date和pattern");
        }
        
        Object dateObj = parameters.get(0).getObject(qContext);
        String pattern = (String) parameters.get(1).getObject(qContext);

        if (dateObj == null) {
            return null;
        }

        if (dateObj instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format((Date) dateObj);
        } else if (dateObj instanceof LocalDateTime) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return ((LocalDateTime) dateObj).format(formatter);
        } else {
            throw new IllegalArgumentException("formatDate函数第一个参数必须是Date或LocalDateTime类型");
        }
    }
}