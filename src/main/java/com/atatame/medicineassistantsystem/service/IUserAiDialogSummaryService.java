package com.atatame.medicineassistantsystem.service;

import com.atatame.medicineassistantsystem.model.entity.UserAiDialogSummary;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IUserAiDialogSummaryService extends IService<UserAiDialogSummary> {
    String summarizeUser(Long userId);
}

