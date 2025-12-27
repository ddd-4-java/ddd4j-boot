/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.ddd4j.boot.core.service;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.ddd4j.boot.core.entity.PaginationEntity;

import java.io.Serializable;

/**
 * 通用Service接口
 *
 * @param <T> 持有的实体对象
 * @author <a href="https://github.com/wandl">wandl</a>
 */
public interface IBaseService<T extends Model<?>> extends IService<T> {

    /**
     * 更新数据状态
     *
     * @param id     数据ID
     * @param status 数据状态
     * @return 更新结果
     */
    boolean setStatus(Serializable id, Serializable status);

    /**
     * 分页查询
     *
     * @param entity 分页查询参数
     * @return 分页查询结果
     */
    Page<T> getPagedList(PaginationEntity<T> entity);

    /**
     * 分页查询
     *
     * @param page   分页查询参数
     * @param entity 分页查询参数
     * @return 分页查询结果
     */
    Page<T> getPagedList(Page<T> page, PaginationEntity<T> entity);

    /**
     * 根据唯一ID编码获取记录数
     *
     * @param uid 唯一ID编码
     * @return 统计记录数
     */
    Long getCountByUid(Serializable uid);

    /**
     * 根据编码获取记录数
     *
     * @param code   编码
     * @param origin 来源
     * @return 统计记录数
     */
    Long getCountByCode(String code, Object origin);

    /**
     * 根据名称获取记录数
     *
     * @param name   名称
     * @param origin 来源
     * @return 统计记录数
     */
    Long getCountByName(String name, Object origin);

    /**
     * 根据父级ID获取记录数
     *
     * @param parent 父级ID
     * @return 统计记录数
     */
    Long getCountByParent(Object parent);

    /**
     *
     * 通过指定key查询对应的唯一值
     *
     * @param key 键
     * @return 值
     */
    String getValue(String key);

}
