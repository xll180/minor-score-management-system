package com.minor.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.minor.entity.ImportBatch;
import com.minor.entity.Score;
import com.minor.mapper.ImportBatchMapper;
import com.minor.mapper.ScoreMapper;
import com.minor.mapper.StudentMapper;
import com.minor.service.ImportBatchService;
import com.minor.utils.ScoreExcelListener;
import com.minor.vo.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 成绩导入控制器
 * 提供成绩Excel批量导入功能，包含统计分析
 */
@RestController
@RequestMapping("/api/score-import")
public class ScoreImportController {

    /** 学生Mapper */
    @Resource
    private StudentMapper studentMapper;

    /** 成绩Mapper */
    @Resource
    private ScoreMapper scoreMapper;

    /** 导入批次Service */
    @Resource
    private ImportBatchService importBatchService;

    /** 导入批次Mapper */
    @Resource
    private ImportBatchMapper importBatchMapper;

    /**
     * 上传Excel文件批量导入成绩
     *
     * @param file      Excel文件（multipart/form-data）
     * @param courseId  课程ID
     * @param teacherId 导入教师ID
     * @return 导入结果，包含成功/失败数量、统计数据、失败原因
     */
    @PostMapping("/upload")
    public Result<Map<String, Object>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("courseId") Long courseId,
            @RequestParam(value = "teacherId", required = false) Long teacherId) {
        // 1. 参数校验
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }

        // 2. 创建监听器实例
        ScoreExcelListener listener = new ScoreExcelListener(
                studentMapper, scoreMapper, importBatchMapper, courseId);

        // 3. 使用EasyExcel读取Excel文件并执行导入
        try {
            EasyExcel.read(file.getInputStream(), com.minor.dto.ScoreImportDTO.class, listener)
                    .sheet()
                    .doRead();
        } catch (IOException e) {
            return Result.error("Excel文件解析失败：" + e.getMessage());
        }

        // 4. 获取导入结果
        int successCount = listener.getSuccessCount();
        int failCount = listener.getFailCount();
        int totalCount = successCount + failCount;
        List<String> failReasons = listener.getFailReasons();

        // 5. 计算统计数据（基于数据库中该课程的所有成绩）
        Map<String, Object> statistics = calculateStatistics(courseId, totalCount);

        // 6. 创建导入批次记录并保存
        ImportBatch batch = new ImportBatch();
        batch.setCourseId(courseId);
        batch.setTeacherId(teacherId);
        batch.setBatchName("成绩导入-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        batch.setTotalCount(totalCount);
        batch.setSuccessCount(successCount);
        batch.setFailCount(failCount);
        batch.setAvgScore((BigDecimal) statistics.get("avgScore"));
        batch.setMaxScore((BigDecimal) statistics.get("maxScore"));
        batch.setMinScore((BigDecimal) statistics.get("minScore"));
        batch.setPassRate((BigDecimal) statistics.get("passRate"));
        batch.setExcellentRate((BigDecimal) statistics.get("excellentRate"));
        batch.setScoreDistribution((String) statistics.get("scoreDistribution"));
        importBatchService.save(batch);

        // 7. 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("batchId", batch.getId());
        result.put("totalCount", totalCount);
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("failReasons", failReasons);
        result.put("statistics", statistics);

        return Result.success(result);
    }

    /**
     * 计算课程的统计数据
     * 包括平均分、最高分、最低分、及格率、优秀率、分数段分布
     *
     * @param courseId   课程ID
     * @param totalCount 本次导入总数
     * @return 统计数据Map
     */
    private Map<String, Object> calculateStatistics(Long courseId, int totalCount) {
        Map<String, Object> statistics = new HashMap<>();

        // 查询数据库中该课程的所有成绩
        List<Score> scoreList = scoreMapper.selectByCourseId(courseId);

        // 计算平均分、最高分、最低分
        BigDecimal avgScore = scoreMapper.selectAvgScoreByCourseId(courseId);
        BigDecimal maxScore = scoreMapper.selectMaxScoreByCourseId(courseId);
        BigDecimal minScore = scoreMapper.selectMinScoreByCourseId(courseId);

        statistics.put("avgScore", avgScore != null ? avgScore.setScale(1, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        statistics.put("maxScore", maxScore != null ? maxScore.setScale(1, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        statistics.put("minScore", minScore != null ? minScore.setScale(1, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        // 计算及格率和优秀率
        if (scoreList != null && !scoreList.isEmpty()) {
            int passCount = 0;
            int excellentCount = 0;

            for (Score score : scoreList) {
                if (score.getScore() != null) {
                    double s = score.getScore().doubleValue();
                    if (s >= 60) {
                        passCount++;
                    }
                    if (s >= 90) {
                        excellentCount++;
                    }
                }
            }

            int total = scoreList.size();
            BigDecimal passRate = BigDecimal.valueOf(passCount * 100.0 / total)
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal excellentRate = BigDecimal.valueOf(excellentCount * 100.0 / total)
                    .setScale(2, RoundingMode.HALF_UP);

            statistics.put("passRate", passRate);
            statistics.put("excellentRate", excellentRate);

            // 计算分数段分布
            statistics.put("scoreDistribution", calculateScoreDistribution(scoreList));
        } else {
            statistics.put("passRate", BigDecimal.ZERO);
            statistics.put("excellentRate", BigDecimal.ZERO);
            statistics.put("scoreDistribution", "[]");
        }

        return statistics;
    }

    /**
     * 计算分数段分布
     * 分段：0-59, 60-69, 70-79, 80-89, 90-100
     *
     * @param scoreList 成绩列表
     * @return JSON格式的分数段分布字符串
     */
    private String calculateScoreDistribution(List<Score> scoreList) {
        int range0_59 = 0;
        int range60_69 = 0;
        int range70_79 = 0;
        int range80_89 = 0;
        int range90_100 = 0;

        for (Score score : scoreList) {
            if (score.getScore() == null) continue;
            double s = score.getScore().doubleValue();
            if (s < 60) {
                range0_59++;
            } else if (s < 70) {
                range60_69++;
            } else if (s < 80) {
                range70_79++;
            } else if (s < 90) {
                range80_89++;
            } else {
                range90_100++;
            }
        }

        // 构建分数段分布列表
        List<Map<String, Object>> distributionList = new ArrayList<>();

        Map<String, Object> range1 = new HashMap<>();
        range1.put("range", "0-59");
        range1.put("count", range0_59);
        distributionList.add(range1);

        Map<String, Object> range2 = new HashMap<>();
        range2.put("range", "60-69");
        range2.put("count", range60_69);
        distributionList.add(range2);

        Map<String, Object> range3 = new HashMap<>();
        range3.put("range", "70-79");
        range3.put("count", range70_79);
        distributionList.add(range3);

        Map<String, Object> range4 = new HashMap<>();
        range4.put("range", "80-89");
        range4.put("count", range80_89);
        distributionList.add(range4);

        Map<String, Object> range5 = new HashMap<>();
        range5.put("range", "90-100");
        range5.put("count", range90_100);
        distributionList.add(range5);

        // 转换为JSON字符串
        return JSON.toJSONString(distributionList);
    }
}