package com.atatame.medicineassistantsystem.ai;

import com.atatame.medicineassistantsystem.utils.ChatMemoryUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public abstract class BasePromptAgent implements AiAgent {

    private final ChatClient chatClient;
    private final ChatMemoryUtil chatMemoryUtil;
    private final ChatModel chatModel;
    private final String systemPrompt;

    protected BasePromptAgent(ChatClient.Builder builder, Resource promptResource, ChatMemoryUtil chatMemoryUtil,
                              ChatModel chatModel) {
        this.chatClient = builder.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemoryUtil).build()).build();
        this.chatMemoryUtil = chatMemoryUtil;
        this.chatModel = chatModel;
        try {
            this.systemPrompt = new String(promptResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("无法读取提示词资源: " + promptResource.getDescription(), e);
        }
    }

    @Override
    public String run(String input, Long conversationId) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(input)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }

    /**
     * 会话模式：直连 {@link ChatModel#stream(Prompt)}，与 Ollama 流式输出一致，不经 ChatClient 记忆聚合链，
     * 避免与 ChatGPT 类似的「整段生成完再下发」观感。记忆仍写入 {@link ChatMemoryUtil}。
     */
    @Override
    public Flux<String> stream(String input, Long conversationId) {
        String convId = String.valueOf(conversationId);
        List<Message> memory = chatMemoryUtil.get(convId);
        List<Message> forPrompt = new ArrayList<>();
        forPrompt.add(new SystemMessage(systemPrompt));
        forPrompt.addAll(memory);
        forPrompt.add(new UserMessage(input));
        chatMemoryUtil.add(convId, List.of(new UserMessage(input)));

        Prompt prompt = new Prompt(forPrompt);
        StringBuilder assistantFull = new StringBuilder();

        Flux<String> deltas = mapStreamingChunksToDeltas(chatModel.stream(prompt));

        return deltas
                .doOnNext(assistantFull::append)
                .doOnComplete(() -> {
                    if (assistantFull.length() > 0) {
                        chatMemoryUtil.add(convId, List.of(new AssistantMessage(assistantFull.toString())));
                    }
                });
    }

    /**
     * Ollama 各版本可能下发「累计全文」或「纯增量」token，统一转成向下游输出的增量字符串。
     */
    private static Flux<String> mapStreamingChunksToDeltas(Flux<ChatResponse> responses) {
        AtomicReference<String> previous = new AtomicReference<>("");
        return responses.concatMap(cr -> {
            if (cr.getResult() == null || cr.getResult().getOutput() == null) {
                return Flux.empty();
            }
            String chunk = cr.getResult().getOutput().getText();
            if (!StringUtils.hasText(chunk)) {
                return Flux.empty();
            }
            String prev = previous.get();
            if (!StringUtils.hasText(prev)) {
                previous.set(chunk);
                return Flux.just(chunk);
            }
            if (chunk.startsWith(prev)) {
                String delta = chunk.substring(prev.length());
                previous.set(chunk);
                return StringUtils.hasText(delta) ? Flux.just(delta) : Flux.empty();
            }
            previous.set(prev + chunk);
            return Flux.just(chunk);
        });
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
