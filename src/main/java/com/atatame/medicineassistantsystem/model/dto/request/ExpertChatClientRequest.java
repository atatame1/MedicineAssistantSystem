package com.atatame.medicineassistantsystem.model.dto.request;

public class ExpertChatClientRequest {
    private String expert;
    private String input;
    private Long conversationId;

    public String getExpert() {
        return expert;
    }

    public void setExpert(String expert) {
        this.expert = expert;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }
}

