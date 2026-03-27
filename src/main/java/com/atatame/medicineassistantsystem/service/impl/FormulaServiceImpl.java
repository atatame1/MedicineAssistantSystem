package com.atatame.medicineassistantsystem.service.impl;

import com.atatame.medicineassistantsystem.model.entity.Formula;
import com.atatame.medicineassistantsystem.mapper.FormulaMapper;
import com.atatame.medicineassistantsystem.service.IFormulaService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 经典方剂库 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-03-26
 */
@Service
public class FormulaServiceImpl extends ServiceImpl<FormulaMapper, Formula> implements IFormulaService {
    @Override
    public List<Formula> listByKeyword(String keyword) {
        LambdaQueryWrapper<Formula> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Formula::getName, keyword)
                    .or().like(Formula::getComposition, keyword)
                    .or().like(Formula::getIndications, keyword)
                    .or().like(Formula::getModernApplications, keyword);
        }
        return list(wrapper);
    }
}
