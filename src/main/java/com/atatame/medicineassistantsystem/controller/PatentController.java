package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.common.Result;
import com.atatame.medicineassistantsystem.model.dto.request.DeleteByIdRequest;
import com.atatame.medicineassistantsystem.model.dto.response.PatentRiskResponse;
import com.atatame.medicineassistantsystem.model.dto.response.PatentSimilarityResponse;
import com.atatame.medicineassistantsystem.model.entity.Patent;
import com.atatame.medicineassistantsystem.service.IPatentService;
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
@RequestMapping("/api/patents")
@RequiredArgsConstructor
@Tag(name = "专利库", description = "专利库相关接口")
public class PatentController {

    private final IPatentService patentService;

    @GetMapping
    @Operation(summary = "专利检索")
    public Result<List<Patent>> list(@RequestParam(required = false) String keyword) {
        return Result.ok(patentService.listByKeyword(keyword));
    }

    @PostMapping("/create")
    @Operation(summary = "新增专利")
    public Result<Void> create(@RequestBody Patent request) {
        patentService.save(request);
        return Result.ok();
    }

    @PostMapping("/update")
    @Operation(summary = "更新专利")
    public Result<Void> update(@RequestBody Patent request) {
        patentService.updateById(request);
        return Result.ok();
    }

    @PostMapping("/delete")
    @Operation(summary = "删除专利")
    public Result<Void> delete(@RequestBody DeleteByIdRequest request) {
        patentService.removeById(request.getId());
        return Result.ok();
    }

    @GetMapping("/similar")
    @Operation(summary = "相似专利推荐")
    public Result<List<PatentSimilarityResponse>> similar(@RequestParam Long patentId) {
        return Result.ok(patentService.similarPatents(patentId));
    }

    @GetMapping("/risk")
    @Operation(summary = "专利侵权风险提示")
    public Result<PatentRiskResponse> risk(@RequestParam Long patentId) {
        return Result.ok(patentService.patentRisk(patentId));
    }
}

