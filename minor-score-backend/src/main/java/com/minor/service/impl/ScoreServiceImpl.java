package com.minor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minor.entity.Score;
import com.minor.mapper.ScoreMapper;
import com.minor.service.ScoreService;
import org.springframework.stereotype.Service;

/**
 * 成绩 Service 实现类
 * 提供成绩相关的业务逻辑实现
 */
@Service
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {
}