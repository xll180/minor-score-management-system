package com.minor.dto;

import lombok.Data;

/**
 * 登录请求DTO
 * 接收前端提交的登录表单数据
 */
@Data
public class LoginDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 角色: ADMIN(管理员) / TEACHER(教师) / STUDENT(学生)
     */
    private String role;
}