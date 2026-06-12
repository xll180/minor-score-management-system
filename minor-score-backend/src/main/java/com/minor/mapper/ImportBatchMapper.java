package com.minor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minor.entity.ImportBatch;
import org.apache.ibatis.annotations.Mapper;

/**
 * 成绩导入批次 Mapper 接口
 * 提供导入批次表的基础增删改查操作
 */
@Mapper
public interface ImportBatchMapper extends BaseMapper<ImportBatch> {
}