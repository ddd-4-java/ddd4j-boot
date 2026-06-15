package io.ddd4j.boot.sample.layered.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import io.ddd4j.boot.data.annotation.BizKey;
import io.ddd4j.boot.data.annotation.OnCreate;
import io.ddd4j.boot.data.annotation.OnUpdate;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户持久化对象（PO）。
 *
 * <p>与领域模型 {@link io.ddd4j.boot.sample.layered.domain.model.User} 分离，
 * 带 MyBatis Plus 注解（{@code @TableName/@TableId/@TableLogic}）。
 * {@code BaseRepositoryImpl} 通过 BeanKit 双向拷贝自动完成 Model↔PO 转换。
 *
 * @author wandl
 */
@Data
@TableName("sample_user")
public class UserPO {

    @TableId
    private String id;

    @BizKey
    private String phone;

    private String nickname;

    private Integer status;

    @OnCreate
    private LocalDateTime createTime;

    @OnUpdate
    private LocalDateTime updateTime;

    @TableLogic
    private Integer delFlag;
}
