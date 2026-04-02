package com.atatame.medicineassistantsystem.ai;

import reactor.core.publisher.Flux;

public interface AiAgent {
    AgentCode code();
    String run(String input,Long conversationId);
    Flux<String> stream(String input,Long conversationId);

    String run(String input);
    Flux<String> stream(String input);
}

