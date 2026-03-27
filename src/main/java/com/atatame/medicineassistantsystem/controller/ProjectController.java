package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.ai.AgentCode;
import com.atatame.medicineassistantsystem.common.Result;
import com.atatame.medicineassistantsystem.model.dto.request.AiTaskRequest;
import com.atatame.medicineassistantsystem.model.dto.request.DeleteByIdRequest;
import com.atatame.medicineassistantsystem.model.dto.response.AiTaskResponse;
import com.atatame.medicineassistantsystem.model.entity.Project;
import com.atatame.medicineassistantsystem.model.entity.ProjectDecision;
import com.atatame.medicineassistantsystem.model.entity.ProjectDocument;
import com.atatame.medicineassistantsystem.model.entity.ProjectMember;
import com.atatame.medicineassistantsystem.service.IAiAgentService;
import com.atatame.medicineassistantsystem.service.IProjectDecisionService;
import com.atatame.medicineassistantsystem.service.IProjectDocumentService;
import com.atatame.medicineassistantsystem.service.IProjectMemberService;
import com.atatame.medicineassistantsystem.service.IProjectService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "项目中心", description = "项目管理全流程接口")
public class ProjectController {

    private final IProjectService projectService;
    private final IProjectDecisionService projectDecisionService;
    private final IProjectDocumentService projectDocumentService;
    private final IProjectMemberService projectMemberService;
    private final IAiAgentService aiAgentService;

    @GetMapping
    @Operation(summary = "项目列表")
    public Result<List<Project>> list(@RequestParam(required = false) String status) {
        LambdaQueryWrapper<Project> q = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) {
            q.eq(Project::getStatus, status);
        }
        q.orderByDesc(Project::getCreateTime);
        return Result.ok(projectService.list(q));
    }

    @PostMapping("/create")
    @Operation(summary = "创建项目")
    public Result<Void> create(@RequestBody Project request) {
        projectService.save(request);
        return Result.ok();
    }

    @PostMapping("/update")
    @Operation(summary = "更新项目")
    public Result<Void> update(@RequestBody Project request) {
        projectService.updateById(request);
        return Result.ok();
    }

    @PostMapping("/delete")
    @Operation(summary = "删除项目")
    public Result<Void> delete(@RequestBody DeleteByIdRequest request) {
        projectService.removeById(request.getId());
        return Result.ok();
    }

    @GetMapping("/{projectId}/decisions")
    @Operation(summary = "决策记录列表")
    public Result<List<ProjectDecision>> decisions(@PathVariable Long projectId) {
        return Result.ok(projectDecisionService.list(new LambdaQueryWrapper<ProjectDecision>()
                .eq(ProjectDecision::getProjectId, projectId)
                .orderByDesc(ProjectDecision::getCreateTime)));
    }

    @PostMapping("/{projectId}/decisions/create")
    @Operation(summary = "新增决策记录")
    public Result<Void> createDecision(@PathVariable Long projectId, @RequestBody ProjectDecision request) {
        request.setProjectId(projectId);
        projectDecisionService.save(request);
        return Result.ok();
    }

    @GetMapping("/{projectId}/documents")
    @Operation(summary = "项目文档列表")
    public Result<List<ProjectDocument>> documents(@PathVariable Long projectId) {
        return Result.ok(projectDocumentService.list(new LambdaQueryWrapper<ProjectDocument>()
                .eq(ProjectDocument::getProjectId, projectId)
                .orderByDesc(ProjectDocument::getUploadTime)));
    }

    @PostMapping("/{projectId}/documents/create")
    @Operation(summary = "新增项目文档")
    public Result<Void> createDocument(@PathVariable Long projectId, @RequestBody ProjectDocument request) {
        request.setProjectId(projectId);
        projectDocumentService.save(request);
        return Result.ok();
    }

    @GetMapping("/{projectId}/members")
    @Operation(summary = "项目成员列表")
    public Result<List<ProjectMember>> members(@PathVariable Long projectId) {
        return Result.ok(projectMemberService.list(new LambdaQueryWrapper<ProjectMember>()
                .eq(ProjectMember::getProjectId, projectId)));
    }

    @PostMapping("/{projectId}/members/create")
    @Operation(summary = "新增项目成员")
    public Result<Void> createMember(@PathVariable Long projectId, @RequestBody ProjectMember request) {
        request.setProjectId(projectId);
        projectMemberService.save(request);
        return Result.ok();
    }

    @PostMapping("/{projectId}/ai/evaluate")
    @Operation(summary = "AI立项评估")
    public Result<AiTaskResponse> aiEvaluate(@PathVariable Long projectId, @RequestBody AiTaskRequest request) {
        return Result.ok(aiAgentService.run(AgentCode.PROJECT_EVALUATION, "项目ID:" + projectId + "\n" + request.getInput()));
    }

    @PostMapping("/{projectId}/ai/report")
    @Operation(summary = "AI报告生成")
    public Result<AiTaskResponse> aiReport(@PathVariable Long projectId, @RequestBody AiTaskRequest request) {
        return Result.ok(aiAgentService.run(AgentCode.REPORT_GENERATION, "项目ID:" + projectId + "\n" + request.getInput()));
    }
}

