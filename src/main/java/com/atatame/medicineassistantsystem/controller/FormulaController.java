package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.common.Result;
import com.atatame.medicineassistantsystem.model.dto.request.DeleteByIdRequest;
import com.atatame.medicineassistantsystem.model.entity.Formula;
import com.atatame.medicineassistantsystem.service.IFormulaService;
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
@RequestMapping("/api/formulas")
@RequiredArgsConstructor
@Tag(name = "方剂库", description = "方剂库相关接口")
public class FormulaController {

    private final IFormulaService formulaService;

    @GetMapping
    @Operation(summary = "方剂检索")
    public Result<List<Formula>> list(@RequestParam(required = false) String keyword) {
        return Result.ok(formulaService.listByKeyword(keyword));
    }

    @PostMapping("/create")
    @Operation(summary = "新增方剂")
    public Result<Void> create(@RequestBody Formula request) {
        formulaService.save(request);
        return Result.ok();
    }

    @PostMapping("/update")
    @Operation(summary = "更新方剂")
    public Result<Void> update(@RequestBody Formula request) {
        formulaService.updateById(request);
        return Result.ok();
    }

    @PostMapping("/delete")
    @Operation(summary = "删除方剂")
    public Result<Void> delete(@RequestBody DeleteByIdRequest request) {
        formulaService.removeById(request.getId());
        return Result.ok();
    }
}

