package com.minor.controller;

import com.minor.entity.Course;
import com.minor.entity.Score;
import com.minor.entity.Student;
import com.minor.service.CourseService;
import com.minor.service.ScoreService;
import com.minor.service.StudentService;
import com.minor.vo.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 成绩管理控制器
 * 提供成绩的增删改查接口，支持按课程和学生维度查询成绩
 */
@RestController
@RequestMapping("/api/score")
public class ScoreController {

    /**
     * 成绩Service
     */
    @Resource
    private ScoreService scoreService;

    /**
     * 学生Service
     */
    @Resource
    private StudentService studentService;

    /**
     * 课程Service
     */
    @Resource
    private CourseService courseService;

    /**
     * 根据课程ID查询所有成绩
     *
     * @param courseId 课程ID
     * @return 该课程的成绩列表
     */
    @GetMapping("/course/{courseId}")
    public Result<List<Score>> listByCourseId(@PathVariable Long courseId) {
        List<Score> list = scoreService.lambdaQuery()
                .eq(Score::getCourseId, courseId)
                .list();
        fillScoreRelatedInfo(list);
        return Result.success(list);
    }

    /**
     * 根据学生ID查询成绩
     *
     * @param studentId 学生ID
     * @return 该学生的成绩列表
     */
    @GetMapping("/student/{studentId}")
    public Result<List<Score>> listByStudentId(@PathVariable Long studentId) {
        List<Score> list = scoreService.lambdaQuery()
                .eq(Score::getStudentId, studentId)
                .list();
        fillScoreRelatedInfo(list);
        return Result.success(list);
    }

    /**
     * 根据ID查询成绩
     *
     * @param id 成绩ID
     * @return 成绩信息
     */
    @GetMapping("/{id}")
    public Result<Score> getById(@PathVariable Long id) {
        Score score = scoreService.getById(id);
        if (score == null) {
            return Result.error("成绩不存在");
        }
        return Result.success(score);
    }

    /**
     * 手动添加成绩（单个）
     *
     * @param score 成绩信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody Score score) {
        boolean saved = scoreService.save(score);
        if (saved) {
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    /**
     * 更新成绩信息
     *
     * @param score 成绩信息
     * @return 操作结果
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody Score score) {
        boolean updated = scoreService.updateById(score);
        if (updated) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除成绩
     *
     * @param id 成绩ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean removed = scoreService.removeById(id);
        if (removed) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 填充成绩关联信息（学生姓名、学号、课程名称）
     *
     * @param scores 成绩列表
     */
    private void fillScoreRelatedInfo(List<Score> scores) {
        if (scores == null || scores.isEmpty()) {
            return;
        }
        // 获取所有学生ID和课程ID
        List<Long> studentIds = scores.stream()
                .map(Score::getStudentId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        List<Long> courseIds = scores.stream()
                .map(Score::getCourseId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        // 批量查询学生和课程信息
        Map<Long, Student> studentMap = studentIds.isEmpty()
                ? new HashMap<>()
                : studentService.listByIds(studentIds).stream()
                    .collect(Collectors.toMap(Student::getId, s -> s));
        Map<Long, Course> courseMap = courseIds.isEmpty()
                ? new HashMap<>()
                : courseService.listByIds(courseIds).stream()
                    .collect(Collectors.toMap(Course::getId, c -> c));
        // 填充关联信息
        scores.forEach(score -> {
            if (score.getStudentId() != null) {
                Student student = studentMap.get(score.getStudentId());
                if (student != null) {
                    score.setStudentName(student.getRealName());
                    score.setStudentNo(student.getStudentNo());
                }
            }
            if (score.getCourseId() != null) {
                Course course = courseMap.get(score.getCourseId());
                if (course != null) {
                    score.setCourseName(course.getCourseName());
                }
            }
        });
    }
}