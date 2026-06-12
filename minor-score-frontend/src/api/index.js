import request from '../utils/request'

// ============ 认证接口 ============
export const login = (data) => request.post('/auth/login', data)

// ============ 学院接口 ============
export const getCollegeList = () => request.get('/college/list')
export const getCollegeById = (id) => request.get(`/college/${id}`)
export const addCollege = (data) => request.post('/college/add', data)
export const updateCollege = (data) => request.put('/college/update', data)
export const deleteCollege = (id) => request.delete(`/college/${id}`)

// ============ 教师接口 ============
export const getTeacherList = (params) => request.get('/teacher/list', { params })
export const getTeacherById = (id) => request.get(`/teacher/${id}`)
export const addTeacher = (data) => request.post('/teacher/add', data)
export const updateTeacher = (data) => request.put('/teacher/update', data)
export const deleteTeacher = (id) => request.delete(`/teacher/${id}`)

// ============ 学生接口 ============
export const getStudentList = (params) => request.get('/student/list', { params })
export const getStudentById = (id) => request.get(`/student/${id}`)
export const addStudent = (data) => request.post('/student/add', data)
export const updateStudent = (data) => request.put('/student/update', data)
export const deleteStudent = (id) => request.delete(`/student/${id}`)

// ============ 课程接口 ============
export const getCourseList = (params) => request.get('/course/list', { params })
export const getCourseById = (id) => request.get(`/course/${id}`)
export const addCourse = (data) => request.post('/course/add', data)
export const updateCourse = (data) => request.put('/course/update', data)
export const deleteCourse = (id) => request.delete(`/course/${id}`)
export const getTeacherCourses = (teacherId) => request.get(`/course/teacher/${teacherId}`)

// ============ 选课接口 ============
export const selectCourse = (data) => request.post('/selection/select', data)
export const getStudentSelections = (studentId) => request.get(`/selection/student/${studentId}`)
export const getCourseStudents = (courseId) => request.get(`/selection/course/${courseId}/students`)
export const dropCourse = (id) => request.delete(`/selection/${id}`)

// ============ 成绩接口 ============
export const getCourseScores = (courseId) => request.get(`/score/course/${courseId}`)
export const getStudentScores = (studentId) => request.get(`/score/student/${studentId}`)
export const getScoreById = (id) => request.get(`/score/${id}`)
export const addScore = (data) => request.post('/score/add', data)
export const updateScore = (data) => request.put('/score/update', data)
export const deleteScore = (id) => request.delete(`/score/${id}`)

// ============ 成绩导入接口 ============
export const importScores = (formData) => request.post('/score-import/upload', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
})

// ============ AI报告接口 ============
export const generateReport = (batchId) => request.post(`/report/generate/${batchId}`)
export const previewReport = (batchId) => request.get(`/report/preview/${batchId}`)
export const getImportBatches = (courseId) => request.get(`/report/batches/${courseId}`)

// ============ 导出接口 ============
export const exportWord = (batchId) => request.get(`/export/word/${batchId}`, { responseType: 'blob' })
export const exportPdf = (batchId) => request.get(`/export/pdf/${batchId}`, { responseType: 'blob' })

// ============ 统计接口 ============
export const getDashboardStats = () => request.get('/statistics/dashboard')
export const getScoreDistribution = (courseId) => request.get(`/statistics/score-distribution/${courseId}`)
export const getCollegeStats = () => request.get('/statistics/college-stats')