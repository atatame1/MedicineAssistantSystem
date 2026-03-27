package com.atatame.medicineassistantsystem.service.impl;

import com.atatame.medicineassistantsystem.model.constants.UserCenterConstants;
import com.atatame.medicineassistantsystem.model.dto.request.FavoriteCreateRequest;
import com.atatame.medicineassistantsystem.model.dto.request.SettingsUpdateRequest;
import com.atatame.medicineassistantsystem.model.dto.response.DocumentResponse;
import com.atatame.medicineassistantsystem.model.dto.response.FavoriteResponse;
import com.atatame.medicineassistantsystem.model.dto.response.FavoriteStatisticsResponse;
import com.atatame.medicineassistantsystem.model.dto.response.ProjectResponse;
import com.atatame.medicineassistantsystem.model.dto.response.SettingsResponse;
import com.atatame.medicineassistantsystem.model.dto.response.TaskResponse;
import com.atatame.medicineassistantsystem.model.dto.response.UserProfileResponse;
import com.atatame.medicineassistantsystem.model.dto.response.UserStatisticsResponse;
import com.atatame.medicineassistantsystem.model.entity.Project;
import com.atatame.medicineassistantsystem.model.entity.ProjectDocument;
import com.atatame.medicineassistantsystem.model.entity.ProjectMember;
import com.atatame.medicineassistantsystem.model.entity.User;
import com.atatame.medicineassistantsystem.model.entity.UserFavorite;
import com.atatame.medicineassistantsystem.model.entity.UserTask;
import com.atatame.medicineassistantsystem.mapper.UserMapper;
import com.atatame.medicineassistantsystem.service.IProjectDocumentService;
import com.atatame.medicineassistantsystem.service.IProjectMemberService;
import com.atatame.medicineassistantsystem.service.IProjectService;
import com.atatame.medicineassistantsystem.service.IUserService;
import com.atatame.medicineassistantsystem.service.IUserFavoriteService;
import com.atatame.medicineassistantsystem.service.IUserTaskService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统用户账户 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-03-26
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final IUserTaskService userTaskService;
    private final IProjectService projectService;
    private final IProjectMemberService projectMemberService;
    private final IProjectDocumentService projectDocumentService;
    private final IUserFavoriteService userFavoriteService;

    private static final Map<Long, SettingsResponse> USER_SETTINGS_CACHE = new ConcurrentHashMap<>();

    @Override
    public List<TaskResponse> myTasks(Long userId) {
        List<UserTask> tasks = userTaskService.list(new LambdaQueryWrapper<UserTask>()
                .eq(UserTask::getUserId, userId)
                .orderByAsc(UserTask::getStatus)
                .orderByAsc(UserTask::getDeadline)
                .orderByDesc(UserTask::getCreateTime));
        return tasks.stream().map(this::toTaskResponse).collect(Collectors.toList());
    }

    @Override
    public List<ProjectResponse> myProjects(Long userId) {
        List<Project> createdProjects = projectService.list(new LambdaQueryWrapper<Project>()
                .eq(Project::getProjectorId, userId));
        List<ProjectMember> joinedMembers = projectMemberService.list(new LambdaQueryWrapper<ProjectMember>()
                .eq(ProjectMember::getUserId, userId));
        List<Long> joinedProjectIds = joinedMembers.stream()
                .map(ProjectMember::getProjectId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        List<Project> joinedProjects = joinedProjectIds.isEmpty() ? Collections.emptyList() : projectService.listByIds(joinedProjectIds);

        Map<Long, Project> merged = new HashMap<>();
        for (Project item : createdProjects) {
            merged.put(item.getId(), item);
        }
        for (Project item : joinedProjects) {
            merged.put(item.getId(), item);
        }
        return merged.values().stream()
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .map(this::toProjectResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentResponse> myReports(Long userId) {
        List<ProjectResponse> projects = myProjects(userId);
        if (projects.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> projectIds = projects.stream().map(ProjectResponse::getId).collect(Collectors.toList());
        List<ProjectDocument> docs = projectDocumentService.list(new LambdaQueryWrapper<ProjectDocument>()
                .in(ProjectDocument::getProjectId, projectIds)
                .eq(ProjectDocument::getDocType, "PROJECT_REPORT")
                .orderByDesc(ProjectDocument::getUploadTime));
        Map<Long, String> projectNameMap = projects.stream()
                .collect(Collectors.toMap(ProjectResponse::getId, ProjectResponse::getProjectName, (a, b) -> a));
        return docs.stream().map(item -> toDocumentResponse(item, projectNameMap.get(item.getProjectId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<FavoriteResponse> myFavorites(Long userId) {
        List<UserFavorite> favorites = userFavoriteService.list(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .orderByDesc(UserFavorite::getId));
        return favorites.stream().map(this::toFavoriteResponse).collect(Collectors.toList());
    }

    @Override
    public void addFavorite(Long userId, FavoriteCreateRequest request) {
        long count = userFavoriteService.count(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getFavoriteId, request.getFavoriteId())
                .eq(UserFavorite::getFavoriteType, request.getFavoriteType()));
        if (count > 0) {
            return;
        }
        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setFavoriteId(request.getFavoriteId());
        favorite.setFavoriteType(request.getFavoriteType());
        userFavoriteService.save(favorite);
    }

    @Override
    public void removeFavorite(Long userId, Long favoriteRecordId) {
        userFavoriteService.remove(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getId, favoriteRecordId)
                .eq(UserFavorite::getUserId, userId));
    }

    @Override
    public FavoriteStatisticsResponse favoriteStatistics(Long userId) {
        List<UserFavorite> favorites = userFavoriteService.list(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId));
        FavoriteStatisticsResponse response = new FavoriteStatisticsResponse();
        response.setTotalCount((long) favorites.size());
        response.setTypeCountMap(favorites.stream()
                .collect(Collectors.groupingBy(UserFavorite::getFavoriteType, Collectors.counting())));
        return response;
    }

    @Override
    public SettingsResponse settings(Long userId) {
        return USER_SETTINGS_CACHE.computeIfAbsent(userId, this::buildDefaultSettings);
    }

    @Override
    public void updateSettings(Long userId, SettingsUpdateRequest request) {
        SettingsResponse response = new SettingsResponse();
        response.setUserId(userId);

        SettingsResponse.NotificationSettings ns = new SettingsResponse.NotificationSettings();
        ns.setEmailEnabled(request.getNotificationSettings().getEmailEnabled());
        ns.setInAppEnabled(request.getNotificationSettings().getInAppEnabled());
        ns.setTaskReminderEnabled(request.getNotificationSettings().getTaskReminderEnabled());
        ns.setProjectNotificationEnabled(request.getNotificationSettings().getProjectNotificationEnabled());
        response.setNotificationSettings(ns);

        SettingsResponse.ThemeSettings ts = new SettingsResponse.ThemeSettings();
        ts.setMode(request.getThemeSettings().getMode());
        ts.setAccentColor(request.getThemeSettings().getAccentColor());
        response.setThemeSettings(ts);

        SettingsResponse.PrivacySettings ps = new SettingsResponse.PrivacySettings();
        ps.setProfileVisibility(request.getPrivacySettings().getProfileVisibility());
        ps.setSearchEnabled(request.getPrivacySettings().getSearchEnabled());
        response.setPrivacySettings(ps);
        response.setOtherSettings(request.getOtherSettings());
        USER_SETTINGS_CACHE.put(userId, response);
    }

    @Override
    public UserProfileResponse profile(Long userId) {
        User user = getById(userId);
        UserProfileResponse response = new UserProfileResponse();
        response.setUserId(userId);
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setGender(user.getGender());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setStatus(user.getStatus());
        response.setStatistics(buildUserStatistics(userId));
        return response;
    }

    private UserStatisticsResponse buildUserStatistics(Long userId) {
        UserStatisticsResponse statistics = new UserStatisticsResponse();
        statistics.setTotalTasks(userTaskService.count(new LambdaQueryWrapper<UserTask>().eq(UserTask::getUserId, userId)));
        statistics.setPendingTasks(userTaskService.count(new LambdaQueryWrapper<UserTask>().eq(UserTask::getUserId, userId).eq(UserTask::getStatus, "PENDING")));
        statistics.setInProgressTasks(userTaskService.count(new LambdaQueryWrapper<UserTask>().eq(UserTask::getUserId, userId).eq(UserTask::getStatus, "IN_PROGRESS")));
        statistics.setCompletedTasks(userTaskService.count(new LambdaQueryWrapper<UserTask>().eq(UserTask::getUserId, userId).eq(UserTask::getStatus, "COMPLETED")));
        List<ProjectResponse> projects = myProjects(userId);
        statistics.setTotalProjects((long) projects.size());
        statistics.setActiveProjects(projects.stream()
                .filter(p -> "IN_PROGRESS".equals(p.getStatus()) || "PLANNING".equals(p.getStatus()))
                .count());
        statistics.setTotalFavorites(userFavoriteService.count(new LambdaQueryWrapper<UserFavorite>().eq(UserFavorite::getUserId, userId)));
        statistics.setTotalDocuments((long) myReports(userId).size());
        return statistics;
    }

    private TaskResponse toTaskResponse(UserTask task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setTaskType(task.getTaskType());
        response.setPriority(task.getPriority());
        response.setStatus(task.getStatus());
        response.setProjectId(task.getProjectId());
        response.setProjectName(task.getProjectName());
        response.setDeadline(task.getDeadline());
        response.setCreateTime(task.getCreateTime());
        response.setOverdue(task.getDeadline() != null && LocalDateTime.now().isAfter(task.getDeadline()) && !"COMPLETED".equals(task.getStatus()));
        response.setTags(parseTags(task.getTags()));
        return response;
    }

    private ProjectResponse toProjectResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setProjectName(project.getProjectName());
        response.setHerbName(project.getHerbName());
        response.setFormulaName(project.getFormulaName());
        response.setIndication(project.getIndication());
        response.setPhase(project.getPhase());
        response.setStatus(project.getStatus());
        response.setPriority(project.getPriority());
        response.setStartTime(project.getStartTime());
        response.setPlannedEndTime(project.getPlannedEndTime());
        response.setDescription(project.getDescription());
        response.setBudget(project.getBudget());
        response.setCreateTime(project.getCreateTime());
        response.setUpdateTime(project.getUpdateTime());
        return response;
    }

    private DocumentResponse toDocumentResponse(ProjectDocument doc, String projectName) {
        DocumentResponse response = new DocumentResponse();
        response.setId(doc.getId());
        response.setProjectId(doc.getProjectId());
        response.setProjectName(projectName);
        response.setDocType(doc.getDocType());
        response.setDocName(doc.getDocName());
        response.setFilePath(doc.getFilePath());
        response.setFileSize(doc.getFileSize());
        response.setFileType(doc.getFileType());
        response.setVersion(doc.getVersion());
        response.setUploadTime(doc.getUploadTime());
        response.setTags(doc.getTags());
        response.setSummary(doc.getSummary());
        return response;
    }

    private FavoriteResponse toFavoriteResponse(UserFavorite favorite) {
        FavoriteResponse response = new FavoriteResponse();
        response.setId(favorite.getId());
        response.setFavoriteId(favorite.getFavoriteId());
        response.setFavoriteType(favorite.getFavoriteType());
        response.setEntityName(resolveFavoriteEntityName(favorite.getFavoriteType(), favorite.getFavoriteId()));
        return response;
    }

    private String resolveFavoriteEntityName(String favoriteType, Long favoriteId) {
        if (UserCenterConstants.EntityType.PROJECT.equals(favoriteType)) {
            Project project = projectService.getById(favoriteId);
            return project == null ? null : project.getProjectName();
        }
        if (UserCenterConstants.EntityType.DOCUMENT.equals(favoriteType)) {
            ProjectDocument document = projectDocumentService.getById(favoriteId);
            return document == null ? null : document.getDocName();
        }
        return favoriteType + "-" + favoriteId;
    }

    private List<String> parseTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return new ArrayList<>();
        }
        String normalized = tags.trim();
        if (normalized.startsWith("[") && normalized.endsWith("]")) {
            normalized = normalized.substring(1, normalized.length() - 1).replace("\"", "");
        }
        List<String> result = new ArrayList<>();
        for (String item : normalized.split(",")) {
            if (!item.trim().isEmpty()) {
                result.add(item.trim());
            }
        }
        return result;
    }

    private SettingsResponse buildDefaultSettings(Long userId) {
        SettingsResponse response = new SettingsResponse();
        response.setUserId(userId);
        SettingsResponse.NotificationSettings ns = new SettingsResponse.NotificationSettings();
        ns.setEmailEnabled(true);
        ns.setInAppEnabled(true);
        ns.setTaskReminderEnabled(true);
        ns.setProjectNotificationEnabled(true);
        response.setNotificationSettings(ns);

        SettingsResponse.ThemeSettings ts = new SettingsResponse.ThemeSettings();
        ts.setMode("AUTO");
        ts.setAccentColor("#3B82F6");
        response.setThemeSettings(ts);

        SettingsResponse.PrivacySettings ps = new SettingsResponse.PrivacySettings();
        ps.setProfileVisibility("PRIVATE");
        ps.setSearchEnabled(false);
        response.setPrivacySettings(ps);
        response.setOtherSettings(new HashMap<>());
        return response;
    }
}
