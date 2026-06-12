package com.minor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minor.entity.ImportBatch;
import com.minor.mapper.ImportBatchMapper;
import com.minor.service.ImportBatchService;
import org.springframework.stereotype.Service;

/**
 * 成绩导入批次 Service 实现类
 * 提供导入批次相关的业务逻辑实现
 */
@Service
public class ImportBatchServiceImpl extends ServiceImpl<ImportBatchMapper, ImportBatch> implements ImportBatchService {
}