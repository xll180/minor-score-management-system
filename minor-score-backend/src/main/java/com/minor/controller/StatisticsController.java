package com.minor.controller;

import com.minor.entity.College;
import com.minor.entity.Score;
import com.minor.entity.Student;
import com.minor.mapper.ScoreMapper;
import com.minor.mapper.StudentMapper;
import com.minor.service.CollegeService;
import com.minor.service.CourseService;
import com.minor.service.ScoreService;
import com.minor.service.StudentService;
import com.minor.service.TeacherService;
import com.minor.vo.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 统计图表控制器
 * 提供仪表盘数据、成绩分布统计和学院统计等接口
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Resource
    private StudentService studentService;

    @Resource
    private TeacherService teacherService;

    @Resource
    private CourseService courseService;

    @Resource
    private ScoreService scoreService;

    @Resource
    private CollegeService collegeService;

    @Resource
    private ScoreMapper scoreMapper;

    @Resource
    private StudentMapper studentMapper;

    /**
     * 获取仪表盘数据
     * 返回系统核心统计数据：总学生数、总教师数、总课程数、总成绩记录数
     *
     * @return 仪表盘统计数据
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        long studentCount = studentService.count();
        long teacherCount = teacherService.count();
        long courseCount = courseService.count();
        long scoreCount = scoreService.count();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("studentCount", studentCount);
        data.put("teacherCount", teacherCount);
        data.put("courseCount", courseCount);
        data.put("scoreCount", scoreCount);

        return Result.success(data);
    }

    /**
     * 获取某课程的成绩分布数据
     * 包含各分数段人数、及格率、优秀率和平均分
     *
     * @param courseId 课程ID
     * @return 成绩分布统计数据
     */
    @GetMapping("/score-distribution/{courseId}")
    public Result<Map<String, Object>> scoreDistribution(@PathVariable Long courseId) {
        // 查询该课程的所有成绩记录
        List<Score> scoreList = scoreMapper.selectByCourseId(courseId);

        int totalCount = scoreList.size();
        int[][] ranges = {{0, 59}, {60, 69}, {70, 79}, {80, 89}, {90, 100}};
        String[] rangeLabels = {"0-59", "60-69", "70-79", "80-89", "90-100"};

        List<Map<String, Object>> distribution = new ArrayList<>();

        int passCount = 0;      // 及格人数 (>=60)
        int excellentCount = 0; // 优秀人数 (>=90)
        BigDecimal sumScore = BigDecimal.ZERO;

        for (int i = 0; i < ranges.length; i++) {
            final int minScore = ranges[i][0];
            final int maxScore = ranges[i][1];
            int count = 0;
            for (Score score : scoreList) {
                if (score.getScore() != null) {
                    double s = score.getScore().doubleValue();
                    if (s >= minScore && s <= maxScore) {
                        count++;
                    }
                }
            }
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("range", rangeLabels[i]);
            item.put("count", count);
            distribution.add(item);
        }

        // 计算及格人数、优秀人数和总分
        for (Score score : scoreList) {
            if (score.getScore() != null) {
                double s = score.getScore().doubleValue();
                sumScore = sumScore.add(score.getScore());
                if (s >= 60) {
                    passCount++;
                }
                if (s >= 90) {
                    excellentCount++;
                }
            }
        }

        // 计算比率
        double passRate = totalCount > 0
                ? BigDecimal.valueOf(passCount).multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalCount), 1, RoundingMode.HALF_UP).doubleValue()
                : 0.0;
        double excellentRate = totalCount > 0
                ? BigDecimal.valueOf(excellentCount).multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalCount), 1, RoundingMode.HALF_UP).doubleValue()
                : 0.0;
        double avgScore = totalCount > 0
                ? sumScore.divide(BigDecimal.valueOf(totalCount), 1, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("distribution", distribution);
        result.put("passRate", passRate);
        result.put("excellentRate", excellentRate);
        result.put("avgScore", avgScore);

        return Result.success(result);
    }

    /**
     * 获取各学院学生人数统计
     * 按学院分组统计学生数量
     *
     * @return 各学院学生人数列表
     */
    @GetMapping("/college-stats")
    public Result<List<Map<String, Object>>> collegeStats() {
        // 查询所有学生
        List<Student> studentList = studentMapper.selectList(null);

        // 查询所有学院，构建ID->名称映射
        Map<Long, String> collegeNameMap = new LinkedHashMap<>();
        collegeService.list().forEach(college ->
                collegeNameMap.put(college.getId(), college.getCollegeName()));

        // 按学院ID分组统计人数
        Map<Long, Integer> collegeCountMap = new LinkedHashMap<>();
        for (Student student : studentList) {
            Long collegeId = student.getCollegeId();
            if (collegeId != null) {
                collegeCountMap.put(collegeId, collegeCountMap.getOrDefault(collegeId, 0) + 1);
            }
        }

        // 构建返回结果
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : collegeCountMap.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("collegeId", entry.getKey());
            item.put("collegeName", collegeNameMap.getOrDefault(entry.getKey(), "未知学院"));
            item.put("studentCount", entry.getValue());
            result.add(item);
        }

        return Result.success(result);
    }
}