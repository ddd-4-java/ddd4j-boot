package io.ddd4j.boot.cmpt.express.infrastructure.function;

import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.OperateDataCacheManager;
import com.ql.util.express.instruction.op.OperatorBase;

/**
 * 自定义函数：字符串开头判断
 */
public class StartsWithFunction extends OperatorBase {
    
    public StartsWithFunction(String name) {
        this.name = name;
    }
    
    @Override
    public OperateData executeInner(InstructionSetContext context, OperateData[] list) throws Exception {
        String source = (String) list[0].getObject(context);
        String target = (String) list[1].getObject(context);
        boolean result = source != null && source.startsWith(target);
        return OperateDataCacheManager.fetchOperateData(result, Boolean.class);
    }
}

