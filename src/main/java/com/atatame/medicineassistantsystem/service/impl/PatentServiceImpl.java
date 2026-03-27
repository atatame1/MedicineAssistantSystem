package com.atatame.medicineassistantsystem.service.impl;

import com.atatame.medicineassistantsystem.model.entity.Patent;
import com.atatame.medicineassistantsystem.mapper.PatentMapper;
import com.atatame.medicineassistantsystem.service.IPatentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 专利库 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-03-26
 */
@Service
public class PatentServiceImpl extends ServiceImpl<PatentMapper, Patent> implements IPatentService {

}
