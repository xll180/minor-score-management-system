package com.minor.controller;

import com.minor.entity.ImportBatch;
import com.minor.service.ImportBatchService;
import com.minor.service.XunfeiSparkService;
import com.minor.vo.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI报告控制器
 * 提供AI教学分析报告的生成、预览和批次查询接口
 */
@RestController
@RequestMapping("/api/report")
public class AIReportController {

    /** 讯飞星火AI分析服务 */
    @Resource
    private XunfeiSparkService xunfeiSparkService;

    /** 导入批次Service */
    @Resource
    private ImportBatchService importBatchService;

    /**
     * 生成AI教学分析报告
     * 调用讯飞星火大模型API，根据导入批次的统计数据生成专业的教学分析报告，
     * 生成的报告将保存到数据库中
     *
     * @param batchId 导入批次ID
     * @return 生成的报告内容
     */
    @PostMapping("/generate/{batchId}")
    public Result<Map<String, Object>> generateReport(@PathVariable Long batchId) {
        String reportContent = xunfeiSparkService.generateReport(batchId);

        // 判断是否生成成功
        if (reportContent != null && !reportContent.startsWith("导入批次不存在")
                && !reportContent.startsWith("AI分析服务调用失败")
                && !reportContent.startsWith("AI报告解析失败")) {
            Map<String, Object> result = new HashMap<>();
            result.put("batchId", batchId);
            result.put("report", reportContent);
            return Result.success(result);
        } else {
            return Result.error(reportContent);
        }
    }

    /**
     * 预览AI报告
     * 先检查数据库中是否已有生成的报告，如果有则直接返回；
     * 如果没有则自动调用生成接口生成报告
     *
     * @param batchId 导入批次ID
     * @return 报告内容
     */
    @GetMapping("/preview/{batchId}")
    public Result<Map<String, Object>> previewReport(@PathVariable Long batchId) {
        // 先尝试获取已生成的报告
        String existingReport = xunfeiSparkService.previewReport(batchId);

        String reportContent;
        if (existingReport != null && !existingReport.isEmpty()) {
            // 已有报告，直接返回
            reportContent = existingReport;
        } else {
            // 没有报告，自动生成
            reportContent = xunfeiSparkService.generateReport(batchId);

            // 判断是否生成成功
            if (reportContent != null && !reportContent.startsWith("导入批次不存在")
                    && !reportContent.startsWith("AI分析服务调用失败")
                    && !reportContent.startsWith("AI报告解析失败")) {
                // 生成成功，继续
            } else {
                return Result.error(reportContent);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("batchId", batchId);
        result.put("report", reportContent);
        return Result.success(result);
    }

    /**
     * 查询某课程的所有导入批次
     * 按创建时间降序排列，最新的批次排在前面
     *
     * @param courseId 课程ID
     * @return 该课程的所有导入批次列表
     */
    @GetMapping("/batches/{courseId}")
    public Result<List<ImportBatch>> listBatchesByCourseId(@PathVariable Long courseId) {
        List<ImportBatch> batchList = importBatchService.lambdaQuery()
                .eq(ImportBatch::getCourseId, courseId)
                .orderByDesc(ImportBatch::getCreateTime)
                .list();
        return Result.success(batchList);
    }
}