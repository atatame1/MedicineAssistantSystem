package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.common.Result;
import com.atatame.medicineassistantsystem.model.dto.request.DeleteByIdRequest;
import com.atatame.medicineassistantsystem.model.entity.Herb;
import com.atatame.medicineassistantsystem.service.IHerbService;
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
@RequestMapping("/api/herbs")
@RequiredArgsConstructor
@Tag(name = "中药材库", description = "中药材库相关接口")
public class HerbController {

    private final IHerbService herbService;

    @GetMapping
    @Operation(summary = "中药材检索")
    public Result<List<Herb>> list(@RequestParam(required = false) String keyword) {
        return Result.ok(herbService.listByKeyword(keyword));
    }

    @PostMapping("/create")
    @Operation(summary = "新增中药材")
    public Result<Void> create(@RequestBody Herb request) {
        herbService.save(request);
        return Result.ok();
    }

    @PostMapping("/update")
    @Operation(summary = "更新中药材")
    public Result<Void> update(@RequestBody Herb request) {
        herbService.updateById(request);
        return Result.ok();
    }

    @PostMapping("/delete")
    @Operation(summary = "删除中药材")
    public Result<Void> delete(@RequestBody DeleteByIdRequest request) {
        herbService.removeById(request.getId());
        return Result.ok();
    }
}

