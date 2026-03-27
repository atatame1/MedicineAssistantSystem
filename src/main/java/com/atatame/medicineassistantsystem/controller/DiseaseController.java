package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.common.Result;
import com.atatame.medicineassistantsystem.model.dto.request.DeleteByIdRequest;
import com.atatame.medicineassistantsystem.model.entity.Disease;
import com.atatame.medicineassistantsystem.service.IDiseaseService;
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
@RequestMapping("/api/diseases")
@RequiredArgsConstructor
@Tag(name = "疾病库", description = "疾病库相关接口")
public class DiseaseController {

    private final IDiseaseService diseaseService;

    @GetMapping
    @Operation(summary = "疾病检索")
    public Result<List<Disease>> list(@RequestParam(required = false) String keyword) {
        return Result.ok(diseaseService.listByKeyword(keyword));
    }

    @PostMapping("/create")
    @Operation(summary = "新增疾病")
    public Result<Void> create(@RequestBody Disease request) {
        diseaseService.save(request);
        return Result.ok();
    }

    @PostMapping("/update")
    @Operation(summary = "更新疾病")
    public Result<Void> update(@RequestBody Disease request) {
        diseaseService.updateById(request);
        return Result.ok();
    }

    @PostMapping("/delete")
    @Operation(summary = "删除疾病")
    public Result<Void> delete(@RequestBody DeleteByIdRequest request) {
        diseaseService.removeById(request.getId());
        return Result.ok();
    }
}

