package com.atatame.medicineassistantsystem.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class VoiceGenUtil {

    private static final Map<String, String> VOICE_URI = Map.of(
            "chenkeji", "speech:chenkeji:d7dj966cnncc73dmpij0:vobjnhnkgxgtaedvqwwr"
    );

    private static final String DEFAULT_VOICE = "FunAudioLLM/CosyVoice2-0.5B:alex";

    private static final String SILICONFLOW_TTS_API_URL = "https://api.siliconflow.cn/v1/audio/speech";

    private final RestTemplate restTemplate;

    @Value("${siliconflow.voice-key}")
    private String siliconflowApiKey;

    @Value("${siliconflow.voice-model}")
    private String ttsModel;

    public byte[] gen(String name, String input) {
        if (!StringUtils.hasText(input)) {
            throw new IllegalArgumentException("text 不能为空");
        }
        String key = name.trim().toLowerCase();
        String voiceUri = VOICE_URI.get(key);

        if(voiceUri==null){
            voiceUri=DEFAULT_VOICE;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(siliconflowApiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", ttsModel);
        body.put("voice", voiceUri);
        body.put("input", input.trim());
        body.put("response_format", "mp3");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<byte[]> resp = restTemplate.exchange(
                    SILICONFLOW_TTS_API_URL,
                    HttpMethod.POST,
                    entity,
                    byte[].class
            );
            byte[] audio = resp.getBody();
            if (audio == null || audio.length == 0) {
                throw new IllegalStateException("TTS 返回空音频");
            }
            return audio;
        } catch (RestClientException e) {
            throw new IllegalStateException("TTS 请求失败: " + e.getMessage(), e);
        }
    }
}
