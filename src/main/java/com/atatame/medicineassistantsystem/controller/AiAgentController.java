package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.ai.AgentCode;
import com.atatame.medicineassistantsystem.common.Result;
import com.atatame.medicineassistantsystem.auth.AuthInterceptor;
import com.atatame.medicineassistantsystem.exception.BusinessException;
import com.atatame.medicineassistantsystem.model.dto.request.AiTaskRequest;
import com.atatame.medicineassistantsystem.model.dto.response.AiConversationHistoryResponse;
import com.atatame.medicineassistantsystem.model.entity.AiAgentConversation;
import com.atatame.medicineassistantsystem.service.IAiAgentService;
import com.atatame.medicineassistantsystem.service.IAiAgentConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "智能体中心", description = "各研发功能独立智能体")
public class AiAgentController {

    private final IAiAgentService aiAgentService;
    private final IAiAgentConversationService aiAgentConversationService;

    private Long resolveUserId(HttpServletRequest request) {
        Object v = request.getAttribute(AuthInterceptor.ATTR_USER_ID);
        if (v == null) return null;
        if (v instanceof Long) return (Long) v;
        return null;
    }

    private static boolean memoryEnabled(AgentCode code) {
        return code != AgentCode.PROJECT_EVALUATION && code != AgentCode.REPORT_GENERATION;
    }

    private static String buildTitle(String input) {
        if (input == null) return null;
        String v = input.trim();
        if (v.isEmpty()) return null;
        return v.length() <= 12 ? v : v.substring(0, 12);
    }

    private Long ensureConversationId(HttpServletRequest servletRequest, AgentCode code, AiTaskRequest request) {
        if (!memoryEnabled(code)) return null;
        Long userId = resolveUserId(servletRequest);
        if (userId == null) throw new BusinessException("未登录");
        Long conversationId = request.getConversationId();
        if (conversationId == null) throw new BusinessException("conversationId必填");

        LambdaQueryWrapper<AiAgentConversation> q = new LambdaQueryWrapper<AiAgentConversation>()
                .eq(AiAgentConversation::getUserId, userId)
                .eq(AiAgentConversation::getType, code.name())
                .eq(AiAgentConversation::getConversationId, conversationId)
                .last("LIMIT 1");
        AiAgentConversation row = aiAgentConversationService.getOne(q);
        if (row == null) {
            row = new AiAgentConversation();
            row.setUserId(userId);
            row.setType(code.name());
            row.setConversationId(conversationId);
            row.setTitle(buildTitle(request.getInput()));
            row.setInputText(request.getInput());
            row.setOutputText(null);
            row.setMessagesJson(null);
            row.setCreateTime(LocalDateTime.now());
            aiAgentConversationService.save(row);
            return conversationId;
        }

        if (row.getTitle() == null || row.getTitle().isBlank()) {
            row.setTitle(buildTitle(request.getInput()));
        }
        row.setInputText(request.getInput());
        aiAgentConversationService.updateById(row);
        return conversationId;
    }

    private SseEmitter stream(AgentCode code, AiTaskRequest request, HttpServletRequest servletRequest) {
        Long conversationId = ensureConversationId(servletRequest, code, request);
        String input = request.getInput() == null ? "" : request.getInput();
        SseEmitter emitter = new SseEmitter(600_000L);
        aiAgentService.stream(code, input, conversationId)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        chunk -> {
                            try {
                                emitter.send(SseEmitter.event().data(chunk));
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
                        .orderByDesc(AiAgentConversation::getCreateTime)
                        .last("LIMIT " + limit)
        );

        return Result.ok(rows.stream().map(r -> {
            AiConversationHistoryResponse dto = new AiConversationHistoryResponse();
            dto.setType(r.getType());
            dto.setConversationId(r.getConversationId());
            dto.setTitle(r.getTitle());
            dto.setInputText(r.getInputText());
            dto.setOutputText(r.getOutputText());
            dto.setCreateTime(r.getCreateTime());
            return dto;
        }).collect(Collectors.toList()));
    }
}

