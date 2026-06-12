<template>
  <div class="dashboard-container">
    <!-- 统计卡片区域 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-content">
            <div class="stat-icon" style="background: #409EFF">
              <el-icon :size="28"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.studentCount || 0 }}</div>
              <div class="stat-label">学生总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-content">
            <div class="stat-icon" style="background: #E6A23C">
              <el-icon :size="28"><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.teacherCount || 0 }}</div>
              <div class="stat-label">教师总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-content">
            <div class="stat-icon" style="background: #67C23A">
              <el-icon :size="28"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.courseCount || 0 }}</div>
              <div class="stat-label">课程总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-content">
            <div class="stat-icon" style="background: #F56C6C">
              <el-icon :size="28"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.scoreCount || 0 }}</div>
              <div class="stat-label">成绩记录</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <!-- 各学院学生人数分布饼图 -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span class="card-header">各学院学生人数分布</span>
          </template>
          <div ref="pieChartRef" class="chart-box"></div>
        </el-card>
      </el-col>

      <!-- 成绩分布柱状图（默认展示第一门课程） -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header-row">
              <span class="card-header">成绩分布</span>
              <el-select
                v-model="selectedCourseId"
                placeholder="选择课程"
                size="small"
                style="width: 180px"
                @change="loadScoreDistribution"
              >
                <el-option
                  v-for="c in courseList"
                  :key="c.id"
                  :label="c.courseName"
                  :value="c.id"
                />
              </el-select>
            </div>
          </template>
          <div ref="barChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 及格率与优秀率 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span class="card-header">成绩概况</span>
          </template>
          <div class="score-overview">
            <div class="overview-item">
              <el-progress
                type="dashboard"
                :percentage="scoreOverview.passRate || 0"
                :color="'#67C23A'"
              >
                <template #default>
                  <span class="progress-text">{{ scoreOverview.passRate || 0 }}%</span>
                </template>
              </el-progress>
              <div class="overview-label">及格率</div>
            </div>
            <div class="overview-item">
              <el-progress
                type="dashboard"
                :percentage="scoreOverview.excellentRate || 0"
                :color="'#409EFF'"
              >
                <template #default>
                  <span class="progress-text">{{ scoreOverview.excellentRate || 0 }}%</span>
                </template>
              </el-progress>
              <div class="overview-label">优秀率</div>
            </div>
            <div class="overview-item">
              <div class="avg-score">{{ scoreOverview.avgScore || 0 }}</div>
              <div class="overview-label">平均分</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { User, UserFilled, Reading, Document } from '@element-plus/icons-vue'
import { getDashboardStats, getCollegeStats, getScoreDistribution, getCourseList } from '../../api'

// 统计数据
const stats = ref({})
const collegeStats = ref([])
const courseList = ref([])
const selectedCourseId = ref(null)
const scoreOverview = ref({})

// 图表引用
const pieChartRef = ref(null)
const barChartRef = ref(null)
let pieChart = null
let barChart = null

// 加载仪表盘统计数据
const loadDashboardStats = async () => {
  try {
    const res = await getDashboardStats()
    stats.value = res.data
  } catch (e) {
    console.error('加载统计数据失败', e)
  }
}

// 加载学院学生分布（饼图）
const loadCollegeStats = async () => {
  try {
    const res = await getCollegeStats()
    collegeStats.value = res.data
    renderPieChart()
  } catch (e) {
    console.error('加载学院统计失败', e)
  }
}

// 加载课程列表（用于下拉选择）
const loadCourses = async () => {
  try {
    const res = await getCourseList({ page: 1, size: 100 })
    courseList.value = res.data.records || []
    // 默认选中第一门课程
    if (courseList.value.length > 0) {
      selectedCourseId.value = courseList.value[0].id
      loadScoreDistribution()
    }
  } catch (e) {
    console.error('加载课程列表失败', e)
  }
}

// 加载成绩分布（柱状图）
const loadScoreDistribution = async () => {
  if (!selectedCourseId.value) return
  try {
    const res = await getScoreDistribution(selectedCourseId.value)
    scoreOverview.value = {
      passRate: res.data.passRate,
      excellentRate: res.data.excellentRate,
      avgScore: res.data.avgScore
    }
    renderBarChart(res.data.distribution || [])
  } catch (e) {
    console.error('加载成绩分布失败', e)
  }
}

// 渲染饼图
const renderPieChart = () => {
  if (!pieChartRef.value) return
  if (pieChart) pieChart.dispose()
  pieChart = echarts.init(pieChartRef.value)
  const data = collegeStats.value.map(item => ({
    name: item.collegeName,
    value: item.studentCount
  }))
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['40%', '65%'],
      center: ['50%', '45%'],
      data,
      label: { formatter: '{b}\n{d}%' },
      emphasis: {
        itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' }
      }
    }]
  })
}

// 渲染柱状图
const renderBarChart = (distribution) => {
  if (!barChartRef.value) return
  if (barChart) barChart.dispose()
  barChart = echarts.init(barChartRef.value)
  const xData = distribution.map(item => item.range)
  const yData = distribution.map(item => item.count)
  barChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: xData },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{
      type: 'bar',
      data: yData,
      barWidth: '50%',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#409EFF' },
          { offset: 1, color: '#79bbff' }
        ]),
        borderRadius: [4, 4, 0, 0]
      }
    }]
  })
}

// 窗口resize时重新调整图表大小
const handleResize = () => {
  pieChart && pieChart.resize()
  barChart && barChart.resize()
}

onMounted(() => {
  loadDashboardStats()
  loadCollegeStats()
  loadCourses()
  window.addEventListener('resize', handleResize)
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

.chart-row {
  margin-bottom: 20px;
}

.chart-box {
  width: 100%;
  height: 320px;
}

.card-header {
  font-weight: 600;
  font-size: 15px;
}

.card-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.score-overview {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 30px 0;
}

.overview-item {
  text-align: center;
}

.overview-label {
  margin-top: 12px;
  font-size: 14px;
  color: #606266;
}

.avg-score {
  font-size: 40px;
  font-weight: 700;
  color: #E6A23C;
  line-height: 1;
}

.progress-text {
  font-size: 14px;
  font-weight: 600;
}
</style>
