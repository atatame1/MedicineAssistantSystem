package com.atatame.medicineassistantsystem.service.impl;

import com.atatame.medicineassistantsystem.model.entity.Disease;
import com.atatame.medicineassistantsystem.mapper.DiseaseMapper;
import com.atatame.medicineassistantsystem.service.IDiseaseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 疾病分类库 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-03-26
 */
@Service
public class DiseaseServiceImpl extends ServiceImpl<DiseaseMapper, Disease> implements IDiseaseService {
    @Override
    public List<Disease> listByKeyword(String keyword) {
        LambdaQueryWrapper<Disease> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Disease::getName, keyword)
                    .or().like(Disease::getTcmSyndrome, keyword)
                    .or().like(Disease::getModernName, keyword)
                    .or().like(Disease::getRelatedTargets, keyword);
        }
        return list(wrapper);
    }
}
