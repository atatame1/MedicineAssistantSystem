package com.atatame.medicineassistantsystem.service.impl;

import com.atatame.medicineassistantsystem.ai.AgentCode;
import com.atatame.medicineassistantsystem.exception.BusinessException;
import com.atatame.medicineassistantsystem.mapper.ProjectMapper;
import com.atatame.medicineassistantsystem.model.dto.response.ProjectBoardResponse;
import com.atatame.medicineassistantsystem.model.entity.Project;
import com.atatame.medicineassistantsystem.service.IAiAgentService;
import com.atatame.medicineassistantsystem.service.IProjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements IProjectService {

    private final IAiAgentService aiAgentService;

    @Override
    public boolean save(Project entity) {
        Long idBefore = entity.getId();
        if (idBefore == null && !StringUtils.hasText(entity.getAiAssess())) {
            throw new BusinessException("请填写立项 AI 评估 aiAssess");
        }
        boolean ok = super.save(entity);
        if (ok && idBefore == null && entity.getId() != null) {
            Long pid = entity.getId();
            new Thread(() -> generateEstablishmentReportAsync(pid), "establishment-report-" + pid).start();
        }
        return ok;
    }

    private void generateEstablishmentReportAsync(Long projectId) {
        try {
            Project p = getById(projectId);
            if (p == null) {
                return;
            }
            String input = buildEstablishmentReportPrompt(p);
            var resp = aiAgentService.run(AgentCode.REPORT_GENERATION, input);
            if (resp != null && StringUtils.hasText(resp.getOutput())) {
                Project u = new Project();
                u.setId(projectId);
                u.setAiReport(resp.getOutput());
                updateById(u);
            }
        } catch (Exception ignored) {
        }
    }

    private static String buildEstablishmentReportPrompt(Project p) {
        StringBuilder sb = new StringBuilder();
        sb.append("请生成立项报告（结构化、可归档）。\n");
        sb.append("项目名称:").append(nullSafe(p.getProjectName())).append("\n");
        sb.append("药材:").append(nullSafe(p.getHerbName())).append("\n");
        sb.append("方剂:").append(nullSafe(p.getFormulaName())).append("\n");
        sb.append("适应症:").append(nullSafe(p.getIndication())).append("\n");
        sb.append("阶段:").append(nullSafe(p.getPhase())).append("\n");
        sb.append("描述:").append(nullSafe(p.getDescription())).append("\n");
        sb.append("预算:").append(p.getBudget() != null ? p.getBudget() : "").append("\n");
        sb.append("优先级:").append(p.getPriority() != null ? p.getPriority() : "").append("\n");
        sb.append("立项评估:\n").append(nullSafe(p.getAiAssess())).append("\n");
        return sb.toString();
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s;
    }

    @Override
    public ProjectBoardResponse getBoard(Long projectId) {
        Project project = getById(projectId);
        if (project == null) {
            return null;
        }
        ProjectBoardResponse r = new ProjectBoardResponse();
        r.setProjectId(projectId);
        r.setCurrentPhase(project.getPhase());
        r.setStatus(project.getStatus());
        Integer st = project.getStatus();
        if (st != null && st >= 1 && st <= 4) {
            r.setProgressPercent((st - 1) * 25.0);
        } else {
            r.setProgressPercent(0.0);
        }
        return r;
    }
}
