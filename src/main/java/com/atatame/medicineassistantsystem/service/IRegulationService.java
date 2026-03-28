package com.atatame.medicineassistantsystem.service;

import com.atatame.medicineassistantsystem.model.entity.Regulation;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * <p>
 * 新药研发法规和指引库 服务类
 * </p>
 *
 * @author author
 * @since 2026-03-26
 */
public interface IRegulationService extends IService<Regulation> {
    List<Regulation> listByKeyword(String keyword);
}
