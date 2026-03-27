package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.common.Result;
import com.atatame.medicineassistantsystem.model.dto.request.DeleteByIdRequest;
import com.atatame.medicineassistantsystem.model.dto.response.LiteratureRefreshResponse;
import com.atatame.medicineassistantsystem.model.dto.response.LiteratureSummaryResponse;
import com.atatame.medicineassistantsystem.model.entity.Literature;
import com.atatame.medicineassistantsystem.service.ILiteratureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/literatures")
@RequiredArgsConstructor
@Tag(name = "文献库", description = "文献库相关接口")
public class LiteratureController {

    private final ILiteratureService literatureService;

    @GetMapping
    @Operation(summary = "文献检索")
    public Result<List<Literature>> list(@RequestParam(required = false) String keyword) {
        return Result.ok(literatureService.listByKeyword(keyword));
    }

    @PostMapping("/create")
    @Operation(summary = "新增文献")
    public Result<Void> create(@RequestBody Literature request) {
        literatureService.save(request);
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

    @GetMapping("/summary")
    @Operation(summary = "文献摘要生成")
    public Result<LiteratureSummaryResponse> summary(@RequestParam Long literatureId) {
        return Result.ok(literatureService.buildSummary(literatureId));
    }

    @PostMapping("/refresh")
    @Operation(summary = "触发文献更新")
    public Result<LiteratureRefreshResponse> refresh(@RequestParam(defaultValue = "manual") String source) {
        return Result.ok(literatureService.triggerRefresh(source));
    }
}

