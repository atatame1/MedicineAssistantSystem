package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.common.Result;
import com.atatame.medicineassistantsystem.model.dto.request.VoiceGenRequest;
import com.atatame.medicineassistantsystem.utils.VoiceGenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    @Operation(summary = "文本转语音（JSON 包装 mp3 字节，便于调试）")
    public Result<byte[]> gen(@RequestBody VoiceGenRequest req) {
        try {
            return Result.ok(voiceGenUtil.gen(req.getVoiceName(), req.getText()));
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            return Result.fail(e.getMessage() != null ? e.getMessage() : "生成失败");
        }
    }

    @PostMapping(value = "/gen/audio", consumes = MediaType.APPLICATION_JSON_VALUE, produces = "audio/mpeg")
    @Operation(summary = "文本转语音（直接返回 mp3 流，供前端播放）")
    public ResponseEntity<byte[]> genAudio(@RequestBody VoiceGenRequest req) {
        try {
            byte[] audio = voiceGenUtil.gen(req.getVoiceName(), req.getText());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"speech.mp3\"")
                    .contentType(MediaType.parseMediaType("audio/mpeg"))
                    .body(audio);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
