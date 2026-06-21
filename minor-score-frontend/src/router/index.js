import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store/user'

const routes = [
  { path: '/', redirect: '/login' },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/admin',
    component: () => import('../views/Layout.vue'),
    meta: { role: 'ADMIN' },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', component: () => import('../views/admin/Dashboard.vue'), meta: { title: '管理仪表盘' } },
      { path: 'college', component: () => import('../views/admin/CollegeManage.vue'), meta: { title: '学院管理' } },
      { path: 'teacher', component: () => import('../views/admin/TeacherManage.vue'), meta: { title: '教师管理' } },
      { path: 'student', component: () => import('../views/admin/StudentManage.vue'), meta: { title: '学生管理' } },
      { path: 'course', component: () => import('../views/admin/CourseManage.vue'), meta: { title: '课程管理' } },
      { path: 'score', component: () => import('../views/admin/ScoreManage.vue'), meta: { title: '成绩管理' } }
    ]
  },
  {
    path: '/teacher',
    component: () => import('../views/Layout.vue'),
    meta: { role: 'TEACHER' },
    children: [
      { path: '', redirect: '/teacher/dashboard' },
      { path: 'dashboard', component: () => import('../views/teacher/Dashboard.vue'), meta: { title: '教师工作台' } },
      { path: 'course', component: () => import('../views/teacher/CourseManage.vue'), meta: { title: '课程管理' } },
      { path: 'score-manage', component: () => import('../views/teacher/ScoreManage.vue'), meta: { title: '成绩管理' } },
      { path: 'score-import', component: () => import('../views/teacher/ScoreImport.vue'), meta: { title: '成绩导入' } },
      { path: 'report', component: () => import('../views/teacher/ReportView.vue'), meta: { title: '分析报告' } },
      { path: 'profile', component: () => import('../views/teacher/Profile.vue'), meta: { title: '个人资料' } },
    ]
  },
  {
    path: '/student',
    component: () => import('../views/Layout.vue'),
    meta: { role: 'STUDENT' },
    children: [
      { path: '', redirect: '/student/dashboard' },
      { path: 'dashboard', component: () => import('../views/student/Dashboard.vue'), meta: { title: '学生首页' } },
      { path: 'course-select', component: () => import('../views/student/CourseSelect.vue'), meta: { title: '选课中心' } },
      { path: 'my-score', component: () => import('../views/student/MyScore.vue'), meta: { title: '我的成绩' } },
      { path: 'profile', component: () => import('../views/student/Profile.vue'), meta: { title: '个人资料' } },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：检查登录状态和角色权限
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.path === '/login') {
    next()
  } else {
    if (!userStore.token) {
      next('/login')
    } else {
      if (to.meta.role && to.meta.role !== userStore.role) {
        next(`/${userStore.role.toLowerCase()}/dashboard`)
      } else {
        next()
      }
    }
  }
})

export default router