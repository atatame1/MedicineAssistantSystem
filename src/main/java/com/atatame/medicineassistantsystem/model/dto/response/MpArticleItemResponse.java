package com.atatame.medicineassistantsystem.model.dto.response;

import lombok.Data;

@Data
public class MpArticleItemResponse {
    private String title;
    private String url;
    private String postTimeStr;
    private Long postTime;
    private String coverUrl;
    private Integer original;
}
