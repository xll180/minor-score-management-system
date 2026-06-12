package com.minor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minor.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 管理员 Mapper 接口
 * 提供管理员表的数据库操作
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

    /**
     * 根据用户名查询管理员
     *
     * @param username 用户名
     * @return 管理员实体，未找到返回 null
     */
    @Select("SELECT * FROM admin WHERE username = #{username}")
    Admin selectByUsername(String username);
}