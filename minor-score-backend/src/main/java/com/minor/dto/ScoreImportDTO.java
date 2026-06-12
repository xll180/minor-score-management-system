package com.minor.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 成绩导入Excel对应的DTO
 * 使用EasyExcel注解映射Excel列与Java字段
 */
@Data
public class ScoreImportDTO {
    /** 学号 */
    @ExcelProperty("学号")
    private String studentNo;

    /** 姓名 */
    @ExcelProperty("姓名")
    private String studentName;

    /** 成绩 */
    @ExcelProperty("成绩")
    private Double score;

    /** 考试类型 */
    @ExcelProperty("考试类型")
    private String examType;
}