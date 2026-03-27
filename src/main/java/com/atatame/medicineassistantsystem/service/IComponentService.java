package com.atatame.medicineassistantsystem.service;

import com.atatame.medicineassistantsystem.model.entity.Component;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * <p>
 * 化学成分库 服务类
 * </p>
 *
 * @author author
 * @since 2026-03-26
 */
public interface IComponentService extends IService<Component> {
    List<Component> listByKeyword(String keyword);
}
