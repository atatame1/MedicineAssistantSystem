package com.atatame.medicineassistantsystem.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;

public abstract class BasePromptAgent implements AiAgent {

    private final ChatClient chatClient;
    private final String systemPrompt;

    protected BasePromptAgent(ChatClient.Builder builder, Resource promptResource) {
        this.chatClient = builder.build();
        try {
            this.systemPrompt = new String(promptResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("无法读取提示词资源: " + promptResource.getDescription(), e);
        }
    }

    @Override
    public String run(String input) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(input)
                .call()
                .content();
    }
}

