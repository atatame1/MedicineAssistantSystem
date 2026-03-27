package com.atatame.medicineassistantsystem.service;

import com.atatame.medicineassistantsystem.model.entity.Herb;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * <p>
 * 中药材信息库 服务类
 * </p>
 *
 * @author author
 * @since 2026-03-26
 */
public interface IHerbService extends IService<Herb> {
    List<Herb> listByKeyword(String keyword);
}
