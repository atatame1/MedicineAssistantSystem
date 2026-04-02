package com.atatame.medicineassistantsystem.utils;

import com.atatame.medicineassistantsystem.mapper.AiAgentConversationMapper;
import com.atatame.medicineassistantsystem.model.entity.AiAgentConversation;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import com.fasterxml.jackson.core.type.TypeReference;

@Component
@RequiredArgsConstructor
public class ChatMemoryUtil implements ChatMemory {

    private final AiAgentConversationMapper aiAgentConversationMapper;
    private final ObjectMapper objectMapper;

    private static class MemMsg {
        public String role;
        public String content;
    }

    private static String safePrefixTitle(String s, int maxChars) {
        if (!StringUtils.hasText(s)) return null;
        String v = s.trim();
        return v.length() <= maxChars ? v : v.substring(0, maxChars);
    }

    private AiAgentConversation findByConversationId(String conversationId) {
        if (!StringUtils.hasText(conversationId)) return null;
        try {
            Long cid = Long.valueOf(conversationId);
            return aiAgentConversationMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiAgentConversation>()
                            .eq(AiAgentConversation::getConversationId, cid)
                            .last("LIMIT 1")
            );
        } catch (Exception e) {
            return null;
        }
    }

    private static List<MemMsg> toMemMsgs(List<Message> messages) {
        List<MemMsg> out = new ArrayList<>();
        for (Message m : messages) {
            MemMsg mm = new MemMsg();
            if (m instanceof UserMessage) {
                mm.role = "user";
                mm.content = ((UserMessage) m).getText();
            } else if (m instanceof AssistantMessage) {
                mm.role = "assistant";
                mm.content = ((AssistantMessage) m).getText();
            } else if (m instanceof SystemMessage) {
                mm.role = "system";
                mm.content = ((SystemMessage) m).getText();
            } else {
                mm.role = "unknown";
                mm.content = m.toString();
            }
            out.add(mm);
        }
        return out;
    }

    private static List<Message> fromMemMsgs(List<MemMsg> mem) {
        List<Message> out = new ArrayList<>();
        for (MemMsg mm : mem) {
            if ("user".equals(mm.role)) out.add(new UserMessage(mm.content == null ? "" : mm.content));
            else if ("assistant".equals(mm.role)) out.add(new AssistantMessage(mm.content == null ? "" : mm.content));
            else if ("system".equals(mm.role)) out.add(new SystemMessage(mm.content == null ? "" : mm.content));
        }
        return out;
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        AiAgentConversation row = findByConversationId(conversationId);
        if (row == null) return;
        List<MemMsg> mem = toMemMsgs(messages);
        try {
            row.setMessagesJson(objectMapper.writeValueAsString(mem));
        } catch (Exception e) {
            row.setMessagesJson(null);
        }

        String firstUser = null;
        String lastUser = null;
        String lastAssistant = null;
        for (Message m : messages) {
            if (m instanceof UserMessage) {
                String text = ((UserMessage) m).getText();
                if (firstUser == null) firstUser = text;
                lastUser = text;
            }
            if (m instanceof AssistantMessage) {
                lastAssistant = ((AssistantMessage) m).getText();
            }
        }

        if (!StringUtils.hasText(row.getTitle()) && firstUser != null) {
            row.setTitle(safePrefixTitle(firstUser, 12));
        }
        row.setInputText(lastUser);
        row.setOutputText(lastAssistant);
        row.setCreateTime(LocalDateTime.now());
        aiAgentConversationMapper.updateById(row);
    }

    @Override
    public List<Message> get(String conversationId) {
        AiAgentConversation row = findByConversationId(conversationId);
        if (row == null || !StringUtils.hasText(row.getMessagesJson())) return List.of();
        try {
            List<MemMsg> mem = objectMapper.readValue(row.getMessagesJson(), new TypeReference<List<MemMsg>>() {});
            return fromMemMsgs(mem);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public void clear(String conversationId) {
        AiAgentConversation row = findByConversationId(conversationId);
        if (row == null) return;
        row.setMessagesJson(null);
        row.setInputText(null);
        row.setOutputText(null);
        row.setCreateTime(LocalDateTime.now());
        aiAgentConversationMapper.updateById(row);
    }
}
