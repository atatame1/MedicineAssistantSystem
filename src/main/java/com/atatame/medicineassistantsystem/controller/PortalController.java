package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.common.Result;
import com.atatame.medicineassistantsystem.model.dto.response.PortalOverviewResponse;
import com.atatame.medicineassistantsystem.model.entity.Project;
import com.atatame.medicineassistantsystem.model.entity.UserTask;
import com.atatame.medicineassistantsystem.service.IProjectService;
import com.atatame.medicineassistantsystem.service.IUserTaskService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/portal")
@RequiredArgsConstructor
@Tag(name = "智能门户", description = "统一入口与看板")
public class PortalController {

    private final IUserTaskService userTaskService;
    private final IProjectService projectService;

    @GetMapping("/overview")
    @Operation(summary = "门户总览")
    public Result<PortalOverviewResponse> overview(@RequestParam Long userId) {
        List<UserTask> tasks = userTaskService.list(new LambdaQueryWrapper<UserTask>()
                .eq(UserTask::getUserId, userId)
                .orderByAsc(UserTask::getPriority)
                .last("limit 10"));
        List<Project> projects = projectService.list(new LambdaQueryWrapper<Project>()
                .eq(Project::getProjectorId, userId)
                .orderByDesc(Project::getUpdateTime)
                .last("limit 10"));
        PortalOverviewResponse response = new PortalOverviewResponse();
        response.setTodayTasks(tasks);
        response.setMyProjects(projects);
        response.setRiskWarnings(projects.stream().filter(p -> "HIGH".equals(p.getPriority())).count());
        return Result.ok(response);
    }
}

