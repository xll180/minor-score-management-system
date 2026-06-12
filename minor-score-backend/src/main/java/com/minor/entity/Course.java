package com.minor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程实体
 */
@Data
@TableName("course")
public class Course {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String courseNo;
    private String courseName;
    private String description;
    private BigDecimal credit;
    private Long teacherId;
    private Long collegeId;
    private Integer maxStudents;
    private Integer currentCount;
    private String semester;
    private String classroom;
    private String schedule;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String teacherName;
    @TableField(exist = false)
    private String collegeName;
}