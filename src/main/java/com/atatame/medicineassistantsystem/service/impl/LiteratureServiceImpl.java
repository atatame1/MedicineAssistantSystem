package com.atatame.medicineassistantsystem.service.impl;

import com.atatame.medicineassistantsystem.ai.AgentCode;
import com.atatame.medicineassistantsystem.model.dto.response.LiteratureRefreshResponse;
import com.atatame.medicineassistantsystem.model.dto.response.LiteratureSummaryResponse;
import com.atatame.medicineassistantsystem.model.entity.Literature;
import com.atatame.medicineassistantsystem.mapper.LiteratureMapper;
import com.atatame.medicineassistantsystem.service.IAiAgentService;
import com.atatame.medicineassistantsystem.service.ILiteratureService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 文献库 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-03-26
 */
@Service
@RequiredArgsConstructor
public class LiteratureServiceImpl extends ServiceImpl<LiteratureMapper, Literature> implements ILiteratureService {

    private final IAiAgentService aiAgentService;
    @Override
    public List<Literature> listByKeyword(String keyword) {
        LambdaQueryWrapper<Literature> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Literature::getTitle, keyword)
                    .or().like(Literature::getAbstractContent, keyword)
                    .or().like(Literature::getKeywords, keyword)
                    .or().like(Literature::getMainFindings, keyword);
        }
        wrapper.orderByDesc(Literature::getPublishYear);
        return list(wrapper);
    }

    @Override
    public LiteratureSummaryResponse buildSummary(Long literatureId) {
        Literature literature = getById(literatureId);
        if (literature == null) {
            return null;
        }
        LiteratureSummaryResponse response = new LiteratureSummaryResponse();
        response.setId(literature.getId());
        response.setTitle(literature.getTitle());
        response.setResearchType(literature.getResearchType());
        response.setMethodology(cut(literature.getMethodology(), 180));
        response.setMainFindings(cut(literature.getMainFindings(), 220));
        response.setConclusion(cut(literature.getConclusion(), 220));
        response.setInnovations(cut(literature.getInnovations(), 180));
        response.setKeywords(literature.getKeywords());
        String aiText = aiAgentService.run(AgentCode.LITERATURE_ANALYSIS,
                "请基于以下文献信息生成结构化摘要：\n标题:" + literature.getTitle() + "\n摘要:" + literature.getAbstractContent()).getOutput();
        response.setMainFindings(cut(aiText, 220));
        return response;
    }

    @Override
    public LiteratureRefreshResponse triggerRefresh(String source) {
        LiteratureRefreshResponse response = new LiteratureRefreshResponse();
        response.setStatus("ACCEPTED");
        response.setMessage("文献自动抓取任务已触发，当前为占位实现。");
        response.setSource(source);
        response.setTriggerTime(LocalDateTime.now());
        return response;
    }

    private String cut(String text, int maxLen) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        return text.length() <= maxLen ? text : text.substring(0, maxLen) + "...";
    }
}
