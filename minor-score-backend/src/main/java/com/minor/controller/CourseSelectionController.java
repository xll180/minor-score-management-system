package com.minor.controller;

import com.minor.entity.CourseSelection;
import com.minor.entity.Student;
import com.minor.service.CourseSelectionService;
import com.minor.service.StudentService;
import com.minor.vo.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 选课管理控制器
 * 提供选课、退课、查询已选课程等功能
 */
@RestController
@RequestMapping("/api/selection")
public class CourseSelectionController {

    /**
     * 选课Service
     */
    @Resource
    private CourseSelectionService courseSelectionService;

    /**
     * 学生Service
     */
    @Resource
    private StudentService studentService;

    /**
     * 学生选课
     *
     * @param body 请求体，包含 studentId 和 courseId
     * @return 选课结果
     */
    @PostMapping("/select")
    public Result<String> selectCourse(@RequestBody Map<String, Long> body) {
        Long studentId = body.get("studentId");
        Long courseId = body.get("courseId");

        if (studentId == null || courseId == null) {
            return Result.error("参数错误：studentId 和 courseId 不能为空");
        }

        boolean success = courseSelectionService.selectCourse(studentId, courseId);
        if (success) {
            return Result.success("选课成功");
        }
        return Result.error("选课失败");
    }

    /**
     * 查询学生已选课程列表
     *
     * @param studentId 学生ID
     * @return 该学生已选的课程列表
     */
    @GetMapping("/student/{studentId}")
    public Result<List<CourseSelection>> listByStudentId(@PathVariable Long studentId) {
        List<CourseSelection> list = courseSelectionService.lambdaQuery()
                .eq(CourseSelection::getStudentId, studentId)
                .list();
        return Result.success(list);
    }

    /**
     * 查询某课程下的学生列表
     *
     * @param courseId 课程ID
     * @return 该课程已选的学生列表
     */
    @GetMapping("/course/{courseId}/students")
    public Result<List<CourseSelection>> listByCourseId(@PathVariable Long courseId) {
        List<CourseSelection> list = courseSelectionService.lambdaQuery()
                .eq(CourseSelection::getCourseId, courseId)
                .list();
        // 填充学生信息
        if (!list.isEmpty()) {
            List<Long> studentIds = list.stream()
                    .map(CourseSelection::getStudentId)
                    .distinct()
                    .collect(Collectors.toList());
            Map<Long, Student> studentMap = studentService.listByIds(studentIds).stream()
                    .collect(Collectors.toMap(Student::getId, s -> s));
            list.forEach(selection -> {
                Student student = studentMap.get(selection.getStudentId());
                if (student != null) {
                    selection.setStudentNo(student.getStudentNo());
                    selection.setStudentName(student.getRealName());
                }
            });
        }
        return Result.success(list);
    }

    /**
     * 退课
     * 根据选课记录ID删除选课记录
     *
     * @param id 选课记录ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean removed = courseSelectionService.removeById(id);
        if (removed) {
            return Result.success("退课成功");
        }
        return Result.error("退课失败");
    }
}