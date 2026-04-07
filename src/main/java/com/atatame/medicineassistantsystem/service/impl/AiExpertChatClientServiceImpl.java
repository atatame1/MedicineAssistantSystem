package com.atatame.medicineassistantsystem.service.impl;

import com.atatame.medicineassistantsystem.exception.BusinessException;
import com.atatame.medicineassistantsystem.model.dto.request.ExpertChatClientRequest;
import com.atatame.medicineassistantsystem.service.IAiExpertChatClientService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AiExpertChatClientServiceImpl implements IAiExpertChatClientService {

    private final ResourceLoader resourceLoader;

    private final ChatMemory chatMemory;
    private final ChatClient chatClient;

    private final AtomicLong conversationSeq = new AtomicLong(System.currentTimeMillis());
    private final ConcurrentHashMap<String, String> promptCache = new ConcurrentHashMap<>();

    public AiExpertChatClientServiceImpl(ChatClient.Builder chatClientBuilder, ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(20)
                .build();
        this.chatClient = chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(this.chatMemory).build())
                .build();
    }

    private static List<String> splitByCodePoints(String chunk, int maxChars) {
        if (chunk == null || chunk.isEmpty()) return List.of();
        if (maxChars < 1) return List.of(chunk);
        List<String> parts = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        int count = 0;
        for (int cp : chunk.codePoints().toArray()) {
            cur.appendCodePoint(cp);
            count++;
            if (count >= maxChars) {
                parts.add(cur.toString());
                cur.setLength(0);
                count = 0;
            }
        }
        if (cur.length() > 0) parts.add(cur.toString());
        return parts;
    }

    private static String normalizeExpertKey(String expert) {
        if (expert == null) return null;
        String v = expert.trim();
        if (v.isEmpty()) return null;
        return v.replace("（", "(").replace("）", ")").replace("·", ".").replace("—", "-");
    }

    private static String expertPromptPath(String expert) {
        String v = normalizeExpertKey(expert);
        if (v == null) return null;
        return switch (v) {
            case "何任", "he-ren", "HeRen" -> "classpath:prompts/expert/he-ren.md";
            case "李佃贵", "李佃貴", "li-dian-gui", "LiDianGui" -> "classpath:prompts/expert/li-dian-gui.md";
            case "王玉川", "wang-yu-chuan", "WangYuChuan" -> "classpath:prompts/expert/wang-yu-chuan.md";
            case "唐由之", "tang-you-zhi", "TangYouZhi" -> "classpath:prompts/expert/tang-you-zhi.md";
            case "程莘农", "cheng-shen-nong", "ChengShenNong" -> "classpath:prompts/expert/cheng-shen-nong.md";
            case "朱良春", "zhu-liang-chun", "ZhuLiangChun" -> "classpath:prompts/expert/zhu-liang-chun.md";
            case "黄宽", "huang-kuan", "HuangKuan" -> "classpath:prompts/expert/huang-kuan.md";
            case "裘法祖", "qiu-fa-zu", "QiuFaZu" -> "classpath:prompts/expert/qiu-fa-zu.md";
            case "张孝骞", "zhang-xiao-qian", "ZhangXiaoQian" -> "classpath:prompts/expert/zhang-xiao-qian.md";
            case "陈可冀", "chen-ke-ji", "ChenKeJi" -> "classpath:prompts/expert/chen-ke-ji.md";
            case "吴咸中", "wu-xian-zhong", "WuXianZhong" -> "classpath:prompts/expert/wu-xian-zhong.md";
            case "孙燕", "sun-yan", "SunYan" -> "classpath:prompts/expert/sun-yan.md";
            case "詹姆斯·艾利森", "James Allison", "JamesAllison", "jim-allison", "james-allison" -> "classpath:prompts/expert/james-allison.md";
            case "阿德姆·帕塔普蒂安", "Ardem Patapoutian", "ArdemPatapoutian", "ardem-patapoutian" -> "classpath:prompts/expert/ardem-patapoutian.md";
            case "巴里·马歇尔", "Barry Marshall", "BarryMarshall", "barry-marshall" -> "classpath:prompts/expert/barry-marshall.md";
            default -> null;
        };
    }

    private String loadPromptCached(String path) {
        return promptCache.computeIfAbsent(path, p -> {
            try {
                Resource r = resourceLoader.getResource(p);
                byte[] bs = r.getInputStream().readAllBytes();
                return new String(bs, StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new IllegalStateException("无法读取提示词资源: " + p, e);
            }
        });
    }

    @Override
    public SseEmitter stream(ExpertChatClientRequest request) {
        String promptPath = expertPromptPath(request == null ? null : request.getExpert());
        if (promptPath == null) throw new BusinessException("expert 无效");
        String prompt = loadPromptCached(promptPath);

        Long cid = request == null ? null : request.getConversationId();
        boolean created = false;
        if (cid == null) {
            cid = conversationSeq.incrementAndGet();
            created = true;
        }
        String convId = String.valueOf(cid);
        String input = request != null && request.getInput() != null ? request.getInput() : "";

        SseEmitter emitter = new SseEmitter(600_000L);
        if (created) {
            try {
                emitter.send(SseEmitter.event().name("meta").data(Map.of("conversationId", cid), MediaType.APPLICATION_JSON));
            } catch (IOException e) {
                emitter.completeWithError(e);
                return emitter;
            }
        }

        chatClient.prompt()
                .system(prompt)
                .user(input)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, convId))
                .stream()
                .content()
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(chunk -> {
                    List<String> pieces = splitByCodePoints(chunk, 16);
                    return pieces.isEmpty() ? Flux.empty() : Flux.fromIterable(pieces);
                })
                .subscribe(
                        piece -> {
                            try {
                                emitter.send(SseEmitter.event().data(piece));
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        emitter::completeWithError,
                        emitter::complete
                );
        return emitter;
    }
}

