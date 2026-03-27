package com.atatame.medicineassistantsystem.service.impl;

import com.atatame.medicineassistantsystem.model.entity.Component;
import com.atatame.medicineassistantsystem.mapper.ComponentMapper;
import com.atatame.medicineassistantsystem.service.IComponentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 化学成分库 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-03-26
 */
@Service
public class ComponentServiceImpl extends ServiceImpl<ComponentMapper, Component> implements IComponentService {
    @Override
    public List<Component> listByKeyword(String keyword) {
        LambdaQueryWrapper<Component> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Component::getName, keyword)
                    .or().like(Component::getCasNumber, keyword)
                    .or().like(Component::getBioactivity, keyword)
                    .or().like(Component::getPotentialTargets, keyword);
        }
        return list(wrapper);
    }
}
