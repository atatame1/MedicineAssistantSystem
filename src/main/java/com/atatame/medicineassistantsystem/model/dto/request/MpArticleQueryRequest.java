package com.atatame.medicineassistantsystem.model.dto.request;

import lombok.Data;

@Data
public class MpArticleQueryRequest {
    private String biz;
    private String url;
    private String name;
    private Integer page;
}
