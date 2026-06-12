package com.minor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minor.entity.Course;
import com.minor.service.CourseService;
import com.minor.vo.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程管理控制器
 * 提供课程的增删改查接口，同时支持根据教师ID查询其教授的课程
 */
@RestController
@RequestMapping("/api/course")
public class CourseController {

    /**
     * 课程Service
     */
    @Resource
    private CourseService courseService;

    /**
     * 分页查询所有课程列表
     *
     * @param page 当前页码
     * @param size 每页条数
     * @return 分页课程数据
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> list(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer size) {
        IPage<Course> iPage = new Page<>(page, size);
        IPage<Course> result = courseService.page(iPage);
        Map<String, Object> map = new HashMap<>();
        map.put("records", result.getRecords());
        map.put("total", result.getTotal());
        map.put("current", result.getCurrent());
        map.put("size", result.getSize());
        return Result.success(map);
    }

    /**
     * 根据ID查询课程
     *
     * @param id 课程ID
     * @return 课程信息
     */
    @GetMapping("/{id}")
    public Result<Course> getById(@PathVariable Long id) {
        Course course = courseService.getById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }
        return Result.success(course);
    }

    /**
     * 新增课程
     *
     * @param course 课程信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody Course course) {
        boolean saved = courseService.save(course);
        if (saved) {
            return Result.success("新增成功");
        }
        return Result.error("新增失败");
    }

    /**
     * 更新课程信息
     *
     * @param course 课程信息
     * @return 操作结果
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody Course course) {
        boolean updated = courseService.updateById(course);
        if (updated) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除课程
     *
     * @param id 课程ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean removed = courseService.removeById(id);
        if (removed) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 根据教师ID查询课程列表
     *
     * @param teacherId 教师ID
     * @return 该教师教授的课程列表
     */
    @GetMapping("/teacher/{teacherId}")
    public Result<List<Course>> listByTeacherId(@PathVariable Long teacherId) {
        List<Course> list = courseService.lambdaQuery()
                .eq(Course::getTeacherId, teacherId)
                .list();
        return Result.success(list);
    }
}