package com.atatame.medicineassistantsystem.mapper;

import com.atatame.medicineassistantsystem.model.dto.response.ProjectMemberResponse;
import com.atatame.medicineassistantsystem.model.entity.ProjectMember;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 项目用户关联表 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2026-03-26
 */
public interface ProjectMemberMapper extends BaseMapper<ProjectMember> {

    List<ProjectMemberResponse> selectListWithUserByProjectId(@Param("projectId") Long projectId);
}
