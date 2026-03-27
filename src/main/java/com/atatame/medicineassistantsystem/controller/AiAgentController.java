package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.ai.AgentCode;
import com.atatame.medicineassistantsystem.common.Result;
import com.atatame.medicineassistantsystem.model.dto.request.AiTaskRequest;
import com.atatame.medicineassistantsystem.model.dto.response.AiTaskResponse;
import com.atatame.medicineassistantsystem.service.IAiAgentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "智能体中心", description = "各研发功能独立智能体")
public class AiAgentController {

    private final IAiAgentService aiAgentService;

    @PostMapping("/project-evaluation")
    @Operation(summary = "立项评估智能体")
    public Result<AiTaskResponse> projectEvaluation(@RequestBody AiTaskRequest request) {
        return Result.ok(aiAgentService.run(AgentCode.PROJECT_EVALUATION, request.getInput()));
    }

    @PostMapping("/formula-compatibility")
    @Operation(summary = "配伍分析智能体")
    public Result<AiTaskResponse> formulaCompatibility(@RequestBody AiTaskRequest request) {
        return Result.ok(aiAgentService.run(AgentCode.FORMULA_COMPATIBILITY, request.getInput()));
    }

    @PostMapping("/mechanism-inference")
    @Operation(summary = "机制推演智能体")
    public Result<AiTaskResponse> mechanismInference(@RequestBody AiTaskRequest request) {
        return Result.ok(aiAgentService.run(AgentCode.MECHANISM_INFERENCE, request.getInput()));
    }

    @PostMapping("/target-prediction")
    @Operation(summary = "靶点预测智能体")
    public Result<AiTaskResponse> targetPrediction(@RequestBody AiTaskRequest request) {
        return Result.ok(aiAgentService.run(AgentCode.TARGET_PREDICTION, request.getInput()));
    }

    @PostMapping("/literature-analysis")
    @Operation(summary = "文献分析智能体")
    public Result<AiTaskResponse> literatureAnalysis(@RequestBody AiTaskRequest request) {
        return Result.ok(aiAgentService.run(AgentCode.LITERATURE_ANALYSIS, request.getInput()));
    }

    @PostMapping("/patent-analysis")
    @Operation(summary = "专利分析智能体")
    public Result<AiTaskResponse> patentAnalysis(@RequestBody AiTaskRequest request) {
        return Result.ok(aiAgentService.run(AgentCode.PATENT_ANALYSIS, request.getInput()));
    }

    @PostMapping("/experiment-design")
    @Operation(summary = "实验设计智能体")
    public Result<AiTaskResponse> experimentDesign(@RequestBody AiTaskRequest request) {
        return Result.ok(aiAgentService.run(AgentCode.EXPERIMENT_DESIGN, request.getInput()));
    }

    @PostMapping("/report-generation")
    @Operation(summary = "报告生成智能体")
    public Result<AiTaskResponse> reportGeneration(@RequestBody AiTaskRequest request) {
        return Result.ok(aiAgentService.run(AgentCode.REPORT_GENERATION, request.getInput()));
    }
}

