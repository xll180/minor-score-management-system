package com.minor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学院实体
 */
@Data
@TableName("college")
public class College {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String collegeName;
    private String description;
    private String dean;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}