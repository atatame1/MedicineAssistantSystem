package com.atatame.medicineassistantsystem.model.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VoiceGenRequest {
    private String voiceName;
    private String text;
}
