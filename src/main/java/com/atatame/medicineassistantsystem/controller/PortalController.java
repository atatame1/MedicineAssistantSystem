package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.common.Result;
import com.atatame.medicineassistantsystem.model.dto.response.PortalOverviewResponse;
import com.atatame.medicineassistantsystem.model.entity.Project;
import com.atatame.medicineassistantsystem.model.entity.UserAiDialogSummary;
import com.atatame.medicineassistantsystem.service.IProjectService;
import com.atatame.medicineassistantsystem.service.IUserService;
import com.atatame.medicineassistantsystem.service.IUserAiDialogSummaryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/portal")
@RequiredArgsConstructor
@Tag(name = "智能门户", description = "统一入口与看板")
public class PortalController {

    private final IUserService userService;
    private final IProjectService projectService;
    private final IUserAiDialogSummaryService userAiDialogSummaryService;

    @GetMapping("/overview")
    @Operation(summary = "门户总览")
    public Result<PortalOverviewResponse> overview(@RequestParam Long userId) {
        List<Project> projects = projectService.list(new LambdaQueryWrapper<Project>()
                .eq(Project::getProjectorId, userId)
                .orderByDesc(Project::getUpdateTime)
                .last("limit 10"));
        PortalOverviewResponse response = new PortalOverviewResponse();
        response.setTasks(userService.myRecentTasks(userId, 10).stream()
                .filter(t -> t.getStatus() == null || t.getStatus() != 2)
                .collect(Collectors.toList()));
        response.setMyProjects(projects);
        response.setRiskWarnings(projects.stream().filter(p -> p.getPriority() != null && p.getPriority() == 1).count());
        UserAiDialogSummary summary = userAiDialogSummaryService.getById(userId);
        response.setSummaryText(summary == null ? null : summary.getSummaryText());
        return Result.ok(response);
    }

    @PostMapping("/summarize")
    @Operation(summary = "手动生成近期AI对话总结")
    public Result<String> summarize(@RequestParam Long userId) {
        userAiDialogSummaryService.summarizeUser(userId);
        UserAiDialogSummary s = userAiDialogSummaryService.getById(userId);
        return Result.ok(s == null ? null : s.getSummaryText());
    }
}
