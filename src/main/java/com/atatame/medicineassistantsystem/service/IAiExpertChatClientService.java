package com.atatame.medicineassistantsystem.service;

import com.atatame.medicineassistantsystem.model.dto.request.ExpertChatClientRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface IAiExpertChatClientService {
    SseEmitter stream(ExpertChatClientRequest request);
}

