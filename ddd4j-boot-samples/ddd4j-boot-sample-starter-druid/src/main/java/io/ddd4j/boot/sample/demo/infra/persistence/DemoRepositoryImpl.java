package io.ddd4j.boot.sample.demo.infra.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.ddd4j.boot.sample.demo.domain.model.entity.DemoEntity;
import io.ddd4j.boot.sample.demo.domain.repository.DemoRepository;
import io.ddd4j.boot.sample.demo.infra.persistence.converter.DemoConverter;
import io.ddd4j.boot.sample.demo.infra.persistence.mapper.DemoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Demo仓储实现（基础设施层）
 */
@Repository
@RequiredArgsConstructor
public class DemoRepositoryImpl implements DemoRepository {

    private final DemoMapper demoMapper;
    private final DemoConverter demoConverter;

    @Override
    public DemoEntity save(DemoEntity domain) {
        io.ddd4j.boot.sample.demo.infra.persistence.entity.DemoEntity entity = demoConverter.toEntity(domain);

        if (domain.getId() == null) {
            demoMapper.insert(entity);
            domain.setId(entity.getId());
        } else {
            demoMapper.updateById(entity);
        }

        return demoConverter.toDomain(entity);
    }

    @Override
    public Optional<DemoEntity> findById(Long id) {
        io.ddd4j.boot.sample.demo.infra.persistence.entity.DemoEntity entity = demoMapper.selectById(id);
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(demoConverter.toDomain(entity));
    }

    @Override
    public List<DemoEntity> findAll() {
        List<io.ddd4j.boot.sample.demo.infra.persistence.entity.DemoEntity> entities =
                demoMapper.selectList(new LambdaQueryWrapper<>());
        return entities.stream()
                .map(demoConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        demoMapper.deleteById(id);
    }
}

