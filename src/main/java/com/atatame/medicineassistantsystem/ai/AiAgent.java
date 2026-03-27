package com.atatame.medicineassistantsystem.ai;

public interface AiAgent {
    AgentCode code();
    String run(String input);
}

