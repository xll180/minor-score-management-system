package com.minor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minor.entity.CourseSelection;
import org.apache.ibatis.annotations.Mapper;

/**
 * 选课记录 Mapper 接口
 * 提供选课记录表的基础增删改查操作
 */
@Mapper
public interface CourseSelectionMapper extends BaseMapper<CourseSelection> {
}