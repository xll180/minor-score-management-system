<template>
  <div class="page-container">
    <!-- 筛选栏 -->
    <el-card shadow="hover" class="filter-card">
      <el-form label-width="80px">
        <el-form-item label="选择课程">
          <el-select v-model="selectedCourseId" placeholder="请选择课程" @change="loadBatches" style="width: 300px">
            <el-option v-for="c in courseList" :key="c.id" :label="c.courseName" :value="c.id" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 导入批次列表 -->
    <el-card v-if="batchList.length > 0" shadow="hover" class="batch-card">
      <template #header>
        <span class="card-header">导入批次记录</span>
      </template>
      <el-table :data="batchList" stripe border style="width: 100%" highlight-current-row @current-change="handleBatchSelect">
        <el-table-column prop="id" label="批次ID" width="80" />
        <el-table-column prop="batchName" label="批次名称" min-width="180" />
        <el-table-column prop="totalCount" label="导入总数" width="90" />
        <el-table-column prop="successCount" label="成功数" width="80" />
        <el-table-column prop="failCount" label="失败数" width="80" />
        <el-table-column prop="avgScore" label="平均分" width="80" />
        <el-table-column prop="passRate" label="及格率" width="80">
          <template #default="{ row }">{{ row.passRate }}%</template>
        </el-table-column>
        <el-table-column prop="createTime" label="导入时间" width="170" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handlePreview(row)">预览报告</el-button>
            <el-button type="success" link :icon="Download" @click="handleExportWord(row)">导出Word</el-button>
            <el-button type="warning" link :icon="Download" @click="handleExportPdf(row)">导出PDF</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-empty v-else-if="selectedCourseId" description="该课程暂无导入批次记录" />
    <el-empty v-else description="请先选择课程查看导入批次" />

    <!-- AI报告预览弹窗 -->
    <el-dialog v-model="reportDialogVisible" title="AI教学分析报告" width="800px" top="5vh">
      <div v-if="reportLoading" class="report-loading">
        <el-icon class="is-loading" :size="32"><Loading /></el-icon>
        <p>AI正在生成分析报告，请稍候...</p>
      </div>
      <div v-else class="report-content">
        <pre class="report-text">{{ reportContent }}</pre>
      </div>
      <template #footer>
        <el-button @click="reportDialogVisible = false">关闭</el-button>
        <el-button type="success" :icon="Download" @click="handleExportWordFromDialog">导出Word</el-button>
        <el-button type="warning" :icon="Download" @click="handleExportPdfFromDialog">导出PDF</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { View, Download, Loading } from '@element-plus/icons-vue'
import {
  getTeacherCourses, getImportBatches, previewReport,
  exportWord, exportPdf
} from '../../api'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const courseList = ref([])
const selectedCourseId = ref(null)
const batchList = ref([])

// 报告弹窗相关
const reportDialogVisible = ref(false)
const reportLoading = ref(false)
const reportContent = ref('')
const currentBatchId = ref(null)

// 加载课程列表
const loadCourses = async () => {
  try {
    const res = await getTeacherCourses(userStore.userId)
    courseList.value = res.data || []
  } catch (e) {
    console.error('加载课程列表失败', e)
  }
}

// 加载导入批次
const loadBatches = async () => {
  if (!selectedCourseId.value) {
    batchList.value = []
    return
  }
  try {
    const res = await getImportBatches(selectedCourseId.value)
    batchList.value = res.data || []
  } catch (e) {
    console.error('加载批次列表失败', e)
  }
}

// 选中批次行
const handleBatchSelect = (row) => {
  if (row) {
    currentBatchId.value = row.id
  }
}

// 预览AI报告
const handlePreview = async (row) => {
  currentBatchId.value = row.id
  reportDialogVisible.value = true
  reportLoading.value = true
  reportContent.value = ''

  try {
    const res = await previewReport(row.id)
    reportContent.value = res.data.report || '暂无报告内容'
  } catch (e) {
    reportContent.value = '报告生成失败：' + (e.message || '未知错误')
  } finally {
    reportLoading.value = false
  }
}

// 导出Word
const handleExportWord = async (row) => {
  try {
    const res = await exportWord(row.id)
    downloadFile(res, `report_${row.id}.doc`)
    ElMessage.success('Word文档导出成功')
  } catch (e) {
    ElMessage.error('导出失败')
  }
}

// 导出PDF
const handleExportPdf = async (row) => {
  try {
    const res = await exportPdf(row.id)
    downloadFile(res, `report_${row.id}.pdf`)
    ElMessage.success('PDF文档导出成功')
  } catch (e) {
    ElMessage.error('导出失败')
  }
}

// 弹窗内导出Word
const handleExportWordFromDialog = async () => {
  if (!currentBatchId.value) return
  try {
    const res = await exportWord(currentBatchId.value)
    downloadFile(res, `report_${currentBatchId.value}.doc`)
    ElMessage.success('Word文档导出成功')
  } catch (e) {
    ElMessage.error('导出失败')
  }
}

// 弹窗内导出PDF
const handleExportPdfFromDialog = async () => {
  if (!currentBatchId.value) return
  try {
    const res = await exportPdf(currentBatchId.value)
    downloadFile(res, `report_${currentBatchId.value}.pdf`)
    ElMessage.success('PDF文档导出成功')
  } catch (e) {
    ElMessage.error('导出失败')
  }
}

// 通用文件下载函数
const downloadFile = (blobData, fileName) => {
  const blob = new Blob([blobData])
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = fileName
  link.click()
  URL.revokeObjectURL(link.href)
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.page-container {
  padding: 0;
}

.filter-card {
  margin-bottom: 20px;
}

.batch-card {
  margin-bottom: 20px;
}

.card-header {
  font-weight: 600;
  font-size: 15px;
}

.report-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  color: #909399;
}

.report-loading p {
  margin-top: 16px;
  font-size: 14px;
}

.report-content {
  max-height: 60vh;
  overflow-y: auto;
}

.report-text {
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'Microsoft YaHei', sans-serif;
  font-size: 14px;
  line-height: 1.8;
  color: #303133;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}
</style>
