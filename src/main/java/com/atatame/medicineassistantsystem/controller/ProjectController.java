package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.ai.ProjectEvaluationAgent;
import com.atatame.medicineassistantsystem.ai.ReportGenerationAgent;
import com.atatame.medicineassistantsystem.common.Result;
import com.atatame.medicineassistantsystem.exception.BusinessException;
import com.atatame.medicineassistantsystem.model.dto.request.AiTaskRequest;
import com.atatame.medicineassistantsystem.model.dto.request.DeleteByIdRequest;
import com.atatame.medicineassistantsystem.model.dto.request.MemberCreateRequest;
import com.atatame.medicineassistantsystem.model.dto.request.ProjectDocumentUploadRequest;
import com.atatame.medicineassistantsystem.model.dto.request.ProjectEstablishmentDraftRequest;
import com.atatame.medicineassistantsystem.model.dto.response.DecisionCompareResponse;
import com.atatame.medicineassistantsystem.model.dto.response.DocumentResponse;
import com.atatame.medicineassistantsystem.model.dto.response.ProjectBoardResponse;
import com.atatame.medicineassistantsystem.model.entity.Project;
import com.atatame.medicineassistantsystem.model.entity.ProjectDecision;
import com.atatame.medicineassistantsystem.model.entity.ProjectDocument;
import com.atatame.medicineassistantsystem.model.entity.ProjectMember;
import com.atatame.medicineassistantsystem.service.IProjectDecisionService;
import com.atatame.medicineassistantsystem.service.IProjectDocumentService;
import com.atatame.medicineassistantsystem.service.IProjectMemberService;
import com.atatame.medicineassistantsystem.service.IProjectService;
import com.atatame.medicineassistantsystem.utils.FileStorageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "项目中心", description = "项目管理全流程接口")
public class ProjectController {

    private final IProjectService projectService;
    private final IProjectDecisionService projectDecisionService;
    private final IProjectDocumentService projectDocumentService;
    private final IProjectMemberService projectMemberService;
    private final ProjectEvaluationAgent projectEvaluationAgent;
    private final ReportGenerationAgent reportGenerationAgent;
    private final FileStorageUtil fileStorageUtil;

    @GetMapping
    @Operation(summary = "项目列表")
    public Result<List<Project>> list(@RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Project> q = new LambdaQueryWrapper<>();
        if (status != null) {
            q.eq(Project::getStatus, status);
        }
        q.orderByDesc(Project::getCreateTime);
        return Result.ok(projectService.list(q));
    }

    @GetMapping("/{projectId:\\d+}")
    @Operation(summary = "项目详情（按项目ID）")
    public Result<Project> detail(@PathVariable Long projectId) {
        Project p = projectService.getById(projectId);
        if (p == null) {
            return Result.fail("项目不存在");
        }
        return Result.ok(p);
    }

    @PostMapping("/create")
    @Operation(summary = "创建项目（提交表单含 aiAssess；保存后异步生成立项报告写入 aiReport）")
    public Result<Void> create(@RequestBody Project request) {
        projectService.save(request);
        return Result.ok();
    }

    @PostMapping("/update")
    @Operation(summary = "更新项目")
    public Result<Void> update(@RequestBody Project request) {
        projectService.updateById(request);
        return Result.ok();
    }

    @PostMapping("/delete")
    @Operation(summary = "删除项目")
    public Result<Void> delete(@RequestBody DeleteByIdRequest request) {
        projectService.removeById(request.getId());
        return Result.ok();
    }

    @GetMapping("/documents/{docId:\\d+}")
    @Operation(summary = "项目文档详情（按文档ID）")
    public Result<DocumentResponse> documentDetail(@PathVariable Long docId) {
        ProjectDocument doc = projectDocumentService.getById(docId);
        if (doc == null) {
            return Result.fail("文档不存在");
        }
        Project p = doc.getProjectId() != null ? projectService.getById(doc.getProjectId()) : null;
        DocumentResponse r = new DocumentResponse();
        r.setId(doc.getId());
        r.setProjectId(doc.getProjectId());
        r.setProjectName(p == null ? null : p.getProjectName());
        r.setDocType(doc.getDocType());
        r.setDocName(doc.getDocName());
        r.setStorageKey(doc.getStorageKey());
        r.setFileSize(doc.getFileSize());
        r.setFileType(doc.getFileType());
        r.setUploadTime(doc.getUploadTime());
        r.setTags(doc.getTags());
        r.setSummary(doc.getSummary());
        return Result.ok(r);
    }

    @PostMapping(value = "/draft/evaluate-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "立项评估流式输出（不落库）")
    public SseEmitter draftEvaluateStream(@RequestBody ProjectEstablishmentDraftRequest req) {
        SseEmitter emitter = new SseEmitter(600_000L);
        String userText = buildDraftPrompt(req);
        projectEvaluationAgent.stream(userText)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        chunk -> {
                            try {
                                emitter.send(SseEmitter.event().data(chunk));
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        emitter::completeWithError,
                        emitter::complete
                );
        return emitter;
    }

    @GetMapping("/{projectId}/board")
    @Operation(summary = "项目看板")
    public Result<ProjectBoardResponse> board(@PathVariable Long projectId) {
        ProjectBoardResponse b = projectService.getBoard(projectId);
        if (b == null) {
            return Result.fail("项目不存在");
        }
        return Result.ok(b);
    }

    @GetMapping("/{projectId}/decisions")
    @Operation(summary = "决策记录列表")
    public Result<List<ProjectDecision>> decisions(@PathVariable Long projectId) {
        return Result.ok(projectDecisionService.list(new LambdaQueryWrapper<ProjectDecision>()
                .eq(ProjectDecision::getProjectId, projectId)
                .orderByDesc(ProjectDecision::getCreateTime)));
    }

    @GetMapping("/{projectId}/decisions/compare")
    @Operation(summary = "决策版本对比")
    public Result<DecisionCompareResponse> compareDecisions(@PathVariable Long projectId,
                                                            @RequestParam Long id1,
                                                            @RequestParam Long id2) {
        DecisionCompareResponse r = projectDecisionService.compare(id1, id2);
        if (!r.getFirst().getProjectId().equals(projectId)) {
            throw new BusinessException("决策不属于该项目");
        }
        return Result.ok(r);
    }

    @PostMapping("/{projectId}/decisions/create")
    @Operation(summary = "新增决策记录")
    public Result<Void> createDecision(@PathVariable Long projectId, @RequestBody ProjectDecision request) {
        request.setProjectId(projectId);
        projectDecisionService.save(request);
        return Result.ok();
    }

    @GetMapping("/{projectId}/documents")
    @Operation(summary = "项目文档列表")
    public Result<List<ProjectDocument>> documents(@PathVariable Long projectId) {
        return Result.ok(projectDocumentService.list(new LambdaQueryWrapper<ProjectDocument>()
                .eq(ProjectDocument::getProjectId, projectId)
                .orderByDesc(ProjectDocument::getUploadTime)));
    }

    @GetMapping("/{projectId}/documents/search")
    @Operation(summary = "文档全文检索（名称/摘要/抽取正文）")
    public Result<List<ProjectDocument>> searchDocuments(@PathVariable Long projectId,
                                                         @RequestParam String q) {
        return Result.ok(projectDocumentService.searchKeyword(projectId, q));
    }

    @PostMapping(value = "/{projectId}/documents/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传项目文档")
    public Result<ProjectDocument> uploadDocument(@PathVariable Long projectId,
                                                  @RequestPart("file") MultipartFile file,
                                                  @RequestPart("metadata") ProjectDocumentUploadRequest metadata) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件为空");
        }
        ProjectDocument d = projectDocumentService.upload(projectId, file, metadata);
        return Result.ok(d);
    }

    @GetMapping("/{projectId}/documents/{docId}/file")
    @Operation(summary = "下载文档文件")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long projectId, @PathVariable Long docId) throws IOException {
        ProjectDocument doc = projectDocumentService.getById(docId);
        if (doc == null || !doc.getProjectId().equals(projectId)) {
            throw new BusinessException("文档不存在");
        }
        if (!StringUtils.hasText(doc.getStorageKey())) {
            throw new BusinessException("无服务器文件");
        }
        Path path = fileStorageUtil.resolvePath(doc.getStorageKey());
        if (!Files.exists(path)) {
            throw new BusinessException("文件已丢失");
        }
        Resource resource = new FileSystemResource(path.toFile());
        String fn = doc.getOriginalFilename() != null ? doc.getOriginalFilename() : doc.getDocName();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + URLEncoder.encode(fn, StandardCharsets.UTF_8) + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/{projectId}/members")
    @Operation(summary = "项目成员列表")
    public Result<List<ProjectMember>> members(@PathVariable Long projectId) {
        return Result.ok(projectMemberService.list(new LambdaQueryWrapper<ProjectMember>()
                .eq(ProjectMember::getProjectId, projectId)));
    }

    @PostMapping("/{projectId}/members/create")
    @Operation(summary = "新增项目成员")
    public Result<Void> createMember(@PathVariable Long projectId, @RequestBody MemberCreateRequest request) {

        ProjectMember projectMember = new ProjectMember();
        projectMember.setProjectId(projectId);
        projectMember.setRole(request.getRole());
        projectMember.setUserId(request.getUserId());
        projectMember.setJoinTime(request.getJoinTime()!=null?request.getJoinTime():LocalDateTime.now());
        projectMemberService.save(projectMember);
        return Result.ok();
    }

    @PostMapping(value = "/{projectId}/ai/report-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "立项/阶段报告流式生成")
    public SseEmitter reportStream(@PathVariable Long projectId, @RequestBody(required = false) AiTaskRequest extra) {
        Project p = projectService.getById(projectId);
        if (p == null) {
            throw new BusinessException("项目不存在");
        }
        String ctx = buildProjectContext(p);
        String extraIn = extra != null && extra.getInput() != null ? extra.getInput() : "";
        String input = ctx + "\n补充说明:\n" + extraIn;
        SseEmitter emitter = new SseEmitter(600_000L);
        reportGenerationAgent.stream(input)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        chunk -> {
                            try {
                                emitter.send(SseEmitter.event().data(chunk));
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        emitter::completeWithError,
                        emitter::complete
                );
        return emitter;
    }

    private static String buildDraftPrompt(ProjectEstablishmentDraftRequest req) {
        StringBuilder sb = new StringBuilder();
        sb.append("项目名称:").append(nullToEmpty(req.getProjectName())).append("\n");
        sb.append("药材:").append(nullToEmpty(req.getHerbName())).append("\n");
        sb.append("方剂:").append(nullToEmpty(req.getFormulaName())).append("\n");
        sb.append("适应症:").append(nullToEmpty(req.getIndication())).append("\n");
        sb.append("描述:").append(nullToEmpty(req.getDescription())).append("\n");
        sb.append("预算:").append(req.getBudget() != null ? req.getBudget() : "").append("\n");
        sb.append("优先级:").append(nullToEmpty(req.getPriority())).append("\n");
        return sb.toString();
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static String buildProjectContext(Project p) {
        StringBuilder sb = new StringBuilder();
        sb.append("项目ID:").append(p.getId()).append("\n");
        sb.append("名称:").append(nullToEmpty(p.getProjectName())).append("\n");
        sb.append("药材:").append(nullToEmpty(p.getHerbName())).append("\n");
        sb.append("方剂:").append(nullToEmpty(p.getFormulaName())).append("\n");
        sb.append("适应症:").append(nullToEmpty(p.getIndication())).append("\n");
        sb.append("阶段:").append(nullToEmpty(p.getPhase())).append("\n");
        sb.append("描述:").append(nullToEmpty(p.getDescription())).append("\n");
        sb.append("立项评估(aiAssess):\n").append(nullToEmpty(p.getAiAssess())).append("\n");
        sb.append("已有立项报告(aiReport):\n").append(nullToEmpty(p.getAiReport())).append("\n");
        return sb.toString();
    }
}
