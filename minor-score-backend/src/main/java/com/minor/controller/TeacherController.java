package com.minor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minor.entity.College;
import com.minor.entity.Teacher;
import com.minor.service.CollegeService;
import com.minor.service.TeacherService;
import com.minor.vo.Result;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 教师管理控制器
 * 提供教师的增删改查接口，新增教师时使用BCrypt加密密码
 */
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    /**
     * 教师Service
     */
    @Resource
    private TeacherService teacherService;

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
    @GetMapping("/profile")
    public Result<Teacher> getProfile() {
        Object credentials = SecurityContextHolder.getContext().getAuthentication().getCredentials();
        Long userId = Long.valueOf(credentials.toString());
        Teacher teacher = teacherService.getById(userId);
        if (teacher == null) {
            return Result.error("教师不存在");
        }
        if (teacher.getCollegeId() != null) {
            College college = collegeService.getById(teacher.getCollegeId());
            if (college != null) {
                teacher.setCollegeName(college.getCollegeName());
            }
        }
        teacher.setPassword(null);
        return Result.success(teacher);
    }

    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody Teacher teacherUpdate) {
        Object credentials = SecurityContextHolder.getContext().getAuthentication().getCredentials();
        Long userId = Long.valueOf(credentials.toString());
        Teacher updateEntity = new Teacher();
        updateEntity.setId(userId);
        if (teacherUpdate.getRealName() != null) updateEntity.setRealName(teacherUpdate.getRealName());
        if (teacherUpdate.getGender() != null) updateEntity.setGender(teacherUpdate.getGender());
        if (teacherUpdate.getPhone() != null) updateEntity.setPhone(teacherUpdate.getPhone());
        if (teacherUpdate.getEmail() != null) updateEntity.setEmail(teacherUpdate.getEmail());
        boolean updated = teacherService.updateById(updateEntity);
        if (updated) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }



    /**
     * 分页查询所有教师列表
     *
     * @param page 当前页码
     * @param size 每页条数
     * @return 分页教师数据
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> list(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer size) {
        IPage<Teacher> iPage = new Page<>(page, size);
        IPage<Teacher> result = teacherService.page(iPage);
        Map<String, Object> map = new HashMap<>();
        map.put("records", result.getRecords());
        map.put("total", result.getTotal());
        map.put("current", result.getCurrent());
        map.put("size", result.getSize());
        return Result.success(map);
    }

    /**
     * 根据ID查询教师
     *
     * @param id 教师ID
     * @return 教师信息
     */
    @GetMapping("/{id}")
    public Result<Teacher> getById(@PathVariable Long id) {
        Teacher teacher = teacherService.getById(id);
        if (teacher == null) {
            return Result.error("教师不存在");
        }
        return Result.success(teacher);
    }

    /**
     * 新增教师
     * 密码使用BCrypt加密后存储
     *
     * @param teacher 教师信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody Teacher teacher) {
        teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
        boolean saved = teacherService.save(teacher);
        if (saved) {
            return Result.success("新增成功");
        }
        return Result.error("新增失败");
    }

    /**
     * 更新教师信息
     *
     * @param teacher 教师信息
     * @return 操作结果
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody Teacher teacher) {
        boolean updated = teacherService.updateById(teacher);
        if (updated) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除教师
     *
     * @param id 教师ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean removed = teacherService.removeById(id);
        if (removed) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}