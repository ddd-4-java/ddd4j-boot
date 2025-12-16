package io.ddd4j.boot.cmpt.express.infrastructure.function;

import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.OperateDataCacheManager;
import com.ql.util.express.instruction.op.OperatorBase;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自定义函数：日期格式化
 */
public class FormatDateFunction extends OperatorBase {
    
    public FormatDateFunction(String name) {
        this.name = name;
    }
    
    @Override
    public OperateData executeInner(InstructionSetContext context, OperateData[] list) throws Exception {
        Date date = (Date) list[0].getObject(context);
        String pattern = (String) list[1].getObject(context);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String result = sdf.format(date);
        return OperateDataCacheManager.fetchOperateData(result, String.class);
    }
}

