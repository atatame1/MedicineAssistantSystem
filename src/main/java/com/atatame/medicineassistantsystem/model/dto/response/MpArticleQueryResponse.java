package com.atatame.medicineassistantsystem.model.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class MpArticleQueryResponse {
    private String mpNickname;
    private Integer nowPage;
    private Integer totalPage;
    private Integer nowPageArticlesNum;
    private Integer totalNum;
    private List<MpArticleItemResponse> articles;
}
