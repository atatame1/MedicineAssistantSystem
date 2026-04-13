package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.common.Result;
import com.atatame.medicineassistantsystem.model.dto.request.UserTaskCreateRequest;
import com.atatame.medicineassistantsystem.model.dto.response.TaskResponse;
import com.atatame.medicineassistantsystem.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "任务管理", description = "任务发布与管理")
public class UserTaskController {

    private final IUserService userService;

    @GetMapping("/{taskId}")
    @Operation(summary = "任务详情", description = "按任务ID查询任务详情")
    public Result<TaskResponse> taskDetail(@PathVariable Long taskId) {
        TaskResponse r = userService.taskDetailById(taskId);
        return r == null ? Result.fail("任务不存在") : Result.ok(r);
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "项目任务列表", description = "按项目查看任务")
    public Result<List<TaskResponse>> listProjectTasks(
            @Parameter(description = "项目 ID", required = true)
            @PathVariable Long projectId
    ) {
        return Result.ok(userService.listProjectTasks(projectId));
    }

    @PostMapping("/create")
    @Operation(summary = "发布任务", description = "发布项目任务（指派人默认当前用户）")
    public Result<Long> createTask(
            @RequestParam Long assigneeId,
            @RequestBody UserTaskCreateRequest request
    ) {
        return Result.ok(userService.createTask(assigneeId, request));
    }

    @PostMapping("/{taskId}/withdraw")
    @Operation(summary = "撤回任务", description = "指派人撤回任务")
    public Result<Void> withdrawTask(
            @PathVariable Long taskId,
            @RequestParam Long assigneeId
    ) {
        userService.withdrawTask(assigneeId, taskId);
        return Result.ok();
    }
}

