<template>
  <div class="page-container">
    <!-- 筛选栏 -->
    <el-card shadow="hover" class="filter-card">
      <el-form :inline="true">
        <el-form-item label="课程搜索">
          <el-input
            v-model="searchKeyword"
            placeholder="输入课程名称搜索"
            clearable
            :prefix-icon="Search"
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 课程卡片列表 -->
    <el-row :gutter="20">
      <el-col :span="8" v-for="course in filteredCourses" :key="course.id">
        <el-card shadow="hover" class="course-card">
          <template #header>
            <div class="course-header">
              <span class="course-name">{{ course.courseName }}</span>
              <el-tag size="small" :type="course.currentCount >= course.maxStudents ? 'danger' : 'success'">
                {{ course.currentCount >= course.maxStudents ? '已满' : '可选' }}
              </el-tag>
            </div>
          </template>
          <div class="course-info">
            <div class="info-item">
              <span class="label">课程编号：</span>
              <span>{{ course.courseNo }}</span>
            </div>
            <div class="info-item">
              <span class="label">学分：</span>
              <span>{{ course.credit }}</span>
            </div>
            <div class="info-item">
              <span class="label">授课教师：</span>
              <span>{{ course.teacherName || '未知' }}</span>
            </div>
            <div class="info-item">
              <span class="label">教室：</span>
              <span>{{ course.classroom || '待定' }}</span>
            </div>
            <div class="info-item">
              <span class="label">上课时间：</span>
              <span>{{ course.schedule || '待定' }}</span>
            </div>
            <div class="info-item">
              <span class="label">选课人数：</span>
              <span>{{ course.currentCount }} / {{ course.maxStudents }}</span>
            </div>
            <div v-if="course.description" class="info-item desc">
              <span class="label">课程简介：</span>
              <span>{{ course.description }}</span>
            </div>
          </div>
          <template #footer>
            <el-button
              v-if="!isStudentSelected(course.id)"
              type="primary"
              :disabled="course.currentCount >= course.maxStudents"
              @click="handleSelect(course)"
            >
              {{ course.currentCount >= course.maxStudents ? '名额已满' : '选课' }}
            </el-button>
            <el-button v-else type="info" disabled>已选</el-button>
          </template>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="filteredCourses.length === 0" description="暂无可选课程" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getCourseList, getStudentSelections, selectCourse } from '../../api'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const allCourses = ref([])
const mySelections = ref([])
const searchKeyword = ref('')

// 过滤后的课程列表
const filteredCourses = computed(() => {
  if (!searchKeyword.value) return allCourses.value
  const kw = searchKeyword.value.toLowerCase()
  return allCourses.value.filter(c =>
    c.courseName.toLowerCase().includes(kw) ||
    (c.courseNo && c.courseNo.toLowerCase().includes(kw))
  )
})

// 判断学生是否已选某门课程
const isStudentSelected = (courseId) => {
  return mySelections.value.some(s => s.courseId === courseId)
}

// 加载所有课程
const loadCourses = async () => {
  try {
    const res = await getCourseList({ page: 1, size: 100 })
    allCourses.value = res.data.records || []
  } catch (e) {
    console.error('加载课程列表失败', e)
  }
}

// 加载已选课程
const loadSelections = async () => {
  try {
    const res = await getStudentSelections(userStore.userId)
    mySelections.value = res.data || []
  } catch (e) {
    console.error('加载选课列表失败', e)
  }
}

// 搜索
const handleSearch = () => {
  // 搜索通过 computed 自动过滤
}

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = ''
}

// 选课
const handleSelect = (course) => {
  ElMessageBox.confirm(`确定要选择课程"${course.courseName}"吗？`, '确认选课', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    try {
      await selectCourse({
        studentId: Number(userStore.userId),
        courseId: course.id
      })
      ElMessage.success('选课成功！')
      // 刷新数据
      loadCourses()
      loadSelections()
    } catch (e) {
      console.error('选课失败', e)
    }
  }).catch(() => {})
}

onMounted(() => {
  loadCourses()
  loadSelections()
})
</script>

<style scoped>
.page-container {
  padding: 0;
}

.filter-card {
  margin-bottom: 20px;
}

.course-card {
  margin-bottom: 20px;
}

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.course-name {
  font-weight: 600;
  font-size: 15px;
  color: #303133;
}

.course-info {
  font-size: 13px;
  color: #606266;
}

.info-item {
  margin-bottom: 8px;
  line-height: 1.6;
}

.info-item .label {
  color: #909399;
}

.info-item.desc {
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px dashed #e4e7ed;
}
</style>
