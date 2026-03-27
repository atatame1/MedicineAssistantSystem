package com.atatame.medicineassistantsystem.service;

import com.atatame.medicineassistantsystem.model.entity.TargetPathway;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * <p>
 * 靶点与信号通路库 服务类
 * </p>
 *
 * @author author
 * @since 2026-03-26
 */
public interface ITargetPathwayService extends IService<TargetPathway> {
    List<TargetPathway> listByKeyword(String keyword);
}
