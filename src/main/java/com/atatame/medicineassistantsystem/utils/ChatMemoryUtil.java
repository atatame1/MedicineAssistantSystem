package com.atatame.medicineassistantsystem.utils;

import com.atatame.medicineassistantsystem.mapper.AiAgentConversationMapper;
import com.atatame.medicineassistantsystem.model.entity.AiAgentConversation;
import com.atatame.medicineassistantsystem.model.entity.AiAgentMessage;
import com.atatame.medicineassistantsystem.service.IAiAgentMessageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatMemoryUtil implements ChatMemory {

    private final AiAgentConversationMapper aiAgentConversationMapper;
    private final IAiAgentMessageService aiAgentMessageService;

    private static String safePrefixTitle(String s, int maxChars) {
        if (!StringUtils.hasText(s)) return null;
        String v = s.trim();
        return v.length() <= maxChars ? v : v.substring(0, maxChars);
    }

    private AiAgentConversation findConversation(String conversationId) {
        if (!StringUtils.hasText(conversationId)) return null;
        try {
            Long id = Long.valueOf(conversationId);
            return aiAgentConversationMapper.selectById(id);
        } catch (Exception e) {
            return null;
        }
    }

    private static String roleOf(Message m) {
        if (m instanceof UserMessage) return "user";
        if (m instanceof AssistantMessage) return "assistant";
        if (m instanceof SystemMessage) return "system";
        return "unknown";
    }

    private static String textOf(Message m) {
        if (m instanceof UserMessage) return ((UserMessage) m).getText();
        if (m instanceof AssistantMessage) return ((AssistantMessage) m).getText();
        if (m instanceof SystemMessage) return ((SystemMessage) m).getText();
        return m.toString();
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        AiAgentConversation conv = findConversation(conversationId);
        if (conv == null || messages == null || messages.isEmpty()) return;
        LocalDateTime now = LocalDateTime.now();
        for (Message m : messages) {
            AiAgentMessage row = new AiAgentMessage();
            row.setConversationId(conv.getId());
            row.setRole(roleOf(m));
            row.setContent(textOf(m));
            row.setCreateTime(now);
            aiAgentMessageService.save(row);
        }
        String firstUser = null;
        for (Message m : messages) {
            if (m instanceof UserMessage) {
                firstUser = ((UserMessage) m).getText();
                break;
            }
        }
        if (!StringUtils.hasText(conv.getTitle()) && firstUser != null) {
            conv.setTitle(safePrefixTitle(firstUser, 12));
        }
        conv.setCreateTime(now);
        aiAgentConversationMapper.updateById(conv);
    }

    @Override
    public List<Message> get(String conversationId) {
        AiAgentConversation conv = findConversation(conversationId);
        if (conv == null) return List.of();
        List<AiAgentMessage> rows = aiAgentMessageService.list(
                new LambdaQueryWrapper<AiAgentMessage>()
                        .eq(AiAgentMessage::getConversationId, conv.getId())
                        .orderByAsc(AiAgentMessage::getId)
        );
        List<Message> out = new ArrayList<>();
        for (AiAgentMessage r : rows) {
            String role = r.getRole();
            String c = r.getContent() == null ? "" : r.getContent();
            if ("user".equals(role)) out.add(new UserMessage(c));
            else if ("assistant".equals(role)) out.add(new AssistantMessage(c));
            else if ("system".equals(role)) out.add(new SystemMessage(c));
        }
        return out;
    }

    @Override
    public void clear(String conversationId) {
        AiAgentConversation conv = findConversation(conversationId);
        if (conv == null) return;
        aiAgentMessageService.remove(
                new LambdaQueryWrapper<AiAgentMessage>().eq(AiAgentMessage::getConversationId, conv.getId())
        );
        conv.setCreateTime(LocalDateTime.now());
        aiAgentConversationMapper.updateById(conv);
    }
}
