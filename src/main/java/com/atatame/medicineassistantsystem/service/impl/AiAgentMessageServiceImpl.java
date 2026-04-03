package com.atatame.medicineassistantsystem.service.impl;

import com.atatame.medicineassistantsystem.mapper.AiAgentMessageMapper;
import com.atatame.medicineassistantsystem.model.entity.AiAgentMessage;
import com.atatame.medicineassistantsystem.service.IAiAgentMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AiAgentMessageServiceImpl extends ServiceImpl<AiAgentMessageMapper, AiAgentMessage> implements IAiAgentMessageService {
}
