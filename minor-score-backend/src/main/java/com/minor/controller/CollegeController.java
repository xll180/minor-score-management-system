package com.minor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minor.entity.College;
import com.minor.service.CollegeService;
import com.minor.vo.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学院管理控制器
 * 提供学院的增删改查接口，删除操作为逻辑删除
 */
@RestController
@RequestMapping("/api/college")
public class CollegeController {

    /**
     * 学院Service
     */
    @Resource
    private CollegeService collegeService;

    /**
     * 查询所有学院列表
     *
     * @return 学院列表
     */
    @GetMapping("/list")
    public Result<List<College>> list() {
        List<College> list = collegeService.list();
        return Result.success(list);
    }

    /**
     * 根据ID查询学院
     *
     * @param id 学院ID
     * @return 学院信息
     */
    @GetMapping("/{id}")
    public Result<College> getById(@PathVariable Long id) {
        College college = collegeService.getById(id);
        if (college == null) {
            return Result.error("学院不存在");
        }
        return Result.success(college);
    }

    /**
     * 新增学院
     *
     * @param college 学院信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody College college) {
        boolean saved = collegeService.save(college);
        if (saved) {
            return Result.success("新增成功");
        }
        return Result.error("新增失败");
    }

    /**
     * 更新学院信息
     *
     * @param college 学院信息
     * @return 操作结果
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody College college) {
        boolean updated = collegeService.updateById(college);
        if (updated) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除学院（逻辑删除）
     *
     * @param id 学院ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean removed = collegeService.removeById(id);
        if (removed) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}