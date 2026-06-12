<template>
  <div class="page-container">
    <!-- 筛选栏 -->
    <div class="toolbar">
      <el-select v-model="selectedCourseId" placeholder="请选择课程" clearable @change="loadScores" style="width: 200px">
        <el-option v-for="c in courseList" :key="c.id" :label="c.courseName" :value="c.id" />
      </el-select>
    </div>

    <!-- 数据表格 -->
    <el-table :data="scoreList" stripe border style="width: 100%">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="studentNo" label="学号" width="120" />
      <el-table-column prop="studentName" label="学生姓名" width="110" />
      <el-table-column prop="courseName" label="课程名称" min-width="140" />
      <el-table-column prop="score" label="成绩" width="90">
        <template #default="{ row }">
          <el-tag :type="getScoreTagType(row.score)">
            {{ row.score != null ? row.score : '未录入' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="gradeLevel" label="等级" width="80" />
      <el-table-column prop="examType" label="考试类型" width="100" />
      <el-table-column prop="createTime" label="录入时间" width="170" />
    </el-table>

    <!-- 空数据提示 -->
    <el-empty v-if="!selectedCourseId" description="请先选择课程查看成绩" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCourseList, getCourseScores } from '../../api'

const courseList = ref([])
const selectedCourseId = ref(null)
const scoreList = ref([])

// 加载课程列表
const loadCourses = async () => {
  try {
    const res = await getCourseList({ page: 1, size: 100 })
    courseList.value = res.data.records || []
  } catch (e) {
    console.error('加载课程列表失败', e)
  }
}

// 加载成绩列表
const loadScores = async () => {
  if (!selectedCourseId.value) {
    scoreList.value = []
    return
  }
  try {
    const res = await getCourseScores(selectedCourseId.value)
    scoreList.value = res.data || []
  } catch (e) {
    console.error('加载成绩列表失败', e)
  }
}

// 根据分数返回标签颜色类型
const getScoreTagType = (score) => {
  if (score == null) return 'info'
  if (score >= 90) return 'success'
  if (score >= 60) return 'primary'
  return 'danger'
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.page-container {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}
.toolbar {
  margin-bottom: 16px;
}
</style>
