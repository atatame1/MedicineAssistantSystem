package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.common.Result;
import com.atatame.medicineassistantsystem.model.dto.request.DeleteByIdRequest;
import com.atatame.medicineassistantsystem.model.entity.Regulation;
import com.atatame.medicineassistantsystem.service.IRegulationService;
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
@RequestMapping("/api/regulations")
@RequiredArgsConstructor
@Tag(name = "法规库", description = "法规库相关接口")
public class RegulationController {

    private final IRegulationService regulationService;

    @GetMapping
    @Operation(summary = "法规检索")
    public Result<List<Regulation>> list(@RequestParam(required = false) String keyword) {
        return Result.ok(regulationService.listByKeyword(keyword));
    }

    @PostMapping("/create")
    @Operation(summary = "新增法规")
    public Result<Void> create(@RequestBody Regulation request) {
        regulationService.save(request);
        return Result.ok();
    }

    @PostMapping("/update")
    @Operation(summary = "更新法规")
    public Result<Void> update(@RequestBody Regulation request) {
        regulationService.updateById(request);
        return Result.ok();
    }

    @PostMapping("/delete")
    @Operation(summary = "删除法规")
    public Result<Void> delete(@RequestBody DeleteByIdRequest request) {
        regulationService.removeById(request.getId());
        return Result.ok();
    }
}

