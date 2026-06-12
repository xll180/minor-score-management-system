package com.minor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minor.entity.Course;
import com.minor.mapper.CourseMapper;
import com.minor.service.CourseService;
import org.springframework.stereotype.Service;

/**
 * 课程 Service 实现类
 * 提供课程相关的业务逻辑实现
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
}