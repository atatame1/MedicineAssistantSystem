package com.atatame.medicineassistantsystem.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_agent_conversation")
public class AiAgentConversation {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("conversation_id")
    private Long conversationId;

    @TableField("user_id")
    private Long userId;

    @TableField("type")
    private String type;

    @TableField("title")
    private String title;

    @TableField("input_text")
    private String inputText;

    @TableField("output_text")
    private String outputText;

    @TableField("messages_json")
    private String messagesJson;

    @TableField("create_time")
    private LocalDateTime createTime;
}

