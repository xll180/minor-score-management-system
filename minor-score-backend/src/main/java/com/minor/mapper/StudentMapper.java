package com.minor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minor.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 学生 Mapper 接口
 * 提供学生表的数据库操作
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    /**
     * 根据用户名查询学生
     *
     * @param username 用户名
     * @return 学生实体，未找到返回 null
     */
    @Select("SELECT * FROM student WHERE username = #{username}")
    Student selectByUsername(String username);
}