package com.atatame.medicineassistantsystem.service.impl;

import com.atatame.medicineassistantsystem.model.entity.Regulation;
import com.atatame.medicineassistantsystem.mapper.RegulationMapper;
import com.atatame.medicineassistantsystem.service.IRegulationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class RegulationServiceImpl extends ServiceImpl<RegulationMapper, Regulation> implements IRegulationService {

    @Override
    public List<Regulation> listByKeyword(String keyword) {
        LambdaQueryWrapper<Regulation> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Regulation::getName, keyword)
                    .or().like(Regulation::getRegulationNumber, keyword)
                    .or().like(Regulation::getIssuingAuthority, keyword)
                    .or().like(Regulation::getSummary, keyword);
        }
        wrapper.orderByDesc(Regulation::getEffectiveDate);
        return list(wrapper);
    }
}
