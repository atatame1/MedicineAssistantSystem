package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.common.Result;
import com.atatame.medicineassistantsystem.model.dto.request.MpArticleQueryRequest;
import com.atatame.medicineassistantsystem.model.dto.response.MpArticleItemResponse;
import com.atatame.medicineassistantsystem.model.dto.response.MpArticleQueryResponse;
import com.atatame.medicineassistantsystem.model.dto.response.PortalOverviewResponse;
import com.atatame.medicineassistantsystem.model.entity.Project;
import com.atatame.medicineassistantsystem.model.entity.UserAiDialogSummary;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.atatame.medicineassistantsystem.service.IProjectService;
import com.atatame.medicineassistantsystem.service.IUserService;
import com.atatame.medicineassistantsystem.service.IUserAiDialogSummaryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/portal")
@RequiredArgsConstructor
@Tag(name = "智能门户", description = "统一入口与看板")
public class PortalController {
    private static final List<String> MP_RESEARCH_INCLUDE_KEYS = Arrays.asList(
            "中药", "中医药", "新药", "研发", "药效", "药理", "药代", "毒理",
            "临床", "实验", "机制", "活性", "成分", "方剂", "复方", "提取",
            "申报", "审评", "注册", "一致性", "指南", "适应症", "靶点", "通路"
    );
    private static final List<String> MP_RESEARCH_EXCLUDE_KEYS = Arrays.asList(
            "养生", "保健", "食疗", "诈骗", "反诈", "辟谣", "节日", "清明", "端午", "春节",
            "招聘", "招募", "征集", "活动", "投票", "抽奖", "直播", "讲座", "培训",
            "公告", "通知", "会议", "晚会", "青年", "团委", "党建", "祝福"
    );

    private final IUserService userService;
    private final IProjectService projectService;
    private final IUserAiDialogSummaryService userAiDialogSummaryService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.mp-monitor.base-url:https://www.dajiala.com/fbmain/monitor/v3/post_history}")
    private String mpMonitorUrl;
    @Value("${app.mp-monitor.key:}")
    private String mpMonitorKey;
    @Value("${app.mp-monitor.default-name:中药新药}")
    private String mpDefaultName;

    @GetMapping("/overview")
    @Operation(summary = "门户总览")
    public Result<PortalOverviewResponse> overview(@RequestParam Long userId) {
        List<Project> projects = projectService.list(new LambdaQueryWrapper<Project>()
                .eq(Project::getProjectorId, userId)
                .orderByDesc(Project::getUpdateTime)
                .last("limit 10"));
        PortalOverviewResponse response = new PortalOverviewResponse();
        response.setTasks(userService.myRecentTasks(userId, 10).stream()
                .filter(t -> t.getStatus() == null || t.getStatus() != 2)
                .collect(Collectors.toList()));
        response.setMyProjects(projects);
        response.setRiskWarnings(projects.stream().filter(p -> p.getPriority() != null && p.getPriority() == 1).count());
        UserAiDialogSummary summary = userAiDialogSummaryService.getById(userId);
        response.setSummaryText(summary == null ? null : summary.getSummaryText());
        return Result.ok(response);
    }

    @PostMapping("/summarize")
    @Operation(summary = "手动生成近期AI对话总结")
    public Result<String> summarize(@RequestParam Long userId) {
        return Result.ok(userAiDialogSummaryService.summarizeUser(userId));
    }

    @PostMapping("/mp-articles")
    @Operation(summary = "查询公众号最近文章")
    public Result<MpArticleQueryResponse> mpArticles(@RequestBody(required = false) MpArticleQueryRequest request) {
        if (!StringUtils.hasText(mpMonitorKey)) return Result.fail("公众号监控key未配置");
        MpArticleQueryRequest req = request == null ? new MpArticleQueryRequest() : request;
        String biz = trim(req.getBiz());
        String url = trim(req.getUrl());
        String name = trim(req.getName());
        if (!StringUtils.hasText(biz) && !StringUtils.hasText(url) && !StringUtils.hasText(name)) {
            name = mpDefaultName;
        }

        int page = req.getPage() == null || req.getPage() < 1 ? 1 : req.getPage();
        Map<String, Object> payload = new HashMap<>();
        payload.put("biz", biz);
        payload.put("url", url);
        payload.put("name", name);
        payload.put("page", page);
        payload.put("key", mpMonitorKey);
        payload.put("verifycode", "");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(payload, headers);

        String body = null;
        RuntimeException lastError = null;
        for (int i = 0; i < 3; i++) {
            try {
                ResponseEntity<String> response = restTemplate.exchange(mpMonitorUrl, HttpMethod.POST, httpEntity, String.class);
                body = response.getBody();
                if (StringUtils.hasText(body)) break;
            } catch (RuntimeException e) {
                lastError = e;
            }
        }
        if (!StringUtils.hasText(body)) {
            if (lastError != null) return Result.fail("公众号文章获取失败，请稍后重试");
            return Result.fail("公众号文章获取失败");
        }

        try {
            JsonNode root = objectMapper.readTree(body);
            int code = root.path("code").asInt(-99999);
            String msg = root.path("msg").asText();
            if (code != 0) return Result.fail(StringUtils.hasText(msg) ? msg : ("请求失败，code=" + code));

            MpArticleQueryResponse response = new MpArticleQueryResponse();
            response.setMpNickname(asText(root, "mp_nickname"));
            response.setNowPage(asIntObj(root, "now_page"));
            response.setTotalPage(asIntObj(root, "total_page"));
            response.setNowPageArticlesNum(asIntObj(root, "now_page_articles_num"));
            response.setTotalNum(asIntObj(root, "total_num"));

            List<MpArticleItemResponse> items = new ArrayList<>();
            JsonNode data = root.path("data");
            if (data.isArray()) {
                for (JsonNode n : data) {
                    if ("1".equals(asText(n, "is_deleted"))) continue;
                    Integer msgStatus = asIntObj(n, "msg_status");
                    if (msgStatus != null && msgStatus != 2) continue;
                    String title = asText(n, "title");
                    String digest = asText(n, "digest");
                    if (!isResearchRelated(title, digest)) continue;
                    MpArticleItemResponse item = new MpArticleItemResponse();
                    item.setTitle(title);
                    item.setUrl(asText(n, "url"));
                    item.setPostTimeStr(asText(n, "post_time_str"));
                    item.setPostTime(asLongObj(n, "post_time"));
                    item.setCoverUrl(asText(n, "cover_url"));
                    item.setOriginal(asIntObj(n, "original"));
                    items.add(item);
                }
            }
            response.setArticles(items);
            return Result.ok(response);
        } catch (Exception e) {
            return Result.fail("公众号文章解析失败");
        }
    }

    private static String trim(String v) {
        return v == null ? "" : v.trim();
    }

    private static String asText(JsonNode node, String field) {
        JsonNode n = node.path(field);
        return n.isMissingNode() || n.isNull() ? null : n.asText();
    }

    private static boolean isResearchRelated(String title, String digest) {
        String text = ((title == null ? "" : title) + " " + (digest == null ? "" : digest)).trim();
        if (text.isEmpty()) return false;
        for (String exclude : MP_RESEARCH_EXCLUDE_KEYS) {
            if (text.contains(exclude)) return false;
        }
        for (String include : MP_RESEARCH_INCLUDE_KEYS) {
            if (text.contains(include)) return true;
        }
        return false;
    }

    private static Integer asIntObj(JsonNode node, String field) {
        JsonNode n = node.path(field);
        return n.isInt() || n.isLong() ? n.asInt() : null;
    }

    private static Long asLongObj(JsonNode node, String field) {
        JsonNode n = node.path(field);
        return n.isLong() || n.isInt() ? n.asLong() : null;
    }
}
