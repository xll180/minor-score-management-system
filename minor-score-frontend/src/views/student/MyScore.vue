<template>
  <div class="page-container">
    <!-- 成绩统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #409EFF">{{ scoreList.length }}</div>
          <div class="stat-label">已出成绩科目</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #67C23A">{{ avgScore }}</div>
          <div class="stat-label">平均分</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #E6A23C">{{ maxScore }}</div>
          <div class="stat-label">最高分</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #F56C6C">{{ passRate }}%</div>
          <div class="stat-label">及格率</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 成绩列表 -->
    <el-card shadow="hover">
      <template #header>
        <span class="card-header">我的成绩</span>
      </template>
      <el-table :data="scoreList" stripe border style="width: 100%">
        <el-table-column prop="courseName" label="课程名称" min-width="150" />
        <el-table-column prop="score" label="成绩" width="100">
          <template #default="{ row }">
            <el-tag :type="getScoreTagType(row.score)">
              {{ row.score != null ? row.score : '未录入' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="gradeLevel" label="等级" width="90">
          <template #default="{ row }">
            <el-tag :type="getGradeTagType(row.gradeLevel)" size="small">
              {{ row.gradeLevel || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="examType" label="考试类型" width="110" />
        <el-table-column prop="createTime" label="录入时间" width="170" />
      </el-table>
      <el-empty v-if="scoreList.length === 0" description="暂无成绩记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getStudentScores } from '../../api'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const scoreList = ref([])

// 计算平均分
const avgScore = computed(() => {
  if (scoreList.value.length === 0) return 0
  const total = scoreList.value.reduce((sum, s) => sum + (Number(s.score) || 0), 0)
  return (total / scoreList.value.length).toFixed(1)
})

// 计算最高分
const maxScore = computed(() => {
  if (scoreList.value.length === 0) return 0
  const scores = scoreList.value.map(s => Number(s.score) || 0).filter(s => s > 0)
  return scores.length > 0 ? Math.max(...scores) : 0
})

// 计算及格率
const passRate = computed(() => {
  if (scoreList.value.length === 0) return 0
  const passCount = scoreList.value.filter(s => Number(s.score) >= 60).length
  return ((passCount / scoreList.value.length) * 100).toFixed(1)
})

// 加载成绩列表
const loadScores = async () => {
  try {
    const res = await getStudentScores(userStore.userId)
    scoreList.value = res.data || []
  } catch (e) {
    console.error('加载成绩失败', e)
  }
}

// 根据分数返回标签颜色类型
const getScoreTagType = (score) => {
  if (score == null) return 'info'
  if (score >= 90) return 'success'
  if (score >= 60) return 'primary'
  return 'danger'
}

// 根据等级返回标签颜色类型
const getGradeTagType = (grade) => {
  const map = {
    '优秀': 'success',
    '良好': '',
    '中等': 'warning',
    '及格': 'info',
    '不及格': 'danger'
  }
  return map[grade] || 'info'
}

onMounted(() => {
  loadScores()
})
</script>

<style scoped>
.page-container {
  padding: 0;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  padding: 10px 0;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 8px;
}

.card-header {
  font-weight: 600;
  font-size: 15px;
}
</style>
