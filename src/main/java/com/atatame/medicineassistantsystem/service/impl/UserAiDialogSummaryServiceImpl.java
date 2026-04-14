package com.atatame.medicineassistantsystem.service.impl;

import com.atatame.medicineassistantsystem.ai.AgentCode;
import com.atatame.medicineassistantsystem.mapper.UserAiDialogSummaryMapper;
import com.atatame.medicineassistantsystem.model.dto.response.AiTaskResponse;
import com.atatame.medicineassistantsystem.model.entity.AiAgentConversation;
import com.atatame.medicineassistantsystem.model.entity.AiAgentMessage;
import com.atatame.medicineassistantsystem.model.entity.User;
import com.atatame.medicineassistantsystem.model.entity.UserAiDialogSummary;
import com.atatame.medicineassistantsystem.service.IAiAgentConversationService;
import com.atatame.medicineassistantsystem.service.IAiAgentMessageService;
import com.atatame.medicineassistantsystem.service.IAiAgentService;
import com.atatame.medicineassistantsystem.service.IUserAiDialogSummaryService;
import com.atatame.medicineassistantsystem.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final IUserService userService;

    @Override
    public String summarizeUser(Long userId) {
        if (userId == null) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        List<AiAgentConversation> conversations = aiAgentConversationService.list(
                new LambdaQueryWrapper<AiAgentConversation>()
                        .eq(AiAgentConversation::getUserId, userId)
        );

        if (conversations == null || conversations.isEmpty()) return null;

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

        if (msgs == null || msgs.isEmpty()) return null;

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

        User u = userService.getById(userId);
        String userName = "您";
        if (u != null) {
            String n = u.getNickname();
            if (n == null || n.isBlank()) n = u.getUsername();
            if (n != null && !n.isBlank()) userName = n.trim();
        }

        String promptTpl = "你是一位细致贴心的个人助理。请以温和、尊敬的口吻，根据下方用户最近与各个智能体的对话记录，撰写一段不超过300字的个性化回顾。\n"
                + "回顾开头必须使用格式：'尊敬的%s您好：'\n"
                + "内容请柔和地概括用户最近关注的主题、典型问题和可能的偏好，避免生硬列举，语气需包含关怀与尊重。\n\n"
                + "对话记录如下：\n" + context;
        String prompt = String.format(promptTpl, userName);
        String summary;
        try {
            AiTaskResponse resp = aiAgentService.run(AgentCode.REPORT_GENERATION, prompt);
            summary = resp == null ? null : resp.getOutput();
        } catch (Exception ex) {
            log.warn("summarize user {} dialog failed", userId, ex);
            return null;
        }
        if (summary == null || summary.isBlank()) {
            return null;
        }

        row.setSummaryText(summary);
        row.setUpdateTime(now);
        this.saveOrUpdate(row);
        return summary;
    }
}


