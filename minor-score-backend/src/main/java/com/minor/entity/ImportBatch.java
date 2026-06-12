package com.minor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成绩导入批次实体
 */
@Data
@TableName("import_batch")
public class ImportBatch {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long courseId;
    private Long teacherId;
    private String batchName;
    private Integer totalCount;
    private Integer successCount;
    private Integer failCount;
    private BigDecimal avgScore;
    private BigDecimal maxScore;
    private BigDecimal minScore;
    private BigDecimal passRate;
    private BigDecimal excellentRate;
    private String scoreDistribution;
    private String aiReport;
    private Integer reportStatus;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String courseName;
    @TableField(exist = false)
    private String teacherName;
}