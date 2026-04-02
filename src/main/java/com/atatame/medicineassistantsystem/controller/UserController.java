package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.common.Result;
import com.atatame.medicineassistantsystem.model.dto.request.FavoriteOperationRequest;
import com.atatame.medicineassistantsystem.model.dto.request.SettingsUpdateRequest;
import com.atatame.medicineassistantsystem.model.dto.response.DocumentResponse;
import com.atatame.medicineassistantsystem.model.dto.response.FavoriteResponse;
import com.atatame.medicineassistantsystem.model.dto.response.FavoriteStatisticsResponse;
import com.atatame.medicineassistantsystem.model.dto.response.ProjectResponse;
import com.atatame.medicineassistantsystem.model.dto.response.SettingsResponse;
import com.atatame.medicineassistantsystem.model.dto.response.TaskResponse;
import com.atatame.medicineassistantsystem.model.dto.response.UserProfileResponse;
import com.atatame.medicineassistantsystem.service.IUserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户中心控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户中心", description = "个人中心相关接口")
public class UserController {

    private final IUserService userService;

    @GetMapping("/{userId}/tasks")
    @Operation(summary = "我的任务", description = "获取当前用户的任务列表")
    public Result<List<TaskResponse>> myTasks(
            @Parameter(description = "用户 ID", required = true)
            @PathVariable Long userId
    ) {
        return Result.ok(userService.myTasks(userId));
    }

    @GetMapping("/{userId}/tasks/{taskId}")
    @Operation(summary = "任务详情", description = "获取当前用户某条任务详情")
    public Result<TaskResponse> taskDetail(
            @Parameter(description = "用户 ID", required = true)
            @PathVariable Long userId,
            @PathVariable Long taskId
    ) {
        return userService.myTasks(userId).stream()
                .filter(t -> t.getId() != null && t.getId().equals(taskId))
                .findFirst()
                .map(Result::ok)
                .orElse(Result.fail("任务不存在"));
    }

    @GetMapping("/{userId}/projects")
    @Operation(summary = "我的项目", description = "获取我创建或参与的项目")
    public Result<List<ProjectResponse>> myProjects(
            @Parameter(description = "用户 ID", required = true)
            @PathVariable Long userId
    ) {
        return Result.ok(userService.myProjects(userId));
    }

    @GetMapping("/{userId}/reports")
    @Operation(summary = "我的报告", description = "获取我相关项目下的报告文档")
    public Result<List<DocumentResponse>> myReports(
            @Parameter(description = "用户 ID", required = true)
            @PathVariable Long userId
    ) {
        return Result.ok(userService.myReports(userId));
    }

    @GetMapping("/{userId}/favorites")
    @Operation(summary = "我的收藏", description = "获取当前用户收藏列表")
    public Result<List<FavoriteResponse>> myFavorites(
            @Parameter(description = "用户 ID", required = true)
            @PathVariable Long userId
    ) {
        return Result.ok(userService.myFavorites(userId));
    }

    @PostMapping("/{userId}/favorites/add")
    @Operation(summary = "新增收藏", description = "新增当前用户收藏项")
    public Result<Void> addFavorite(
            @Parameter(description = "用户 ID", required = true)
            @PathVariable Long userId,
            @RequestBody FavoriteOperationRequest request
    ) {
        userService.addFavorite(userId, request);
        return Result.ok();
    }

    @PostMapping("/{userId}/favorites/remove")
    @Operation(summary = "取消收藏", description = "取消某一条收藏记录")
    public Result<Void> removeFavorite(
            @Parameter(description = "用户 ID", required = true)
            @PathVariable Long userId,
            @RequestBody FavoriteOperationRequest request
    ) {
        userService.removeFavorite(userId, request);
        return Result.ok();
    }

    @GetMapping("/{userId}/favorites/statistics")
    @Operation(summary = "收藏统计", description = "按类型统计我的收藏数量")
    public Result<FavoriteStatisticsResponse> favoriteStatistics(
            @Parameter(description = "用户 ID", required = true)
            @PathVariable Long userId
    ) {
        return Result.ok(userService.favoriteStatistics(userId));
    }

    @GetMapping("/{userId}/settings")
    @Operation(summary = "获取设置", description = "获取用户个性化设置")
    public Result<SettingsResponse> settings(
            @Parameter(description = "用户 ID", required = true)
            @PathVariable Long userId
    ) {
        return Result.ok(userService.settings(userId));
    }

    //前端须转义字符避免引号歧义
    @PostMapping("/{userId}/settings/update")
    @Operation(summary = "更新设置", description = "更新用户个性化设置")
    public Result<Void> updateSettings(
            @Parameter(description = "用户 ID", required = true)
            @PathVariable Long userId,
            @RequestBody SettingsUpdateRequest request
    ) {
        userService.updateSettings(userId, request);
        return Result.ok();
    }

    @GetMapping("/{userId}/profile")
    @Operation(summary = "我的资料", description = "获取用户资料和统计信息")
    public Result<UserProfileResponse> profile(
            @Parameter(description = "用户 ID", required = true)
            @PathVariable Long userId
    ) {
        return Result.ok(userService.profile(userId));
    }
}
