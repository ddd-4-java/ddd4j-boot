package io.ddd4j.boot.sample.demo.app.service;

import io.ddd4j.boot.sample.demo.app.command.CreateDemoCommand;
import io.ddd4j.boot.sample.demo.app.command.UpdateDemoCommand;
import io.ddd4j.boot.sample.demo.app.dto.DemoDTO;
import io.ddd4j.boot.sample.demo.domain.model.entity.DemoEntity;
import io.ddd4j.boot.sample.demo.domain.repository.DemoRepository;
import io.ddd4j.core.exception.BizRuntimeException;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Demo应用服务
 */

@Service

public class DemoApplicationService {

    private final DemoRepository demoRepository;

    /**
     * 构造 DemoApplicationService 实例。
     *
     * @param demoRepository demoRepository
     */
    public DemoApplicationService(DemoRepository demoRepository) {
        this.demoRepository = demoRepository;
    }

    /**
     * 创建Demo
     *
     * @param command 命令对象
     * @return 新增结果
     */
    public DemoDTO createDemo(CreateDemoCommand command) {
        org.slf4j.LoggerFactory.getLogger(DemoApplicationService.class).info("创建Demo，名称: {}", command.getName());

        DemoEntity entity = new DemoEntity();
        entity.setName(command.getName());
        entity.setIntro(command.getIntro());
        entity.setOrderBy(command.getOrderBy());
        entity.setStatus(command.getStatus() != null ? command.getStatus() : 1);

        DemoEntity saved = demoRepository.save(entity);
        return toDTO(saved);
    }

    /**
     * 更新Demo
     *
     * @param command 命令对象
     * @return 更新结果
     */
    public DemoDTO updateDemo(UpdateDemoCommand command) {
        org.slf4j.LoggerFactory.getLogger(DemoApplicationService.class).info("更新Demo，ID: {}", command.getId());

        DemoEntity entity = demoRepository.findById(command.getId())
                .orElseThrow(() -> new BizRuntimeException("Demo不存在"));

        if (command.getName() != null) {
            entity.setName(command.getName());
        }
        if (command.getIntro() != null) {
            entity.setIntro(command.getIntro());
        }
        if (command.getOrderBy() != null) {
            entity.setOrderBy(command.getOrderBy());
        }
        if (command.getStatus() != null) {
            entity.setStatus(command.getStatus());
        }

        DemoEntity saved = demoRepository.save(entity);
        return toDTO(saved);
    }

    /**
     * 根据ID查询Demo
     *
     * @param id 标识 ID
     * @return 查询结果
     */
    public DemoDTO getDemoById(Long id) {
        return demoRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new BizRuntimeException("Demo不存在"));
    }

    /**
     * 查询所有Demo
     *
     * @return 查询结果
     */
    public List<DemoDTO> getAllDemos() {
        return demoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 删除Demo
     *
     * @param id 标识 ID
     */
    public void deleteDemo(Long id) {
        org.slf4j.LoggerFactory.getLogger(DemoApplicationService.class).info("删除Demo，ID: {}", id);
        demoRepository.delete(id);
    }

    /**
     * 批量删除Demo
     *
     * @param ids 标识 ID 集合
     */
    public void deleteDemos(List<Long> ids) {
        org.slf4j.LoggerFactory.getLogger(DemoApplicationService.class).info("批量删除Demo，IDs: {}", ids);
        ids.forEach(this::deleteDemo);
    }

    /**
     * 实体转DTO
     */
    private DemoDTO toDTO(DemoEntity entity) {
        DemoDTO dto = new DemoDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setIntro(entity.getIntro());
        dto.setOrderBy(entity.getOrderBy());
        dto.setStatus(entity.getStatus());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }
}

