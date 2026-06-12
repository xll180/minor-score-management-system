package com.minor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minor.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 教师 Mapper 接口
 * 提供教师表的数据库操作
 */
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {

    /**
     * 根据用户名查询教师
     *
     * @param username 用户名
     * @return 教师实体，未找到返回 null
     */
    @Select("SELECT * FROM teacher WHERE username = #{username}")
    Teacher selectByUsername(String username);
}