package com.minor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minor.entity.CourseSelection;

/**
 * 选课记录 Service 接口
 * 提供选课相关的业务逻辑操作
 */
public interface CourseSelectionService extends IService<CourseSelection> {

    /**
     * 学生选课
     * 检查课程是否存在、是否已满、是否已选过，然后插入选课记录并更新课程已选人数
     *
     * @param studentId 学生ID
     * @param courseId  课程ID
     * @return true 表示选课成功，false 表示选课失败
     */
    boolean selectCourse(Long studentId, Long courseId);
}