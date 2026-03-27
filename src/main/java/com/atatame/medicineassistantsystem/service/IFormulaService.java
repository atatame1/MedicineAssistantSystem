package com.atatame.medicineassistantsystem.service;

import com.atatame.medicineassistantsystem.model.entity.Formula;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * <p>
 * 经典方剂库 服务类
 * </p>
 *
 * @author author
 * @since 2026-03-26
 */
public interface IFormulaService extends IService<Formula> {
    List<Formula> listByKeyword(String keyword);
}
