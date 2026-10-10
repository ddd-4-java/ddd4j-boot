package io.ddd4j.boot.sample.layered.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 用户持久化对象（PO）。
 *
 * <p>与领域模型 {@link io.ddd4j.boot.sample.layered.domain.model.User} 分离，
 * 带 MyBatis Plus 注解（{@code @TableName/@TableId/@TableLogic/@TableField}）。
 * {@code BaseRepositoryImpl} 通过 BeanKit 双向拷贝自动完成 Model↔PO 转换。
 *
 * @author wandl
 */
@TableName("sample_user")
public class UserPO {

/**
 * 构造UserPO对象（默认无参构造，字段由调用方逐个设置）。
 */
public UserPO() {
}

    /** 用户ID */
    @TableId
    private String id;

    /** 手机号 */
    @TableField("phone")
    private String phone;

    /** 昵称 */
    private String nickname;

    /** 状态 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 删除标记 */
    @TableLogic
    private Integer delFlag;

    /** 获取用户ID。
     * @return 用户ID */
    public String getId() {
        return id;
    }

    /** 设置用户ID。
     * @param id 用户ID */
    public void setId(String id) {
        this.id = id;
    }

    /** 获取手机号。
     * @return 手机号 */
    public String getPhone() {
        return phone;
    }

    /** 设置手机号。
     * @param phone 手机号 */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /** 获取昵称。
     * @return 昵称 */
    public String getNickname() {
        return nickname;
    }

    /** 设置昵称。
     * @param nickname 昵称 */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /** 获取状态。
     * @return 状态 */
    public Integer getStatus() {
        return status;
    }

    /** 设置状态。
     * @param status 状态 */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /** 获取创建时间。
     * @return 创建时间 */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /** 设置创建时间。
     * @param createTime 创建时间 */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /** 获取更新时间。
     * @return 更新时间 */
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    /** 设置更新时间。
     * @param updateTime 更新时间 */
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    /** 获取删除标记。
     * @return 删除标记 */
    public Integer getDelFlag() {
        return delFlag;
    }

    /** 设置删除标记。
     * @param delFlag 删除标记 */
    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
}