package com.atatame.medicineassistantsystem.service;

import com.atatame.medicineassistantsystem.model.dto.response.ProjectBoardResponse;
import com.atatame.medicineassistantsystem.model.entity.Project;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IProjectService extends IService<Project> {

    ProjectBoardResponse getBoard(Long projectId);
}
