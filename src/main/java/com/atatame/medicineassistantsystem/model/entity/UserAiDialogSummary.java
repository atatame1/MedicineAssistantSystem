package com.atatame.medicineassistantsystem.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_ai_dialog_summary")
public class UserAiDialogSummary {

    @TableId
    private Long userId;

    @TableField("summary_text")
    private String summaryText;

    @TableField("update_time")
    private LocalDateTime updateTime;
}

