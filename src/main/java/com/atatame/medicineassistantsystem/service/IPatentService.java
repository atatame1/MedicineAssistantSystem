package com.atatame.medicineassistantsystem.service;

import com.atatame.medicineassistantsystem.model.dto.response.PatentRiskResponse;
import com.atatame.medicineassistantsystem.model.dto.response.PatentSimilarityResponse;
import com.atatame.medicineassistantsystem.model.entity.Patent;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * <p>
 * 专利库 服务类
 * </p>
 *
 * @author author
 * @since 2026-03-26
 */
public interface IPatentService extends IService<Patent> {
    List<Patent> listByKeyword(String keyword);
    List<PatentSimilarityResponse> similarPatents(Long patentId);
    PatentRiskResponse patentRisk(Long patentId);
}
