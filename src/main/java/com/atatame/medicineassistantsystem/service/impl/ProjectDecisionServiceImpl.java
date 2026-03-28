package com.atatame.medicineassistantsystem.service.impl;

import com.atatame.medicineassistantsystem.exception.BusinessException;
import com.atatame.medicineassistantsystem.mapper.ProjectDecisionMapper;
import com.atatame.medicineassistantsystem.model.dto.response.DecisionCompareResponse;
import com.atatame.medicineassistantsystem.model.entity.ProjectDecision;
import com.atatame.medicineassistantsystem.service.IProjectDecisionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ProjectDecisionServiceImpl extends ServiceImpl<ProjectDecisionMapper, ProjectDecision> implements IProjectDecisionService {

    @Override
    public DecisionCompareResponse compare(Long id1, Long id2) {
        ProjectDecision a = getById(id1);
        ProjectDecision b = getById(id2);
        if (a == null || b == null) {
            throw new BusinessException("决策记录不存在");
        }
        if (!a.getProjectId().equals(b.getProjectId())) {
            throw new BusinessException("仅支持同一项目下的决策版本对比");
        }
        return new DecisionCompareResponse(a, b);
    }
}
