package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.ai.AgentCode;
import com.atatame.medicineassistantsystem.auth.AuthInterceptor;
import com.atatame.medicineassistantsystem.common.Result;
import com.atatame.medicineassistantsystem.exception.BusinessException;
import com.atatame.medicineassistantsystem.model.dto.request.AiConversationTopRequest;
import com.atatame.medicineassistantsystem.model.dto.request.AiTaskRequest;
import com.atatame.medicineassistantsystem.model.dto.response.AiAgentMessageResponse;
import com.atatame.medicineassistantsystem.model.dto.response.AiConversationHistoryResponse;
import com.atatame.medicineassistantsystem.model.entity.AiAgentConversation;
import com.atatame.medicineassistantsystem.model.entity.AiAgentMessage;
import com.atatame.medicineassistantsystem.service.IAiAgentConversationService;
import com.atatame.medicineassistantsystem.service.IAiAgentMessageService;
import com.atatame.medicineassistantsystem.service.IAiAgentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "智能体中心", description = "各研发功能独立智能体")
public class AiAgentController {

    private final IAiAgentService aiAgentService;
    private final IAiAgentConversationService aiAgentConversationService;
    private final IAiAgentMessageService aiAgentMessageService;

    private Long resolveUserId(HttpServletRequest request) {
        Object v = request.getAttribute(AuthInterceptor.ATTR_USER_ID);
        if (v == null) return null;
        if (v instanceof Long) return (Long) v;
        return null;
    }

    private static String buildTitle(String input) {
        if (input == null) return null;
        String v = input.trim();
        if (v.isEmpty()) return null;
        return v.length() <= 12 ? v : v.substring(0, 12);
    }

    private static final class ConvCtx {
        final Long id;
        final boolean created;

        ConvCtx(Long id, boolean created) {
            this.id = id;
            this.created = created;
        }
    }

    private ConvCtx ensureConversation(HttpServletRequest servletRequest, AgentCode code, AiTaskRequest request) {
        Long userId = resolveUserId(servletRequest);
        if (userId == null) throw new BusinessException("未登录");
        Long cid = request.getConversationId();
        if (cid == null) {
            AiAgentConversation row = new AiAgentConversation();
            row.setUserId(userId);
            row.setType(code.name());
            row.setTitle(buildTitle(request.getInput()));
            row.setIsTop(Boolean.FALSE);
            row.setCreateTime(LocalDateTime.now());
            aiAgentConversationService.save(row);
            return new ConvCtx(row.getId(), true);
        }
        AiAgentConversation row = aiAgentConversationService.getById(cid);
        if (row == null || !userId.equals(row.getUserId()) || !code.name().equals(row.getType())) {
            throw new BusinessException("会话不存在");
        }
        if (row.getTitle() == null || row.getTitle().isBlank()) {
            row.setTitle(buildTitle(request.getInput()));
            aiAgentConversationService.updateById(row);
        }
        return new ConvCtx(row.getId(), false);
    }

    private void fillPreview(AiAgentConversation c, AiConversationHistoryResponse dto) {
        List<AiAgentMessage> msgs = aiAgentMessageService.list(
                new LambdaQueryWrapper<AiAgentMessage>()
                        .eq(AiAgentMessage::getConversationId, c.getId())
                        .orderByDesc(AiAgentMessage::getId)
                        .last("LIMIT 120")
        );
        String lastUser = null;
        String lastAssistant = null;
        for (AiAgentMessage m : msgs) {
            if ("user".equals(m.getRole()) && lastUser == null) {
                lastUser = m.getContent();
            }
            if ("assistant".equals(m.getRole()) && lastAssistant == null) {
                lastAssistant = m.getContent();
            }
            if (lastUser != null && lastAssistant != null) {
                break;
            }
        }
        dto.setInputText(lastUser);
        dto.setOutputText(lastAssistant);
    }

    private AiAgentConversation requireOwnedConversation(Long userId, Long id) {
        AiAgentConversation c = aiAgentConversationService.getById(id);
        if (c == null || !userId.equals(c.getUserId())) {
            throw new BusinessException("会话不存在");
        }
        return c;
    }

    private static List<String> splitByCodePoints(String chunk, int maxChars) {
        if (chunk == null || chunk.isEmpty()) {
            return List.of();
        }
        if (maxChars < 1) {
            return List.of(chunk);
        }
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
        if (cur.length() > 0) {
            parts.add(cur.toString());
        }
        return parts;
    }

    private SseEmitter stream(AgentCode code, AiTaskRequest request, HttpServletRequest servletRequest) {
        ConvCtx ctx = ensureConversation(servletRequest, code, request);
        Long conversationId = ctx.id;
        String input = request.getInput() == null ? "" : request.getInput();
        SseEmitter emitter = new SseEmitter(600_000L);
        if (ctx.created && conversationId != null) {
            try {
                emitter.send(SseEmitter.event().name("meta").data(Map.of("conversationId", conversationId), MediaType.APPLICATION_JSON));
            } catch (IOException e) {
                emitter.completeWithError(e);
                return emitter;
            }
        }
        aiAgentService.stream(code, input, conversationId)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(chunk -> {
                    List<String> pieces = splitByCodePoints(chunk, 16);
                    if (pieces.isEmpty()) {
                        return Flux.empty();
                    }
                    return Flux.fromIterable(pieces);
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

    @PostMapping(value = "/project-evaluation", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "立项评估智能体")
    public SseEmitter projectEvaluation(@RequestBody AiTaskRequest request, HttpServletRequest servletRequest) {
        return stream(AgentCode.PROJECT_EVALUATION, request, servletRequest);
    }

    @PostMapping(value = "/formula-compatibility", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "配伍分析智能体")
    public SseEmitter formulaCompatibility(@RequestBody AiTaskRequest request, HttpServletRequest servletRequest) {
        return stream(AgentCode.FORMULA_COMPATIBILITY, request, servletRequest);
    }

    @PostMapping(value = "/mechanism-inference", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "机制推演智能体")
    public SseEmitter mechanismInference(@RequestBody AiTaskRequest request, HttpServletRequest servletRequest) {
        return stream(AgentCode.MECHANISM_INFERENCE, request, servletRequest);
    }

    @PostMapping(value = "/target-prediction", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "靶点预测智能体")
    public SseEmitter targetPrediction(@RequestBody AiTaskRequest request, HttpServletRequest servletRequest) {
        return stream(AgentCode.TARGET_PREDICTION, request, servletRequest);
    }

    @PostMapping(value = "/literature-analysis", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "文献分析智能体")
    public SseEmitter literatureAnalysis(@RequestBody AiTaskRequest request, HttpServletRequest servletRequest) {
        return stream(AgentCode.LITERATURE_ANALYSIS, request, servletRequest);
    }

    @PostMapping(value = "/patent-analysis", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "专利分析智能体")
    public SseEmitter patentAnalysis(@RequestBody AiTaskRequest request, HttpServletRequest servletRequest) {
        return stream(AgentCode.PATENT_ANALYSIS, request, servletRequest);
    }

    @PostMapping(value = "/experiment-design", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "实验设计智能体")
    public SseEmitter experimentDesign(@RequestBody AiTaskRequest request, HttpServletRequest servletRequest) {
        return stream(AgentCode.EXPERIMENT_DESIGN, request, servletRequest);
    }

    @PostMapping(value = "/report-generation", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "报告生成智能体")
    public SseEmitter reportGeneration(@RequestBody AiTaskRequest request, HttpServletRequest servletRequest) {
        return stream(AgentCode.REPORT_GENERATION, request, servletRequest);
    }

    @PostMapping(value = "/decision-support", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "研发决策支持智能体")
    public SseEmitter decisionSupport(@RequestBody AiTaskRequest request, HttpServletRequest servletRequest) {
        return stream(AgentCode.DECISION_SUPPORT, request, servletRequest);
    }

    @GetMapping("/history")
    @Operation(summary = "智能体对话历史")
    public Result<List<AiConversationHistoryResponse>> history(
            @RequestParam String agentType,
            @RequestParam(defaultValue = "100") Integer limit,
            HttpServletRequest servletRequest
    ) {
        Long userId = resolveUserId(servletRequest);
        if (userId == null) {
            return Result.fail("未登录");
        }

        List<AiAgentConversation> rows = aiAgentConversationService.list(
                new LambdaQueryWrapper<AiAgentConversation>()
                        .eq(AiAgentConversation::getUserId, userId)
                        .eq(AiAgentConversation::getType, agentType)
                        .orderByDesc(AiAgentConversation::getIsTop)
                        .orderByDesc(AiAgentConversation::getCreateTime)
                        .last("LIMIT " + limit)
        );

        List<AiConversationHistoryResponse> out = new ArrayList<>();
        for (AiAgentConversation r : rows) {
            AiConversationHistoryResponse dto = new AiConversationHistoryResponse();
            dto.setType(r.getType());
            dto.setConversationId(r.getId());
            dto.setTitle(r.getTitle());
            dto.setIsTop(r.getIsTop());
            dto.setCreateTime(r.getCreateTime());
            fillPreview(r, dto);
            out.add(dto);
        }
        return Result.ok(out);
    }

    @GetMapping("/conversations/{id}/messages")
    @Operation(summary = "会话消息列表")
    public Result<List<AiAgentMessageResponse>> conversationMessages(
            @PathVariable Long id,
            HttpServletRequest servletRequest
    ) {
        Long userId = resolveUserId(servletRequest);
        if (userId == null) {
            return Result.fail("未登录");
        }
        requireOwnedConversation(userId, id);
        List<AiAgentMessage> list = aiAgentMessageService.list(
                new LambdaQueryWrapper<AiAgentMessage>()
                        .eq(AiAgentMessage::getConversationId, id)
                        .orderByAsc(AiAgentMessage::getId)
        );
        return Result.ok(list.stream().map(m -> {
            AiAgentMessageResponse d = new AiAgentMessageResponse();
            d.setRole(m.getRole());
            d.setContent(m.getContent());
            d.setCreateTime(m.getCreateTime());
            return d;
        }).collect(Collectors.toList()));
    }

    @PutMapping("/conversations/{id}/top")
    @Operation(summary = "会话置顶")
    public Result<Void> setConversationTop(
            @PathVariable Long id,
            @RequestBody AiConversationTopRequest body,
            HttpServletRequest servletRequest
    ) {
        Long userId = resolveUserId(servletRequest);
        if (userId == null) {
            return Result.fail("未登录");
        }
        AiAgentConversation c = requireOwnedConversation(userId, id);
        c.setIsTop(Boolean.TRUE.equals(body.getIsTop()));
        aiAgentConversationService.updateById(c);
        return Result.ok();
    }

    @DeleteMapping("/conversations/{id}")
    @Operation(summary = "删除会话")
    public Result<Void> deleteConversation(@PathVariable Long id, HttpServletRequest servletRequest) {
        Long userId = resolveUserId(servletRequest);
        if (userId == null) {
            return Result.fail("未登录");
        }
        requireOwnedConversation(userId, id);
        aiAgentMessageService.remove(
                new LambdaQueryWrapper<AiAgentMessage>().eq(AiAgentMessage::getConversationId, id)
        );
        aiAgentConversationService.removeById(id);
        return Result.ok();
    }
}
