package com.minor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minor.dto.LoginDTO;
import com.minor.dto.LoginResponse;
import com.minor.entity.Admin;

/**
 * 管理员 Service 接口
 * 提供管理员相关的业务逻辑操作
 */
public interface AdminService extends IService<Admin> {

    /**
     * 管理员登录
     * 校验用户名和密码，生成JWT Token并返回登录响应
     *
     * @param dto 登录请求DTO（包含用户名、密码、角色）
     * @return 登录响应（包含Token、用户信息等）
     */
    LoginResponse login(LoginDTO dto);
}