package com.atatame.medicineassistantsystem.service;

import com.atatame.medicineassistantsystem.model.dto.response.LiteratureRefreshResponse;
import com.atatame.medicineassistantsystem.model.dto.response.LiteratureSummaryResponse;
import com.atatame.medicineassistantsystem.model.entity.Literature;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * <p>
 * 文献库 服务类
 * </p>
 *
 * @author author
 * @since 2026-03-26
 */
public interface ILiteratureService extends IService<Literature> {
    List<Literature> listByKeyword(String keyword);
    LiteratureSummaryResponse buildSummary(Long literatureId);
    LiteratureRefreshResponse triggerRefresh(String source);
}
