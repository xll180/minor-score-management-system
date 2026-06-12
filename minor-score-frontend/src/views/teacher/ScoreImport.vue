<template>
  <div class="page-container">
    <!-- 导入区域 -->
    <el-card shadow="hover" class="import-card">
      <template #header>
        <span class="card-header">Excel成绩导入</span>
      </template>

      <el-form label-width="100px">
        <el-form-item label="选择课程">
          <el-select v-model="selectedCourseId" placeholder="请选择要导入成绩的课程" style="width: 300px">
            <el-option v-for="c in courseList" :key="c.id" :label="c.courseName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="上传文件">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            accept=".xlsx,.xls"
            :on-change="handleFileChange"
            :on-exceed="handleExceed"
          >
            <template #trigger>
              <el-button type="primary">选择Excel文件</el-button>
            </template>
            <template #tip>
              <div class="el-upload__tip">
                仅支持 .xlsx / .xls 格式，表头应包含：学号、姓名、成绩、考试类型
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="success" :loading="importing" :disabled="!selectedCourseId || !selectedFile" @click="handleImport">
            开始导入
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 导入结果 -->
    <el-card v-if="importResult" shadow="hover" class="result-card">
      <template #header>
        <span class="card-header">导入结果</span>
      </template>

      <el-descriptions :column="3" border>
        <el-descriptions-item label="导入总数">{{ importResult.totalCount }}</el-descriptions-item>
        <el-descriptions-item label="成功数量">
          <el-tag type="success">{{ importResult.successCount }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="失败数量">
          <el-tag type="danger">{{ importResult.failCount }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 统计数据 -->
      <div v-if="importResult.statistics" class="stats-section">
        <h4>成绩统计</h4>
        <el-descriptions :column="4" border>
          <el-descriptions-item label="平均分">{{ importResult.statistics.avgScore }}</el-descriptions-item>
          <el-descriptions-item label="最高分">{{ importResult.statistics.maxScore }}</el-descriptions-item>
          <el-descriptions-item label="最低分">{{ importResult.statistics.minScore }}</el-descriptions-item>
          <el-descriptions-item label="及格率">{{ importResult.statistics.passRate }}%</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 失败原因 -->
      <div v-if="importResult.failReasons && importResult.failReasons.length > 0" class="fail-section">
        <h4>失败原因</h4>
        <el-table :data="failReasonData" stripe border size="small">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="reason" label="失败原因" />
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTeacherCourses, importScores } from '../../api'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const courseList = ref([])
const selectedCourseId = ref(null)
const selectedFile = ref(null)
const importing = ref(false)
const importResult = ref(null)
const uploadRef = ref(null)

// 失败原因列表转为表格数据
const failReasonData = computed(() => {
  if (!importResult.value || !importResult.value.failReasons) return []
  return importResult.value.failReasons.map(reason => ({ reason }))
})

// 加载课程列表
const loadCourses = async () => {
  try {
    const res = await getTeacherCourses(userStore.userId)
    courseList.value = res.data || []
  } catch (e) {
    console.error('加载课程列表失败', e)
  }
}

// 文件选择变化
const handleFileChange = (file) => {
  selectedFile.value = file.raw
}

// 文件超出数量限制
const handleExceed = () => {
  ElMessage.warning('只能选择一个文件，请先移除已选文件')
}

// 执行导入
const handleImport = async () => {
  if (!selectedCourseId.value) {
    ElMessage.warning('请选择课程')
    return
  }
  if (!selectedFile.value) {
    ElMessage.warning('请选择Excel文件')
    return
  }

  importing.value = true
  importResult.value = null

  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    formData.append('courseId', selectedCourseId.value)
    formData.append('teacherId', userStore.userId)

    const res = await importScores(formData)
    importResult.value = res.data

    if (res.data.failCount === 0) {
      ElMessage.success(`导入成功！共导入 ${res.data.successCount} 条成绩`)
    } else {
      ElMessage.warning(`导入完成：成功 ${res.data.successCount} 条，失败 ${res.data.failCount} 条`)
    }
  } catch (e) {
    console.error('导入失败', e)
    ElMessage.error('导入失败，请检查文件格式')
  } finally {
    importing.value = false
  }
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.page-container {
  padding: 0;
}

.import-card {
  margin-bottom: 20px;
}

.card-header {
  font-weight: 600;
  font-size: 15px;
}

.stats-section {
  margin-top: 20px;
}

.stats-section h4 {
  margin-bottom: 12px;
  color: #303133;
  font-size: 14px;
}

.fail-section {
  margin-top: 20px;
}

.fail-section h4 {
  margin-bottom: 12px;
  color: #F56C6C;
  font-size: 14px;
}
</style>
