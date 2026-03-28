package com.atatame.medicineassistantsystem.service.impl;

import com.atatame.medicineassistantsystem.model.constants.UserCenterConstants;
import com.atatame.medicineassistantsystem.model.dto.request.FavoriteOperationRequest;
import com.atatame.medicineassistantsystem.model.dto.request.SettingsUpdateRequest;
import com.atatame.medicineassistantsystem.model.dto.response.DocumentResponse;
import com.atatame.medicineassistantsystem.model.dto.response.FavoriteResponse;
import com.atatame.medicineassistantsystem.model.dto.response.FavoriteStatisticsResponse;
import com.atatame.medicineassistantsystem.model.dto.response.ProjectResponse;
import com.atatame.medicineassistantsystem.model.dto.response.SettingsResponse;
import com.atatame.medicineassistantsystem.model.dto.response.TaskResponse;
import com.atatame.medicineassistantsystem.model.dto.response.UserProfileResponse;
import com.atatame.medicineassistantsystem.model.dto.response.UserStatisticsResponse;
import com.atatame.medicineassistantsystem.model.entity.Component;
import com.atatame.medicineassistantsystem.model.entity.Formula;
import com.atatame.medicineassistantsystem.model.entity.Herb;
import com.atatame.medicineassistantsystem.model.entity.Literature;
import com.atatame.medicineassistantsystem.model.entity.Patent;
import com.atatame.medicineassistantsystem.model.entity.Project;
import com.atatame.medicineassistantsystem.model.entity.ProjectDocument;
import com.atatame.medicineassistantsystem.model.entity.ProjectMember;
import com.atatame.medicineassistantsystem.model.entity.User;
import com.atatame.medicineassistantsystem.model.entity.UserFavorite;
import com.atatame.medicineassistantsystem.model.entity.UserSettings;
import com.atatame.medicineassistantsystem.model.entity.UserTask;
import com.atatame.medicineassistantsystem.mapper.UserMapper;
import com.atatame.medicineassistantsystem.service.IComponentService;
import com.atatame.medicineassistantsystem.service.IFormulaService;
import com.atatame.medicineassistantsystem.service.IHerbService;
import com.atatame.medicineassistantsystem.service.ILiteratureService;
import com.atatame.medicineassistantsystem.service.IPatentService;
import com.atatame.medicineassistantsystem.service.IProjectDocumentService;
import com.atatame.medicineassistantsystem.service.IProjectMemberService;
import com.atatame.medicineassistantsystem.service.IProjectService;
import com.atatame.medicineassistantsystem.service.IUserService;
import com.atatame.medicineassistantsystem.service.IUserFavoriteService;
import com.atatame.medicineassistantsystem.service.IUserSettingsService;
import com.atatame.medicineassistantsystem.service.IUserTaskService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
    private final IUserSettingsService userSettingsService;
    private final ILiteratureService literatureService;
    private final IPatentService patentService;
    private final IFormulaService formulaService;
    private final IHerbService herbService;
    private final IComponentService componentService;

    @Override
    public List<TaskResponse> myTasks(Long userId) {
        List<UserTask> tasks = userTaskService.list(new LambdaQueryWrapper<UserTask>()
                .and(w -> w.eq(UserTask::getUserId, userId).or().eq(UserTask::getAssigneeId, userId))
                .orderByAsc(UserTask::getStatus)
                .orderByAsc(UserTask::getPriority)
                .orderByAsc(UserTask::getDeadline)
                .orderByDesc(UserTask::getCreateTime));
        return mapTasksToResponses(tasks);
    }

    @Override
    public List<TaskResponse> myRecentTasks(Long userId, int limit) {
        List<UserTask> tasks = userTaskService.list(new LambdaQueryWrapper<UserTask>()
                .and(w -> w.eq(UserTask::getUserId, userId).or().eq(UserTask::getAssigneeId, userId))
                .orderByAsc(UserTask::getPriority)
                .orderByDesc(UserTask::getCreateTime)
                .last("LIMIT " + limit));
        return mapTasksToResponses(tasks);
    }

    private List<TaskResponse> mapTasksToResponses(List<UserTask> tasks) {
        if (tasks.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Long> pids = tasks.stream().map(UserTask::getProjectId).filter(Objects::nonNull).collect(Collectors.toCollection(HashSet::new));
        Map<Long, String> projectNames = new HashMap<>();
        if (!pids.isEmpty()) {
            projectService.listByIds(pids).forEach(p -> projectNames.put(p.getId(), p.getProjectName()));
        }
        return tasks.stream().map(t -> toTaskResponse(t, projectNames)).collect(Collectors.toList());
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
    public void addFavorite(Long userId, FavoriteOperationRequest request) {
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
    public void removeFavorite(Long userId, FavoriteOperationRequest request) {
        userFavoriteService.remove(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getFavoriteType,request.getFavoriteType())
                .eq(UserFavorite::getId, request.getFavoriteId())
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
        UserSettings row = userSettingsService.getById(userId);
        SettingsResponse response = new SettingsResponse();
        response.setUserId(userId);
        response.setPreferences(row != null && row.getPreferences() != null ? row.getPreferences() : "");
        return response;
    }

    @Override
    public void updateSettings(Long userId, SettingsUpdateRequest request) {
        String p = request.getPreferences() != null ? request.getPreferences() : "";
        UserSettings row = new UserSettings();
        row.setUserId(userId);
        row.setPreferences(p);
        userSettingsService.saveOrUpdate(row);
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
        response.setStatus(user.getStatus() == null ? null : String.valueOf(user.getStatus()));
        response.setStatistics(buildUserStatistics(userId));
        return response;
    }

    private UserStatisticsResponse buildUserStatistics(Long userId) {
        UserStatisticsResponse statistics = new UserStatisticsResponse();
        statistics.setTotalTasks(userTaskService.count(new LambdaQueryWrapper<UserTask>()
                .and(w -> w.eq(UserTask::getUserId, userId).or().eq(UserTask::getAssigneeId, userId))));
        statistics.setPendingTasks(userTaskService.count(new LambdaQueryWrapper<UserTask>()
                .and(w -> w.eq(UserTask::getUserId, userId).or().eq(UserTask::getAssigneeId, userId))
                .eq(UserTask::getStatus, 0)));
        statistics.setInProgressTasks(userTaskService.count(new LambdaQueryWrapper<UserTask>()
                .and(w -> w.eq(UserTask::getUserId, userId).or().eq(UserTask::getAssigneeId, userId))
                .eq(UserTask::getStatus, 1)));
        statistics.setCompletedTasks(userTaskService.count(new LambdaQueryWrapper<UserTask>()
                .and(w -> w.eq(UserTask::getUserId, userId).or().eq(UserTask::getAssigneeId, userId))
                .eq(UserTask::getStatus, 2)));
        List<ProjectResponse> projects = myProjects(userId);
        statistics.setTotalProjects((long) projects.size());
        statistics.setActiveProjects(projects.stream()
                .filter(p -> p.getStatus() != null && (p.getStatus() == 1 || p.getStatus() == 2))
                .count());
        statistics.setTotalFavorites(userFavoriteService.count(new LambdaQueryWrapper<UserFavorite>().eq(UserFavorite::getUserId, userId)));
        statistics.setTotalDocuments((long) myReports(userId).size());
        return statistics;
    }

    private TaskResponse toTaskResponse(UserTask task, Map<Long, String> projectNames) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setPriority(task.getPriority());
        response.setStatus(task.getStatus());
        response.setProjectId(task.getProjectId());
        response.setProjectName(task.getProjectId() != null ? projectNames.get(task.getProjectId()) : null);
        response.setAssigneeId(task.getAssigneeId());
        response.setCompletionFeedback(task.getCompletionFeedback());
        response.setDeadline(task.getDeadline());
        response.setCreateTime(task.getCreateTime());
        boolean done = task.getStatus() != null && task.getStatus() == 2;
        response.setOverdue(task.getDeadline() != null && LocalDateTime.now().isAfter(task.getDeadline()) && !done);
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
        response.setCollectTime(favorite.getCreateTime());
        return response;
    }

    private String resolveFavoriteEntityName(String favoriteType, Long favoriteId) {
        if (UserCenterConstants.EntityType.TASK.equals(favoriteType)) {
            UserTask task = userTaskService.getById(favoriteId);
            return task == null ? null : task.getTitle();
        }
        if (UserCenterConstants.EntityType.PROJECT.equals(favoriteType)) {
            Project project = projectService.getById(favoriteId);
            return project == null ? null : project.getProjectName();
        }
        if (UserCenterConstants.EntityType.DOCUMENT.equals(favoriteType)) {
            ProjectDocument document = projectDocumentService.getById(favoriteId);
            return document == null ? null : document.getDocName();
        }
        if (UserCenterConstants.EntityType.LITERATURE.equals(favoriteType)) {
            Literature literature = literatureService.getById(favoriteId);
            return literature == null ? null : literature.getTitle();
        }
        if (UserCenterConstants.EntityType.PATENT.equals(favoriteType)) {
            Patent patent = patentService.getById(favoriteId);
            return patent == null ? null : patent.getName();
        }
        if (UserCenterConstants.EntityType.FORMULA.equals(favoriteType)) {
            Formula formula = formulaService.getById(favoriteId);
            return formula == null ? null : formula.getName();
        }
        if (UserCenterConstants.EntityType.HERB.equals(favoriteType)) {
            Herb herb = herbService.getById(favoriteId);
            return herb == null ? null : herb.getName();
        }
        if (UserCenterConstants.EntityType.COMPONENT.equals(favoriteType)) {
            Component component = componentService.getById(favoriteId);
            return component == null ? null : component.getName();
        }
        return favoriteType + "-" + favoriteId;
    }

}
