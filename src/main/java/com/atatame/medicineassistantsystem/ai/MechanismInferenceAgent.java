package com.atatame.medicineassistantsystem.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.atatame.medicineassistantsystem.utils.ChatMemoryUtil;

@Component
public class MechanismInferenceAgent extends BasePromptAgent {
    public MechanismInferenceAgent(ChatClient.Builder builder,
                                   @Value("classpath:prompts/mechanism-inference.txt") Resource prompt,
                                   ChatMemoryUtil chatMemoryUtil,
                                   ChatModel chatModel) {
        super(builder, prompt, chatMemoryUtil, chatModel);
    }

    @Override
    public AgentCode code() {
        return AgentCode.MECHANISM_INFERENCE;
    }
}

