package io.ddd4j.boot.sample.layered.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 用户 MyBatis Mapper。
 *
 * <p>空接口——继承 MP {@link BaseMapper} 即获得标准 CRUD 方法，
 * 由 {@link UserRepositoryImpl} 的四泛型体系自动调用。
 *
 * @author wandl
 */
public interface UserMapper extends BaseMapper<UserPO> {
}
