package com.atatame.medicineassistantsystem.service;

import com.atatame.medicineassistantsystem.model.dto.request.FavoriteOperationRequest;
import com.atatame.medicineassistantsystem.model.dto.request.SettingsUpdateRequest;
import com.atatame.medicineassistantsystem.model.dto.response.DocumentResponse;
import com.atatame.medicineassistantsystem.model.dto.response.FavoriteResponse;
import com.atatame.medicineassistantsystem.model.dto.response.FavoriteStatisticsResponse;
import com.atatame.medicineassistantsystem.model.dto.response.MyProjectItemResponse;
import com.atatame.medicineassistantsystem.model.dto.response.ProjectResponse;
import com.atatame.medicineassistantsystem.model.dto.response.UserListItemResponse;
import com.atatame.medicineassistantsystem.model.dto.response.SettingsResponse;
import com.atatame.medicineassistantsystem.model.dto.response.TaskResponse;
import com.atatame.medicineassistantsystem.model.dto.response.UserProfileResponse;
import com.atatame.medicineassistantsystem.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * <p>
 * 系统用户账户 服务类
 * </p>
 *
 * @author author
 * @since 2026-03-26
 */
public interface IUserService extends IService<User> {
    List<TaskResponse> myTasks(Long userId);
    List<TaskResponse> myRecentTasks(Long userId, int limit);
    List<ProjectResponse> myProjects(Long userId);
    List<MyProjectItemResponse> myProjectsForCurrentUser(Long userId, Integer status);
    List<DocumentResponse> myReports(Long userId);
    List<FavoriteResponse> myFavorites(Long userId);
    void addFavorite(Long userId, FavoriteOperationRequest request);
    void removeFavorite(Long userId, FavoriteOperationRequest request);
    FavoriteStatisticsResponse favoriteStatistics(Long userId);
    SettingsResponse settings(Long userId);
    void updateSettings(Long userId, SettingsUpdateRequest request);
    UserProfileResponse profile(Long userId);
    List<UserListItemResponse> listAllUsers();
}
