package com.atatame.medicineassistantsystem.service.impl;

import com.atatame.medicineassistantsystem.ai.AgentCode;
import com.atatame.medicineassistantsystem.model.dto.response.RegulationReminderResponse;
import com.atatame.medicineassistantsystem.model.entity.Regulation;
import com.atatame.medicineassistantsystem.mapper.RegulationMapper;
import com.atatame.medicineassistantsystem.service.IAiAgentService;
import com.atatame.medicineassistantsystem.service.IRegulationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 新药研发法规和指引库 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-03-26
 */
@Service
@RequiredArgsConstructor
public class RegulationServiceImpl extends ServiceImpl<RegulationMapper, Regulation> implements IRegulationService {

    private final IAiAgentService aiAgentService;
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

    @Override
    public List<RegulationReminderResponse> reminders() {
        List<Regulation> regulations = list(new LambdaQueryWrapper<Regulation>()
                .eq(Regulation::getNeedFollowUp, true)
                .orderByDesc(Regulation::getEffectiveDate));
        return regulations.stream().map(regulation -> {
            RegulationReminderResponse response = new RegulationReminderResponse();
            response.setId(regulation.getId());
            response.setName(regulation.getName());
            response.setRegulationNumber(regulation.getRegulationNumber());
            response.setFollowUpStatus(regulation.getFollowUpStatus());
            response.setEffectiveDate(regulation.getEffectiveDate());
            response.setImportance(regulation.getImportance());
            String aiImpact = aiAgentService.run(AgentCode.REPORT_GENERATION,
                    "请总结法规对中药新药研发的影响：\n法规名称:" + regulation.getName() + "\n摘要:" + regulation.getSummary()).getOutput();
            response.setImpactAssessment(StringUtils.hasText(aiImpact) ? aiImpact : regulation.getImpactAssessment());
            return response;
        }).collect(Collectors.toList());
    }
}
