package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.common.Result;
import com.atatame.medicineassistantsystem.model.dto.request.VoiceGenRequest;
import com.atatame.medicineassistantsystem.utils.VoiceGenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/voice")
@RequiredArgsConstructor
@Tag(name = "语音合成", description = "SiliconFlow TTS")
public class VoiceGenController {

    private final VoiceGenUtil voiceGenUtil;

    @PostMapping("/gen")
    @Operation(summary = "文本转语音（mp3 字节）")
    public Result<byte[]> gen(@RequestBody VoiceGenRequest req) {
        try {
            return Result.ok(voiceGenUtil.gen(req.getVoiceName(), req.getText()));
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            return Result.fail(e.getMessage() != null ? e.getMessage() : "生成失败");
        }
    }
}
