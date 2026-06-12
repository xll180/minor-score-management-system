package com.minor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minor.entity.Course;
import com.minor.entity.CourseSelection;
import com.minor.mapper.CourseMapper;
import com.minor.mapper.CourseSelectionMapper;
import com.minor.service.CourseSelectionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 选课记录 Service 实现类
 * 提供选课相关的业务逻辑实现，包括选课操作
 */
@Service
public class CourseSelectionServiceImpl extends ServiceImpl<CourseSelectionMapper, CourseSelection> implements CourseSelectionService {

    @Resource
    private CourseSelectionMapper courseSelectionMapper;

    @Resource
    private CourseMapper courseMapper;

    /**
     * 学生选课
     * 检查课程是否存在、是否已满、是否已选过，然后插入选课记录并更新课程已选人数
     *
     * @param studentId 学生ID
     * @param courseId  课程ID
     * @return true 表示选课成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean selectCourse(Long studentId, Long courseId) {
        // 检查课程是否存在
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }

        // 检查课程是否已满
        if (course.getCurrentCount() >= course.getMaxStudents()) {
            throw new RuntimeException("课程已满，无法选课");
        }

        // 检查是否已选过该课程
        QueryWrapper<CourseSelection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        queryWrapper.eq("course_id", courseId);
        Long count = courseSelectionMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("您已选过该课程，请勿重复选课");
        }

        // 插入选课记录
        CourseSelection selection = new CourseSelection();
        selection.setStudentId(studentId);
        selection.setCourseId(courseId);
        selection.setSelectTime(LocalDateTime.now());
        selection.setStatus(1);
        courseSelectionMapper.insert(selection);

        // 更新课程已选人数
        course.setCurrentCount(course.getCurrentCount() + 1);
        courseMapper.updateById(course);

        return true;
    }
}