package com.minor.controller;

import com.minor.dto.LoginDTO;
import com.minor.dto.LoginResponse;
import com.minor.service.AdminService;
import com.minor.service.StudentService;
import com.minor.service.TeacherService;
import com.minor.vo.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理用户登录相关请求，根据角色分发到不同的Service进行登录验证
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * 管理员Service
     */
    @Resource
    private AdminService adminService;

    /**
     * 教师Service
     */
    @Resource
    private TeacherService teacherService;

    /**
     * 学生Service
     */
    @Resource
    private StudentService studentService;

    /**
     * 用户登录接口
     * 根据角色调用对应的Service进行登录验证
     *
     * @param dto 登录请求数据，包含用户名、密码、角色
     * @return 登录成功返回用户信息和Token，失败返回错误提示
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginDTO dto) {
        LoginResponse loginResponse = switch (dto.getRole()) {
            case "ADMIN" -> adminService.login(dto);
            case "TEACHER" -> teacherService.login(dto);
            case "STUDENT" -> studentService.login(dto);
            default -> null;
        };

        if (loginResponse == null) {
            return Result.error("用户名或密码错误");
        }
        return Result.success(loginResponse);
    }
}
