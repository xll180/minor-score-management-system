<template>
  <div class="dashboard-container">
    <!-- 欢迎区域 -->
    <el-card shadow="hover" class="welcome-card">
      <div class="welcome-content">
        <div class="welcome-text">
          <h2>欢迎回来，{{ userStore.realName || userStore.username }}！</h2>
          <p>以下是你的选课和成绩概况</p>
        </div>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-content">
            <div class="stat-icon" style="background: #409EFF">
              <el-icon :size="28"><Collection /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ selectionCount }}</div>
              <div class="stat-label">已选课程</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-content">
            <div class="stat-icon" style="background: #67C23A">
              <el-icon :size="28"><Tickets /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ scoreCount }}</div>
              <div class="stat-label">已有成绩</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-content">
            <div class="stat-icon" style="background: #E6A23C">
              <el-icon :size="28"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ avgScore }}</div>
              <div class="stat-label">平均成绩</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 已选课程列表 -->
    <el-card shadow="hover">
      <template #header>
        <span class="card-header">我的选课</span>
      </template>
      <el-table :data="mySelections" stripe style="width: 100%">
        <el-table-column prop="courseName" label="课程名称" min-width="150" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="createTime" label="选课时间" width="170" />
      </el-table>
      <el-empty v-if="mySelections.length === 0" description="暂未选课，快去选课中心吧！" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Collection, Tickets, TrendCharts } from '@element-plus/icons-vue'
import { getStudentSelections, getStudentScores } from '../../api'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const mySelections = ref([])
const selectionCount = ref(0)
const scoreCount = ref(0)
const avgScore = ref(0)

// 加载已选课程
const loadSelections = async () => {
  try {
    const res = await getStudentSelections(userStore.userId)
    mySelections.value = res.data || []
    selectionCount.value = mySelections.value.length
  } catch (e) {
    console.error('加载选课列表失败', e)
  }
}

// 加载成绩信息
const loadScores = async () => {
  try {
    const res = await getStudentScores(userStore.userId)
    const scores = res.data || []
    scoreCount.value = scores.length
    if (scores.length > 0) {
      const total = scores.reduce((sum, s) => sum + (Number(s.score) || 0), 0)
      avgScore.value = (total / scores.length).toFixed(1)
    }
  } catch (e) {
    console.error('加载成绩失败', e)
  }
}

onMounted(() => {
  loadSelections()
  loadScores()
})
</script>

<style scoped>
.dashboard-container {
  padding: 0;
}

.welcome-card {
  margin-bottom: 20px;
}

.welcome-content {
  display: flex;
  align-items: center;
}

.welcome-text h2 {
  font-size: 22px;
  color: #303133;
  margin: 0 0 8px 0;
}

.welcome-text p {
  color: #909399;
  font-size: 14px;
  margin: 0;
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
