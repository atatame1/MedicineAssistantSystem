package com.atatame.medicineassistantsystem.service.impl;

import com.atatame.medicineassistantsystem.model.entity.Herb;
import com.atatame.medicineassistantsystem.mapper.HerbMapper;
import com.atatame.medicineassistantsystem.service.IHerbService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 中药材信息库 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-03-26
 */
@Service
public class HerbServiceImpl extends ServiceImpl<HerbMapper, Herb> implements IHerbService {
    @Override
    public List<Herb> listByKeyword(String keyword) {
        LambdaQueryWrapper<Herb> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Herb::getName, keyword)
                    .or().like(Herb::getAliases, keyword)
                    .or().like(Herb::getEffects, keyword);
        }
        return list(wrapper);
    }
}
