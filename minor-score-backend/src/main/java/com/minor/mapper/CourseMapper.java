package com.minor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minor.entity.Course;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程 Mapper 接口
 * 提供课程表的基础增删改查操作
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}