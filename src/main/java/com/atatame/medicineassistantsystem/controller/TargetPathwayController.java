package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.common.Result;
import com.atatame.medicineassistantsystem.model.dto.request.DeleteByIdRequest;
import com.atatame.medicineassistantsystem.model.entity.TargetPathway;
import com.atatame.medicineassistantsystem.service.ITargetPathwayService;
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
@RequestMapping("/api/target-pathways")
@RequiredArgsConstructor
@Tag(name = "靶点通路库", description = "靶点通路库相关接口")
public class TargetPathwayController {

    private final ITargetPathwayService targetPathwayService;

    @GetMapping
    @Operation(summary = "靶点/通路检索")
    public Result<List<TargetPathway>> list(@RequestParam(required = false) String keyword) {
        return Result.ok(targetPathwayService.listByKeyword(keyword));
    }

    @PostMapping("/create")
    @Operation(summary = "新增靶点通路")
    public Result<Void> create(@RequestBody TargetPathway request) {
        targetPathwayService.save(request);
        return Result.ok();
    }

    @PostMapping("/update")
    @Operation(summary = "更新靶点通路")
    public Result<Void> update(@RequestBody TargetPathway request) {
        targetPathwayService.updateById(request);
        return Result.ok();
    }

    @PostMapping("/delete")
    @Operation(summary = "删除靶点通路")
    public Result<Void> delete(@RequestBody DeleteByIdRequest request) {
        targetPathwayService.removeById(request.getId());
        return Result.ok();
    }
}

