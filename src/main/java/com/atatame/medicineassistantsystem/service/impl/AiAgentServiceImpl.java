package com.atatame.medicineassistantsystem.service.impl;

import com.atatame.medicineassistantsystem.ai.AgentCode;
import com.atatame.medicineassistantsystem.ai.AiAgent;
import com.atatame.medicineassistantsystem.model.dto.response.AiTaskResponse;
import com.atatame.medicineassistantsystem.service.IAiAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiAgentServiceImpl implements IAiAgentService {

    private final List<AiAgent> agents;
    private Map<AgentCode, AiAgent> cache;


    @Override
    public AiTaskResponse run(AgentCode code, String input) {
        if (cache == null) {
            cache = new EnumMap<>(AgentCode.class);
            for (AiAgent agent : agents) {
                cache.put(agent.code(), agent);
            }
        }
        AiAgent agent = cache.get(code);
        AiTaskResponse response = new AiTaskResponse();
        response.setAgentCode(code.name());
        response.setOutput(agent == null ? "未找到对应智能体" : agent.run(input));
        return response;
    }

    @Override
    public Flux<String> stream(AgentCode code, String input, Long conversationId) {
        if (cache == null) {
            cache = new EnumMap<>(AgentCode.class);
            for (AiAgent agent : agents) {
                cache.put(agent.code(), agent);
            }
        }
        AiAgent agent = cache.get(code);
        if (agent == null) return Flux.just("未找到对应智能体");
        return agent.stream(input, conversationId);
    }
}

