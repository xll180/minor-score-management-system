<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-content">
            <div class="stat-icon" style="background: #409EFF">
              <el-icon :size="28"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ courseCount }}</div>
              <div class="stat-label">我的课程</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-content">
            <div class="stat-icon" style="background: #67C23A">
              <el-icon :size="28"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ totalStudents }}</div>
              <div class="stat-label">选课学生总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-content">
            <div class="stat-icon" style="background: #E6A23C">
              <el-icon :size="28"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ batchCount }}</div>
              <div class="stat-label">导入批次</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 我的课程列表 -->
    <el-card shadow="hover">
      <template #header>
        <span class="card-header">我的课程</span>
      </template>
      <el-table :data="courseList" stripe style="width: 100%">
        <el-table-column prop="courseNo" label="课程编号" width="120" />
        <el-table-column prop="courseName" label="课程名称" min-width="150" />
        <el-table-column prop="credit" label="学分" width="80" />
        <el-table-column prop="maxStudents" label="最大人数" width="90" />
        <el-table-column prop="currentCount" label="已选人数" width="90" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="classroom" label="教室" width="90" />
      </el-table>
      <el-empty v-if="courseList.length === 0" description="暂无课程" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Reading, User, Document } from '@element-plus/icons-vue'
import { getTeacherCourses, getCourseStudents } from '../../api'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const courseList = ref([])
const courseCount = ref(0)
const totalStudents = ref(0)
const batchCount = ref(0)

// 加载教师的课程列表
const loadCourses = async () => {
  try {
    const res = await getTeacherCourses(userStore.userId)
    courseList.value = res.data || []
    courseCount.value = courseList.value.length

    // 统计选课学生总数
    let studentTotal = 0
    for (const course of courseList.value) {
      studentTotal += course.currentCount || 0
    }
    totalStudents.value = studentTotal
  } catch (e) {
    console.error('加载课程列表失败', e)
  }
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.dashboard-container {
  padding: 0;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.card-header {
  font-weight: 600;
  font-size: 15px;
}
</style>
