package com.atatame.medicineassistantsystem.service.impl;

import com.atatame.medicineassistantsystem.ai.AgentCode;
import com.atatame.medicineassistantsystem.mapper.UserAiDialogSummaryMapper;
import com.atatame.medicineassistantsystem.model.dto.response.AiTaskResponse;
import com.atatame.medicineassistantsystem.model.entity.AiAgentConversation;
import com.atatame.medicineassistantsystem.model.entity.AiAgentMessage;
import com.atatame.medicineassistantsystem.model.entity.UserAiDialogSummary;
import com.atatame.medicineassistantsystem.service.IAiAgentConversationService;
import com.atatame.medicineassistantsystem.service.IAiAgentMessageService;
import com.atatame.medicineassistantsystem.service.IAiAgentService;
import com.atatame.medicineassistantsystem.service.IUserAiDialogSummaryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAiDialogSummaryServiceImpl
        extends ServiceImpl<UserAiDialogSummaryMapper, UserAiDialogSummary>
        implements IUserAiDialogSummaryService {

    private final IAiAgentConversationService aiAgentConversationService;
    private final IAiAgentMessageService aiAgentMessageService;
    private final IAiAgentService aiAgentService;

    @Override
    public void summarizeUser(Long userId) {
        if (userId == null) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        List<AiAgentConversation> conversations = aiAgentConversationService.list(
                new LambdaQueryWrapper<AiAgentConversation>()
                        .eq(AiAgentConversation::getUserId, userId)
        );

        if(conversations==null||conversations.isEmpty())return;

        UserAiDialogSummary row = this.getById(userId);
        if (row == null) {
            row = new UserAiDialogSummary();
            row.setUserId(userId);
        }

        List<Long> convIds = conversations.stream().map(AiAgentConversation::getId).toList();
        List<AiAgentMessage> msgs = aiAgentMessageService.list(
                new LambdaQueryWrapper<AiAgentMessage>()
                        .in(AiAgentMessage::getConversationId, convIds)
                        .ge(row.getUpdateTime()!=null,AiAgentMessage::getCreateTime, row.getUpdateTime())
                        .orderByAsc(AiAgentMessage::getCreateTime)
        );

        if(msgs==null||msgs.isEmpty())return;

        StringBuilder sb = new StringBuilder();
        for (AiAgentMessage m : msgs) {
            String role = String.valueOf(m.getRole());
            String prefix = "user".equals(role) ? "用户：" : ("assistant".equals(role) ? "智能体：" : "");
            sb.append(prefix).append(m.getContent() == null ? "" : m.getContent())
                    .append(" ").append("发送时间")
                    .append(m.getCreateTime())
                    .append("\n");
        }




        if (row.getSummaryText()!=null&&!row.getSummaryText().isEmpty()) {
            sb.append("在此之前的总结为：").append(row.getSummaryText());
        }


        String context = sb.toString();

        String prompt = "下面是你在本系统中最近一天与各个智能体的对话记录，请用简洁的中文总结出你最近关注的主题、典型问题和可能的偏好，不超过 300 字：\n\n" + context;
        String summary;
        try {
            AiTaskResponse resp = aiAgentService.run(AgentCode.REPORT_GENERATION, prompt);
            summary = resp == null ? null : resp.getOutput();
        } catch (Exception ex) {
            log.warn("summarize user {} dialog failed", userId, ex);
            return;
        }
        if (summary == null || summary.isBlank()) {
            return;
        }

        row.setSummaryText(summary);
        row.setUpdateTime(now);
        this.saveOrUpdate(row);
    }
}


