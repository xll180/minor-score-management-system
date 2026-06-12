package com.minor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minor.entity.Score;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 成绩 Mapper 接口
 * 提供成绩表的数据库操作，包含统计分析查询方法
 */
@Mapper
public interface ScoreMapper extends BaseMapper<Score> {

    /**
     * 根据课程ID查询所有成绩记录
     *
     * @param courseId 课程ID
     * @return 成绩列表
     */
    @Select("SELECT * FROM score WHERE course_id = #{courseId}")
    List<Score> selectByCourseId(Long courseId);

    /**
     * 根据学生ID查询所有成绩记录
     *
     * @param studentId 学生ID
     * @return 成绩列表
     */
    @Select("SELECT * FROM score WHERE student_id = #{studentId}")
    List<Score> selectByStudentId(Long studentId);

    /**
     * 计算某门课程的平均分
     *
     * @param courseId 课程ID
     * @return 平均分
     */
    @Select("SELECT AVG(score) FROM score WHERE course_id = #{courseId}")
    BigDecimal selectAvgScoreByCourseId(Long courseId);

    /**
     * 查询某门课程的最高分
     *
     * @param courseId 课程ID
     * @return 最高分
     */
    @Select("SELECT MAX(score) FROM score WHERE course_id = #{courseId}")
    BigDecimal selectMaxScoreByCourseId(Long courseId);

    /**
     * 查询某门课程的最低分
     *
     * @param courseId 课程ID
     * @return 最低分
     */
    @Select("SELECT MIN(score) FROM score WHERE course_id = #{courseId}")
    BigDecimal selectMinScoreByCourseId(Long courseId);

    /**
     * 统计某门课程的及格人数（分数 >= 60）
     *
     * @param courseId 课程ID
     * @return 及格人数
     */
    @Select("SELECT COUNT(*) FROM score WHERE course_id = #{courseId} AND score >= 60")
    Integer selectPassCountByCourseId(Long courseId);

    /**
     * 统计某门课程的优秀人数（分数 >= 90）
     *
     * @param courseId 课程ID
     * @return 优秀人数
     */
    @Select("SELECT COUNT(*) FROM score WHERE course_id = #{courseId} AND score >= 90")
    Integer selectExcellentCountByCourseId(Long courseId);

    /**
     * 统计某门课程的总选课人数
     *
     * @param courseId 课程ID
     * @return 总人数
     */
    @Select("SELECT COUNT(*) FROM score WHERE course_id = #{courseId}")
    Integer selectTotalCountByCourseId(Long courseId);
}