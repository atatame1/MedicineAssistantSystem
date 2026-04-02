package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.common.Result;
import com.atatame.medicineassistantsystem.model.dto.request.DeleteByIdRequest;
import com.atatame.medicineassistantsystem.model.entity.Component;
import com.atatame.medicineassistantsystem.service.IComponentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/components")
@RequiredArgsConstructor
@Tag(name = "成分库", description = "成分库相关接口")
public class ComponentController {

    private final IComponentService componentService;

    @GetMapping
    @Operation(summary = "成分检索")
    public Result<List<Component>> list(@RequestParam(required = false) String keyword) {
        return Result.ok(componentService.listByKeyword(keyword));
    }

    @GetMapping("/{id:\\d+}")
    @Operation(summary = "成分详情（按ID）")
    public Result<Component> detail(@PathVariable Long id) {
        Component c = componentService.getById(id);
        if (c == null) {
            return Result.fail("成分不存在");
        }
        return Result.ok(c);
    }

    @PostMapping("/create")
    @Operation(summary = "新增成分")
    public Result<Void> create(@RequestBody Component request) {
        componentService.save(request);
        return Result.ok();
    }

    @PostMapping("/update")
    @Operation(summary = "更新成分")
    public Result<Void> update(@RequestBody Component request) {
        componentService.updateById(request);
        return Result.ok();
    }

    @PostMapping("/delete")
    @Operation(summary = "删除成分")
    public Result<Void> delete(@RequestBody DeleteByIdRequest request) {
        componentService.removeById(request.getId());
        return Result.ok();
    }
}

