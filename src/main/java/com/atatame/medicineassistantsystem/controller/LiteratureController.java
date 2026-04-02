package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.common.Result;
import com.atatame.medicineassistantsystem.exception.BusinessException;
import com.atatame.medicineassistantsystem.model.dto.request.DeleteByIdRequest;
import com.atatame.medicineassistantsystem.model.dto.response.LiteratureRefreshResponse;
import com.atatame.medicineassistantsystem.model.entity.Literature;
import com.atatame.medicineassistantsystem.service.ILiteratureService;
import com.atatame.medicineassistantsystem.utils.FileStorageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;

@RestController
@RequestMapping("/api/literatures")
@RequiredArgsConstructor
@Tag(name = "文献库", description = "文献库相关接口")
public class LiteratureController {

    private final ILiteratureService literatureService;
    private final FileStorageUtil fileStorageUtil;

    @GetMapping
    @Operation(summary = "文献检索")
    public Result<List<Literature>> list(@RequestParam(required = false) String keyword) {
        return Result.ok(literatureService.listByKeyword(keyword));
    }

    @GetMapping("/{literatureId:\\d+}")
    @Operation(summary = "文献详情（按ID）")
    public Result<Literature> detail(@PathVariable Long literatureId) {
        Literature l = literatureService.getById(literatureId);
        if (l == null) {
            return Result.fail("文献不存在");
        }
        return Result.ok(l);
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "新增文献")
    public Result<Void> create(@RequestPart(value = "file",required = false) MultipartFile file,
                                 @RequestPart("metadata") Literature request) throws Exception {
        boolean ok = literatureService.save(request);
        if (!ok || request.getId() == null) {
            throw new BusinessException("保存文献失败");
        }if(!(file == null || file.isEmpty())){
            String key = fileStorageUtil.store(request.getId(), file);
            request.setPdfPath(key);
            request.setFileSize(file.getSize());
            literatureService.updateById(request);
        }
        return Result.ok();
    }

    @PostMapping("/update")
    @Operation(summary = "更新文献")
    public Result<Void> update(@RequestBody Literature request) {
        literatureService.updateById(request);
        return Result.ok();
    }

    @PostMapping("/delete")
    @Operation(summary = "删除文献")
    public Result<Void> delete(@RequestBody DeleteByIdRequest request) {
        literatureService.removeById(request.getId());
        return Result.ok();
    }

    @GetMapping(value = "/summary", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "文献摘要生成（流式输出）")
    public SseEmitter summary(@RequestParam Long literatureId) {
        return literatureService.streamSummary(literatureId);
    }

    @PostMapping("/refresh")
    @Operation(summary = "触发文献更新")
    public Result<LiteratureRefreshResponse> refresh(@RequestParam(defaultValue = "manual") String source) {
        return Result.ok(literatureService.triggerRefresh(source));
    }
}

