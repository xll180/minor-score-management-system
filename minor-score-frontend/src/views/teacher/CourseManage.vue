<template>
  <div class="page-container">
    <!-- 操作栏 -->
    <div class="toolbar">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增课程</el-button>
    </div>

    <!-- 课程表格 -->
    <el-table :data="courseList" stripe border style="width: 100%">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="courseNo" label="课程编号" width="110" />
      <el-table-column prop="courseName" label="课程名称" min-width="140" />
      <el-table-column prop="credit" label="学分" width="70" />
      <el-table-column prop="maxStudents" label="最大人数" width="90" />
      <el-table-column prop="currentCount" label="已选人数" width="90" />
      <el-table-column prop="semester" label="学期" width="120" />
      <el-table-column prop="classroom" label="教室" width="90" />
      <el-table-column prop="schedule" label="上课时间" width="120" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
          <el-button type="success" link :icon="View" @click="viewStudents(row)">查看学生</el-button>
          <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-box">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
      />
    </div>

    <!-- 查看选课学生弹窗 -->
    <el-dialog v-model="studentDialogVisible" :title="`选课学生 - ${currentCourseName}`" width="650px">
      <el-table :data="studentList" stripe style="width: 100%">
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="110" />
        <el-table-column prop="createTime" label="选课时间" width="170" />
      </el-table>
      <el-empty v-if="studentList.length === 0" description="暂无学生选课" />
    </el-dialog>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑课程' : '新增课程'"
      width="600px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="课程编号" prop="courseNo">
          <el-input v-model="formData.courseNo" placeholder="请输入课程编号" />
        </el-form-item>
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="formData.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="学分" prop="credit">
          <el-input-number v-model="formData.credit" :min="0.5" :max="10" :step="0.5" />
        </el-form-item>
        <el-form-item label="最大人数" prop="maxStudents">
          <el-input-number v-model="formData.maxStudents" :min="1" :max="500" />
        </el-form-item>
        <el-form-item label="所属学院" prop="collegeId">
          <el-select v-model="formData.collegeId" placeholder="请选择学院" style="width: 100%">
            <el-option v-for="item in collegeList" :key="item.id" :label="item.collegeName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-input v-model="formData.semester" placeholder="如：2025-2026-1" />
        </el-form-item>
        <el-form-item label="教室" prop="classroom">
          <el-input v-model="formData.classroom" placeholder="如：A301" />
        </el-form-item>
        <el-form-item label="上课时间" prop="schedule">
          <el-input v-model="formData.schedule" placeholder="如：周一3-4节" />
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="2" placeholder="请输入课程描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, View } from '@element-plus/icons-vue'
import {
  getCourseList, addCourse, updateCourse, deleteCourse,
  getCourseStudents, getCollegeList
} from '../../api'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const courseList = ref([])
const collegeList = ref([])
const pagination = reactive({ page: 1, size: 10, total: 0 })

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

// 查看学生相关
const studentDialogVisible = ref(false)
const currentCourseName = ref('')
const studentList = ref([])

// 表单数据
const formData = reactive({
  id: null,
  courseNo: '',
  courseName: '',
  description: '',
  credit: 3,
  maxStudents: 60,
  collegeId: null,
  semester: '',
  classroom: '',
  schedule: ''
})

const rules = {
  courseNo: [{ required: true, message: '请输入课程编号', trigger: 'blur' }],
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  collegeId: [{ required: true, message: '请选择所属学院', trigger: 'change' }]
}

// 加载课程列表（仅当前教师的课程）
const loadData = async () => {
  try {
    const res = await getCourseList({ page: pagination.page, size: pagination.size })
    // 过滤出当前教师的课程
    const allCourses = res.data.records || []
    courseList.value = allCourses.filter(c => String(c.teacherId) === String(userStore.userId))
    pagination.total = courseList.value.length
  } catch (e) {
    console.error('加载课程列表失败', e)
  }
}

// 加载学院列表
const loadColleges = async () => {
  try {
    const res = await getCollegeList()
    collegeList.value = res.data || []
  } catch (e) {
    console.error('加载学院列表失败', e)
  }
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(formData, {
    id: row.id, courseNo: row.courseNo, courseName: row.courseName,
    description: row.description, credit: row.credit, maxStudents: row.maxStudents,
    collegeId: row.collegeId, semester: row.semester, classroom: row.classroom,
    schedule: row.schedule
  })
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除课程"${row.courseName}"吗？`, '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    try {
      await deleteCourse(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (e) {
      console.error('删除失败', e)
    }
  }).catch(() => {})
}

// 查看选课学生
const viewStudents = async (row) => {
  currentCourseName.value = row.courseName
  studentDialogVisible.value = true
  try {
    const res = await getCourseStudents(row.id)
    studentList.value = res.data || []
  } catch (e) {
    console.error('加载学生列表失败', e)
  }
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    const submitData = { ...formData, teacherId: Number(userStore.userId) }
    if (isEdit.value) {
      await updateCourse(submitData)
      ElMessage.success('更新成功')
    } else {
      await addCourse(submitData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    console.error('提交失败', e)
  } finally {
    submitLoading.value = false
  }
}

const resetForm = () => {
  Object.assign(formData, {
    id: null, courseNo: '', courseName: '', description: '',
    credit: 3, maxStudents: 60, collegeId: null, semester: '', classroom: '', schedule: ''
  })
  formRef.value && formRef.value.resetFields()
}

onMounted(() => {
  loadData()
  loadColleges()
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
.pagination-box {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
