package com.minor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minor.dto.LoginDTO;
import com.minor.dto.LoginResponse;
import com.minor.entity.Student;
import com.minor.mapper.StudentMapper;
import com.minor.security.JwtUtil;
import com.minor.service.StudentService;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 学生 Service 实现类
 * 提供学生相关的业务逻辑实现，包括登录认证
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 学生登录
     * 根据用户名查询学生，BCrypt 匹配密码，生成 JWT Token 并返回登录响应
     *
     * @param dto 登录请求DTO（包含用户名、密码、角色）
     * @return 登录响应（包含Token、用户信息等）
     */
    @Override
    public LoginResponse login(LoginDTO dto) {
        // 根据用户名查询学生
        Student student = studentMapper.selectByUsername(dto.getUsername());
        if (student == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // BCrypt 匹配密码
        if (!passwordEncoder.matches(dto.getPassword(), student.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 生成 JWT Token，角色为 STUDENT
        String token = jwtUtil.generateToken(student.getId(), student.getUsername(), "STUDENT");

        // 构建并返回登录响应
        return new LoginResponse(token, student.getId(), student.getUsername(), student.getRealName(), "STUDENT");
    }
}