package com.atatame.medicineassistantsystem.service.impl;

import com.atatame.medicineassistantsystem.model.entity.Disease;
import com.atatame.medicineassistantsystem.mapper.DiseaseMapper;
import com.atatame.medicineassistantsystem.service.IDiseaseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 疾病分类库 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-03-26
 */
@Service
public class DiseaseServiceImpl extends ServiceImpl<DiseaseMapper, Disease> implements IDiseaseService {

}
