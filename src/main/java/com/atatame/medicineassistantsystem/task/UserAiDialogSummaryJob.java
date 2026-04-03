package com.atatame.medicineassistantsystem.task;

import com.atatame.medicineassistantsystem.model.entity.AiAgentConversation;
import com.atatame.medicineassistantsystem.model.entity.User;
import com.atatame.medicineassistantsystem.service.IUserAiDialogSummaryService;
import com.atatame.medicineassistantsystem.service.IAiAgentConversationService;
import com.atatame.medicineassistantsystem.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserAiDialogSummaryJob {

    private final IUserAiDialogSummaryService userAiDialogSummaryService;
    private final IAiAgentConversationService aiAgentConversationService;
    private final IUserService userService;

    @Scheduled(cron = "0 19 16 * * ?")
    public void summarizeAllUsers() {
        // 先从用户表拿一份 ID 列表，再与有会话记录的用户做交集
        List<Long> allUserIds = userService.lambdaQuery()
                .select(User::getId)
                .list()
                .stream()
                .map(User::getId)
                .toList();
        if (allUserIds.isEmpty()) {
            return;
        }
        List<Long> convUserIds = aiAgentConversationService.lambdaQuery()
                .select(AiAgentConversation::getUserId)
                .in(AiAgentConversation::getUserId, allUserIds)
                .list()
                .stream()
                .map(AiAgentConversation::getUserId)
                .distinct()
                .toList();
        for (Long userId : convUserIds) {
            try {
                userAiDialogSummaryService.summarizeUser(userId);
            } catch (Exception ex) {
                log.warn("summarizeAllUsers: user {} failed", userId, ex);
            }
        }
    }
}

