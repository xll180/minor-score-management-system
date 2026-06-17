package com.minor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minor.entity.College;
import com.minor.entity.Student;
import com.minor.service.CollegeService;
import com.minor.service.StudentService;
import com.minor.vo.Result;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学生管理控制器
 * 提供学生的增删改查接口，新增学生时使用BCrypt加密密码
 */
@RestController
@RequestMapping("/api/student")
public class StudentController {

    /**
     * 学生Service
     */
    @Resource
    private StudentService studentService;

    /**
     * 学院Service
     */
    @Resource
    private CollegeService collegeService;

    /**
     * 密码编码器，用于BCrypt加密
     */
    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 分页查询所有学生列表
     *
     * @param page 当前页码
     * @param size 每页条数
     * @return 分页学生数据
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> list(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer size) {
        IPage<Student> iPage = new Page<>(page, size);
        IPage<Student> result = studentService.page(iPage);
        // 填充学院名称
        List<Student> records = result.getRecords();
        if (!records.isEmpty()) {
            List<Long> collegeIds = records.stream()
                    .map(Student::getCollegeId)
                    .filter(id -> id != null)
                    .distinct()
                    .collect(Collectors.toList());
            if (!collegeIds.isEmpty()) {
                Map<Long, String> collegeMap = collegeService.listByIds(collegeIds).stream()
                        .collect(Collectors.toMap(College::getId, College::getCollegeName));
                records.forEach(student -> {
                    if (student.getCollegeId() != null) {
                        student.setCollegeName(collegeMap.get(student.getCollegeId()));
                    }
                });
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("records", records);
        map.put("total", result.getTotal());
        map.put("current", result.getCurrent());
        map.put("size", result.getSize());
        return Result.success(map);
    }

    /**
     * 根据ID查询学生
     *
     * @param id 学生ID
     * @return 学生信息
     */
    @GetMapping("/{id}")
    public Result<Student> getById(@PathVariable Long id) {
        Student student = studentService.getById(id);
        if (student == null) {
            return Result.error("学生不存在");
        }
        return Result.success(student);
    }

    /**
     * 新增学生
     * 密码使用BCrypt加密后存储
     *
     * @param student 学生信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody Student student) {
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        boolean saved = studentService.save(student);
        if (saved) {
            return Result.success("新增成功");
        }
        return Result.error("新增失败");
    }

    /**
     * 更新学生信息
     *
     * @param student 学生信息
     * @return 操作结果
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody Student student) {
        boolean updated = studentService.updateById(student);
        if (updated) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除学生
     *
     * @param id 学生ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean removed = studentService.removeById(id);
        if (removed) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}