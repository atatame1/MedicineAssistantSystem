package com.atatame.medicineassistantsystem.service;

import com.atatame.medicineassistantsystem.model.dto.response.ProjectMemberResponse;
import com.atatame.medicineassistantsystem.model.entity.ProjectMember;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 项目用户关联表 服务类
 * </p>
 *
 * @author author
 * @since 2026-03-26
 */
public interface IProjectMemberService extends IService<ProjectMember> {

    List<ProjectMemberResponse> listWithUserByProjectId(Long projectId);
}
