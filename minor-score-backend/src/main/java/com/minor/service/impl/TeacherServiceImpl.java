package com.minor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minor.dto.LoginDTO;
import com.minor.dto.LoginResponse;
import com.minor.entity.Teacher;
import com.minor.mapper.TeacherMapper;
import com.minor.security.JwtUtil;
import com.minor.service.TeacherService;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 教师 Service 实现类
 * 提供教师相关的业务逻辑实现，包括登录认证
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Resource
    private TeacherMapper teacherMapper;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 教师登录
     * 根据用户名查询教师，BCrypt 匹配密码，生成 JWT Token 并返回登录响应
     *
     * @param dto 登录请求DTO（包含用户名、密码、角色）
     * @return 登录响应（包含Token、用户信息等）
     */
    @Override
    public LoginResponse login(LoginDTO dto) {
        // 根据用户名查询教师
        Teacher teacher = teacherMapper.selectByUsername(dto.getUsername());
        if (teacher == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // BCrypt 匹配密码
        if (!passwordEncoder.matches(dto.getPassword(), teacher.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 生成 JWT Token，角色为 TEACHER
        String token = jwtUtil.generateToken(teacher.getId(), teacher.getUsername(), "TEACHER");

        // 构建并返回登录响应
        return new LoginResponse(token, teacher.getId(), teacher.getUsername(), teacher.getRealName(), "TEACHER");
    }
}