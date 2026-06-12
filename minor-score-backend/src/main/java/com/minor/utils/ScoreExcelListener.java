package com.minor.utils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minor.dto.ScoreImportDTO;
import com.minor.entity.Score;
import com.minor.entity.Student;
import com.minor.mapper.ImportBatchMapper;
import com.minor.mapper.ScoreMapper;
import com.minor.mapper.StudentMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 成绩导入Excel监听器
 * 继承EasyExcel的AnalysisEventListener，逐行解析Excel数据并批量写入数据库
 */
public class ScoreExcelListener extends AnalysisEventListener<ScoreImportDTO> {

    /** 每次批量处理的数量 */
    private static final int BATCH_SIZE = 100;

    /** 学生Mapper（通过构造器传入） */
    private final StudentMapper studentMapper;

    /** 成绩Mapper（通过构造器传入） */
    private final ScoreMapper scoreMapper;

    /** 导入批次Mapper（通过构造器传入） */
    private final ImportBatchMapper importBatchMapper;

    /** 课程ID（通过构造器传入） */
    private final Long courseId;

    /** 数据缓存列表，用于批量处理 */
    private final List<ScoreImportDTO> cachedList = new ArrayList<>();

    /** 成功导入数量 */
    private int successCount = 0;

    /** 失败数量 */
    private int failCount = 0;

    /** 失败原因列表 */
    private final List<String> failReasons = new ArrayList<>();

    /**
     * 构造器注入所需Mapper和课程ID
     *
     * @param studentMapper    学生Mapper
     * @param scoreMapper      成绩Mapper
     * @param importBatchMapper 导入批次Mapper
     * @param courseId         课程ID
     */
    public ScoreExcelListener(StudentMapper studentMapper, ScoreMapper scoreMapper,
                              ImportBatchMapper importBatchMapper, Long courseId) {
        this.studentMapper = studentMapper;
        this.scoreMapper = scoreMapper;
        this.importBatchMapper = importBatchMapper;
        this.courseId = courseId;
    }

    /**
     * 每解析一行Excel数据时调用
     *
     * @param data    当前行解析出的DTO对象
     * @param context EasyExcel分析上下文
     */
    @Override
    public void invoke(ScoreImportDTO data, AnalysisContext context) {
        // 将数据加入缓存列表
        cachedList.add(data);
        // 每达到BATCH_SIZE条执行一次批量处理
        if (cachedList.size() >= BATCH_SIZE) {
            batchProcess();
            cachedList.clear();
        }
    }

    /**
     * 所有Excel数据解析完成后调用
     *
     * @param context EasyExcel分析上下文
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 处理剩余未达到批量阈值的数据
        if (!cachedList.isEmpty()) {
            batchProcess();
            cachedList.clear();
        }
    }

    /**
     * 批量处理缓存中的数据
     * 遍历缓存列表，根据学号查询学生ID，如果学生不存在则记录失败原因，
     * 否则根据成绩分数计算等级并插入成绩记录
     */
    private void batchProcess() {
        for (ScoreImportDTO dto : cachedList) {
            try {
                // 根据学号查询学生
                Student student = studentMapper.selectOne(
                        new LambdaQueryWrapper<Student>()
                                .eq(Student::getStudentNo, dto.getStudentNo())
                );

                if (student == null) {
                    // 学生不存在，记录失败原因
                    failCount++;
                    failReasons.add("学号 " + dto.getStudentNo() + "（" + dto.getStudentName() + "）对应的学生不存在");
                    continue;
                }

                // 构建成绩实体
                Score score = new Score();
                score.setStudentId(student.getId());
                score.setCourseId(courseId);
                score.setScore(dto.getScore() != null ? BigDecimal.valueOf(dto.getScore()).setScale(1, RoundingMode.HALF_UP) : null);
                score.setExamType(dto.getExamType() != null ? dto.getExamType() : "期末考试");
                // 根据分数计算成绩等级
                score.setGradeLevel(calcGradeLevel(dto.getScore()));

                // 插入成绩记录
                scoreMapper.insert(score);
                successCount++;
            } catch (Exception e) {
                // 捕获异常，记录失败原因
                failCount++;
                failReasons.add("学号 " + dto.getStudentNo() + "（" + dto.getStudentName() + "）导入失败：" + e.getMessage());
            }
        }
    }

    /**
     * 根据分数计算成绩等级
     *
     * @param score 分数
     * @return 成绩等级（优秀/良好/中等/及格/不及格）
     */
    private String calcGradeLevel(Double score) {
        if (score == null) {
            return "未评分";
        }
        if (score >= 90) {
            return "优秀";
        } else if (score >= 80) {
            return "良好";
        } else if (score >= 70) {
            return "中等";
        } else if (score >= 60) {
            return "及格";
        } else {
            return "不及格";
        }
    }

    /**
     * 获取成功导入数量
     *
     * @return 成功数量
     */
    public int getSuccessCount() {
        return successCount;
    }

    /**
     * 获取失败数量
     *
     * @return 失败数量
     */
    public int getFailCount() {
        return failCount;
    }

    /**
     * 获取失败原因列表
     *
     * @return 失败原因列表
     */
    public List<String> getFailReasons() {
        return failReasons;
    }
}