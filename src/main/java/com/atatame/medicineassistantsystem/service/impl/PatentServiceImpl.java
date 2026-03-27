package com.atatame.medicineassistantsystem.service.impl;

import com.atatame.medicineassistantsystem.ai.AgentCode;
import com.atatame.medicineassistantsystem.model.dto.response.PatentRiskResponse;
import com.atatame.medicineassistantsystem.model.dto.response.PatentSimilarityResponse;
import com.atatame.medicineassistantsystem.model.entity.Patent;
import com.atatame.medicineassistantsystem.mapper.PatentMapper;
import com.atatame.medicineassistantsystem.service.IAiAgentService;
import com.atatame.medicineassistantsystem.service.IPatentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * <p>
 * 专利库 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-03-26
 */
@Service
@RequiredArgsConstructor
public class PatentServiceImpl extends ServiceImpl<PatentMapper, Patent> implements IPatentService {

    private final IAiAgentService aiAgentService;
    @Override
    public List<Patent> listByKeyword(String keyword) {
        LambdaQueryWrapper<Patent> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Patent::getName, keyword)
                    .or().like(Patent::getAbstractContent, keyword)
                    .or().like(Patent::getTechnicalField, keyword)
                    .or().like(Patent::getPatentNumber, keyword);
        }
        wrapper.orderByDesc(Patent::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<PatentSimilarityResponse> similarPatents(Long patentId) {
        Patent basePatent = getById(patentId);
        if (basePatent == null) {
            return new ArrayList<>();
        }
        List<Patent> patents = list(new LambdaQueryWrapper<Patent>().ne(Patent::getId, patentId));
        return patents.stream()
                .map(candidate -> buildPatentSimilarity(basePatent, candidate))
                .filter(item -> item.getScore() > 0.15D)
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public PatentRiskResponse patentRisk(Long patentId) {
        Patent patent = getById(patentId);
        if (patent == null) {
            return null;
        }
        String riskLevel = patent.getInfringementRisk();
        if (!StringUtils.hasText(riskLevel)) {
            riskLevel = "MEDIUM";
        }
        PatentRiskResponse response = new PatentRiskResponse();
        response.setPatentId(patent.getId());
        response.setPatentName(patent.getName());
        response.setRiskLevel(riskLevel);
        response.setRiskDescription(StringUtils.hasText(patent.getInfringementDescription())
                ? patent.getInfringementDescription()
                : "系统未检索到明确侵权描述，建议人工复核。");
        String aiSuggestion = aiAgentService.run(AgentCode.PATENT_ANALYSIS,
                "请评估专利侵权风险并给出规避建议：\n专利名称:" + patent.getName() + "\n摘要:" + patent.getAbstractContent()).getOutput();
        response.setSuggestion(StringUtils.hasText(aiSuggestion) ? aiSuggestion : buildRiskSuggestion(riskLevel));
        return response;
    }

    private PatentSimilarityResponse buildPatentSimilarity(Patent base, Patent candidate) {
        double score = 0D;
        List<String> reasons = new ArrayList<>();
        if (containsAny(base.getTechnicalField(), candidate.getTechnicalField())) {
            score += 0.35D;
            reasons.add("技术领域相近");
        }
        if (containsAny(base.getRelatedHerbs(), candidate.getRelatedHerbs())) {
            score += 0.20D;
            reasons.add("涉及药材重叠");
        }
        if (containsAny(base.getRelatedFormulas(), candidate.getRelatedFormulas())) {
            score += 0.20D;
            reasons.add("涉及方剂重叠");
        }
        if (containsAny(base.getRelatedComponents(), candidate.getRelatedComponents())) {
            score += 0.15D;
            reasons.add("涉及成分重叠");
        }
        if (containsAny(base.getRelatedTargets(), candidate.getRelatedTargets())) {
            score += 0.10D;
            reasons.add("关联靶点重叠");
        }
        PatentSimilarityResponse response = new PatentSimilarityResponse();
        response.setPatentId(candidate.getId());
        response.setPatentName(candidate.getName());
        response.setScore(Math.min(score, 1D));
        response.setReasons(reasons);
        response.setRiskLevel(candidate.getInfringementRisk());
        return response;
    }

    private boolean containsAny(String left, String right) {
        if (!StringUtils.hasText(left) || !StringUtils.hasText(right)) {
            return false;
        }
        String a = left.toLowerCase(Locale.ROOT);
        String b = right.toLowerCase(Locale.ROOT);
        return tokenize(a).stream().anyMatch(token -> token.length() > 1 && b.contains(token));
    }

    private List<String> tokenize(String value) {
        String normalized = value.replace("[", "").replace("]", "")
                .replace("\"", "").replace("{", "").replace("}", "");
        String[] tokens = normalized.split("[,;|\\s]+");
        List<String> result = new ArrayList<>();
        for (String token : tokens) {
            if (StringUtils.hasText(token)) {
                result.add(token.trim());
            }
        }
        return result.stream().distinct().collect(Collectors.toList());
    }

    private String buildRiskSuggestion(String riskLevel) {
        if ("HIGH".equalsIgnoreCase(riskLevel)) {
            return "建议立即进行权利要求逐条比对，并发起法务评审。";
        }
        if ("MEDIUM".equalsIgnoreCase(riskLevel)) {
            return "建议开展专利地图检索并补充规避设计。";
        }
        if ("LOW".equalsIgnoreCase(riskLevel)) {
            return "建议保持定期监控，跟踪同族与近似专利动态。";
        }
        return "当前风险较低，建议持续监控即可。";
    }
}
