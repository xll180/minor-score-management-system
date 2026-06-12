package com.minor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minor.entity.College;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学院 Mapper 接口
 * 提供学院表的基础增删改查操作
 */
@Mapper
public interface CollegeMapper extends BaseMapper<College> {
}