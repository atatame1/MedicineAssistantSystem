package com.atatame.medicineassistantsystem.service.impl;

import com.atatame.medicineassistantsystem.model.entity.TargetPathway;
import com.atatame.medicineassistantsystem.mapper.TargetPathwayMapper;
import com.atatame.medicineassistantsystem.service.ITargetPathwayService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 靶点与信号通路库 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-03-26
 */
@Service
public class TargetPathwayServiceImpl extends ServiceImpl<TargetPathwayMapper, TargetPathway> implements ITargetPathwayService {
    @Override
    public List<TargetPathway> listByKeyword(String keyword) {
        LambdaQueryWrapper<TargetPathway> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(TargetPathway::getName, keyword)
                    .or().like(TargetPathway::getGeneSymbol, keyword)
                    .or().like(TargetPathway::getPathwayName, keyword)
                    .or().like(TargetPathway::getRelatedDiseases, keyword);
        }
        return list(wrapper);
    }
}
