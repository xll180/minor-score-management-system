<template>
  <el-container class="layout-container">
    <!-- 左侧侧边栏 -->
    <el-aside width="200px" class="layout-aside">
      <!-- 系统名称 -->
      <div class="aside-logo">
        <h2>辅修成绩管理系统</h2>
      </div>
      
      <!-- 侧边栏菜单（根据角色动态渲染） -->
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <template v-for="item in menuList" :key="item.path">
          <!-- 有子菜单的项 -->
          <el-sub-menu v-if="item.children" :index="item.path">
            <template #title>
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.title }}</span>
            </template>
            <el-menu-item
              v-for="child in item.children"
              :key="child.path"
              :index="child.path"
            >
              {{ child.title }}
            </el-menu-item>
          </el-sub-menu>
          
          <!-- 无子菜单的项 -->
          <el-menu-item v-else :index="item.path">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    
    <!-- 右侧区域 -->
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="layout-header">
        <div class="header-left">
          <!-- 面包屑或标题可在此扩展 -->
        </div>
        <div class="header-right">
          <span class="header-user">
            <el-icon><UserFilled /></el-icon>
            {{ userStore.realName || userStore.username }}
          </span>
          <el-tag size="small" :type="roleTagType">
            {{ roleLabel }}
          </el-tag>
          <el-button
            type="danger"
            size="small"
            :icon="SwitchButton"
            @click="handleLogout"
          >
            退出登录
          </el-button>
        </div>
      </el-header>
      
      <!-- 主体内容区 -->
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import {
  DataAnalysis,
  School,
  UserFilled,
  User,
  Reading,
  Document,
  HomeFilled,
  Edit,
  TrendCharts,
  Collection,
  Tickets,
  SwitchButton
} from '@element-plus/icons-vue'
import { useUserStore } from '../store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 当前激活的菜单项
const activeMenu = computed(() => route.path)

// 管理员菜单
const adminMenuList = [
  { path: '/admin/dashboard', title: '管理仪表盘', icon: DataAnalysis },
  { path: '/admin/college', title: '学院管理', icon: School },
  { path: '/admin/teacher', title: '教师管理', icon: UserFilled },
  { path: '/admin/student', title: '学生管理', icon: User },
  { path: '/admin/course', title: '课程管理', icon: Reading },
  { path: '/admin/score', title: '成绩管理', icon: Document }
]

// 教师菜单
const teacherMenuList = [
  { path: '/teacher/dashboard', title: '教师工作台', icon: HomeFilled },
  { path: '/teacher/course', title: '课程管理', icon: Reading },
  { path: '/teacher/score-import', title: '成绩导入', icon: Edit },
  { path: '/teacher/report', title: '分析报告', icon: TrendCharts }
]

// 学生菜单
const studentMenuList = [
  { path: '/student/dashboard', title: '学生首页', icon: HomeFilled },
  { path: '/student/course-select', title: '选课中心', icon: Collection },
  { path: '/student/my-score', title: '我的成绩', icon: Tickets }
]

// 根据角色获取菜单列表
const menuList = computed(() => {
  const roleMenuMap = {
    ADMIN: adminMenuList,
    TEACHER: teacherMenuList,
    STUDENT: studentMenuList
  }
  return roleMenuMap[userStore.role] || []
})

// 角色标签显示
const roleLabel = computed(() => {
  const map = { ADMIN: '管理员', TEACHER: '教师', STUDENT: '学生' }
  return map[userStore.role] || '未知'
})

// 角色标签样式
const roleTagType = computed(() => {
  const map = { ADMIN: 'danger', TEACHER: 'warning', STUDENT: 'success' }
  return map[userStore.role] || 'info'
})

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

/* 侧边栏 */
.layout-aside {
  background-color: #304156;
  overflow-y: auto;
  overflow-x: hidden;
}

.aside-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b2f3a;
}

.aside-logo h2 {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
}

/* 覆盖 Element Plus 菜单样式 */
.layout-aside .el-menu {
  border-right: none;
}

/* 顶部导航栏 */
.layout-header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
  height: 60px;
}

.header-left {
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-user {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #303133;
  font-size: 14px;
}

/* 主体内容区 */
.layout-main {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>