package com.minor.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.minor.entity.ImportBatch;
import com.minor.mapper.ImportBatchMapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 讯飞星火大模型AI分析服务
 * 调用讯飞星火HTTP API生成教学分析报告
 */
@Service
public class XunfeiSparkService {

    private static final Logger log = LoggerFactory.getLogger(XunfeiSparkService.class);

    /** 讯飞星火API地址 */
    @Value("${xunfei.spark.api-url}")
    private String apiUrl;

    /** 讯飞星火API Key */
    @Value("${xunfei.spark.api-key}")
    private String apiKey;

    /** 默认模型版本 */
    @Value("${xunfei.spark.model:lite}")
    private String model;

    /** 导入批次Mapper */
    @Resource
    private ImportBatchMapper importBatchMapper;

    /** HTTP请求客户端 */
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 根据导入批次ID生成AI教学分析报告
     * 从数据库读取该批次的统计数据，构建Prompt发送给讯飞星火API，
     * 解析返回的报告内容并保存到数据库中
     *
     * @param batchId 导入批次ID
     * @return AI生成的报告内容，失败时返回错误信息
     */
    public String generateReport(Long batchId) {
        // 1. 查询导入批次获取统计数据
        ImportBatch batch = importBatchMapper.selectById(batchId);
        if (batch == null) {
            return "导入批次不存在，batchId：" + batchId;
        }

        // 2. 构建Prompt提示词
        String prompt = buildPrompt(batch);

        // 3. 调用讯飞星火API
        String aiResult;
        try {
            aiResult = callXunfeiApi(prompt);
        } catch (Exception e) {
            log.error("调用讯飞星火API失败：{}", e.getMessage(), e);
            return "AI分析服务调用失败：" + e.getMessage();
        }

        // 4. 解析API响应获取报告内容
        String reportContent;
        try {
            reportContent = parseResponse(aiResult);
        } catch (Exception e) {
            log.error("解析讯飞星火API响应失败：{}", e.getMessage(), e);
            return "AI报告解析失败：" + e.getMessage();
        }

        // 5. 保存报告到数据库
        batch.setAiReport(reportContent);
        batch.setReportStatus(1);
        importBatchMapper.updateById(batch);

        return reportContent;
    }

    /**
     * 预览已生成的AI报告
     * 直接返回数据库中已有的AI报告内容，不重新生成
     *
     * @param batchId 导入批次ID
     * @return AI报告内容，如果没有则返回null
     */
    public String previewReport(Long batchId) {
        ImportBatch batch = importBatchMapper.selectById(batchId);
        if (batch == null) {
            return null;
        }
        return batch.getAiReport();
    }

    /**
     * 构建发送给AI的Prompt提示词
     *
     * @param batch 导入批次实体（含统计数据）
     * @return 完整的提示词字符串
     */
    private String buildPrompt(ImportBatch batch) {
        return "你是一位资深教育数据分析专家，请根据以下辅修专业课程成绩统计数据，" +
                "生成一份专业的教学分析报告。报告应包含：\n" +
                "1. 考试概况\n" +
                "2. 成绩分布分析\n" +
                "3. 教学效果评估\n" +
                "4. 改进建议\n\n" +
                "统计数据如下：\n" +
                "平均分" + (batch.getAvgScore() != null ? batch.getAvgScore() : "无") + "，\n" +
                "最高分" + (batch.getMaxScore() != null ? batch.getMaxScore() : "无") + "，\n" +
                "最低分" + (batch.getMinScore() != null ? batch.getMinScore() : "无") + "，\n" +
                "及格率" + (batch.getPassRate() != null ? batch.getPassRate() + "%" : "无") + "，\n" +
                "优秀率" + (batch.getExcellentRate() != null ? batch.getExcellentRate() + "%" : "无") + "，\n" +
                "分数段分布" + (batch.getScoreDistribution() != null ? batch.getScoreDistribution() : "无") + "。\n\n" +
                "请使用中文输出，格式清晰。";
    }

    /**
     * 调用讯飞星火大模型HTTP API
     *
     * @param prompt 提示词
     * @return API返回的原始JSON字符串
     */
    private String callXunfeiApi(String prompt) {
        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // 构建消息体
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", List.of(message));
        requestBody.put("temperature", 0.7);

        // 发送POST请求
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("API返回非2xx状态码：" + response.getStatusCode());
        }
    }

    /**
     * 解析讯飞星火API返回的JSON响应，提取报告内容
     * 响应格式为OpenAI兼容格式：
     * {
     *   "choices": [{"message": {"content": "报告内容..."}}]
     * }
     *
     * @param responseJson API返回的原始JSON字符串
     * @return 提取出的报告内容
     */
    private String parseResponse(String responseJson) {
        JSONObject jsonObject = JSON.parseObject(responseJson);

        // 检查是否有错误信息
        if (jsonObject.containsKey("error")) {
            JSONObject error = jsonObject.getJSONObject("error");
            String errorMsg = error.getString("message");
            throw new RuntimeException("AI API返回错误：" + errorMsg);
        }

        // 提取choices数组中的内容
        JSONArray choices = jsonObject.getJSONArray("choices");
        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException("AI API返回结果中无choices数据");
        }

        JSONObject firstChoice = choices.getJSONObject(0);
        JSONObject message = firstChoice.getJSONObject("message");
        if (message == null) {
            throw new RuntimeException("AI API返回结果中无message数据");
        }

        String content = message.getString("content");
        if (content == null || content.isEmpty()) {
            throw new RuntimeException("AI API返回的报告内容为空");
        }

        return content;
    }
}