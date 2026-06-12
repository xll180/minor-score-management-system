package com.minor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minor.entity.College;
import com.minor.mapper.CollegeMapper;
import com.minor.service.CollegeService;
import org.springframework.stereotype.Service;

/**
 * 学院 Service 实现类
 * 提供学院相关的业务逻辑实现
 */
@Service
public class CollegeServiceImpl extends ServiceImpl<CollegeMapper, College> implements CollegeService {
}