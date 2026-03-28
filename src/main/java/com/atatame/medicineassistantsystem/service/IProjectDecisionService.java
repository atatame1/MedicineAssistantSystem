package com.atatame.medicineassistantsystem.service;

import com.atatame.medicineassistantsystem.model.dto.response.DecisionCompareResponse;
import com.atatame.medicineassistantsystem.model.entity.ProjectDecision;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IProjectDecisionService extends IService<ProjectDecision> {

    DecisionCompareResponse compare(Long id1, Long id2);
}
