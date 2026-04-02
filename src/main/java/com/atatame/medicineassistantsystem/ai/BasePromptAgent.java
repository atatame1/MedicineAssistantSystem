package com.atatame.medicineassistantsystem.ai;

import com.atatame.medicineassistantsystem.utils.ChatMemoryUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;

public abstract class BasePromptAgent implements AiAgent {

    private final ChatClient chatClient;
    private final String systemPrompt;

    protected BasePromptAgent(ChatClient.Builder builder, Resource promptResource, ChatMemoryUtil chatMemoryUtil) {
        this.chatClient = builder.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemoryUtil).build()).build();
        try {
            this.systemPrompt = new String(promptResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("无法读取提示词资源: " + promptResource.getDescription(), e);
        }
    }

    @Override
    public String run(String input,Long conversationId) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(input)
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID,conversationId))
                .call()
                .content();
    }

    @Override
    public Flux<String> stream(String input,Long conversationId) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(input)
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID,conversationId))
                .stream()
                .content();
    }

    @Override
    public String run(String input) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(input)
                .call()
                .content();
    }

    @Override
    public Flux<String> stream(String input) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(input)
                .stream()
                .content();
    }
}

