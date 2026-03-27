package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.common.Result;
import com.atatame.medicineassistantsystem.model.dto.DocumentDTO;
import com.atatame.medicineassistantsystem.model.dto.FavoriteDTO;
import com.atatame.medicineassistantsystem.model.dto.FavoriteStatisticsDTO;
import com.atatame.medicineassistantsystem.model.dto.ProjectDTO;
import com.atatame.medicineassistantsystem.model.dto.SettingsDTO;
import com.atatame.medicineassistantsystem.model.dto.TaskDTO;
import com.atatame.medicineassistantsystem.model.dto.UserProfileDTO;
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


}
