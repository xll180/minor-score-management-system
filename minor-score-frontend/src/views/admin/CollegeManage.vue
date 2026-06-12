<template>
  <div class="page-container">
    <!-- 操作栏 -->
    <div class="toolbar">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增学院</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table :data="tableData" stripe border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="collegeName" label="学院名称" min-width="150" />
      <el-table-column prop="description" label="学院描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="dean" label="院长" width="120" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑学院' : '新增学院'"
      width="500px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="学院名称" prop="collegeName">
          <el-input v-model="formData.collegeName" placeholder="请输入学院名称" />
        </el-form-item>
        <el-form-item label="学院描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入学院描述"
          />
        </el-form-item>
        <el-form-item label="院长" prop="dean">
          <el-input v-model="formData.dean" placeholder="请输入院长姓名" />
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
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getCollegeList, addCollege, updateCollege, deleteCollege } from '../../api'

const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

// 表单数据
const formData = reactive({
  id: null,
  collegeName: '',
  description: '',
  dean: ''
})

// 表单校验规则
const rules = {
  collegeName: [{ required: true, message: '请输入学院名称', trigger: 'blur' }]
}

// 加载数据
const loadData = async () => {
  try {
    const res = await getCollegeList()
    tableData.value = res.data || []
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
    id: row.id,
    collegeName: row.collegeName,
    description: row.description,
    dean: row.dean
  })
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除学院"${row.collegeName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCollege(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (e) {
      console.error('删除失败', e)
    }
  }).catch(() => {})
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateCollege(formData)
      ElMessage.success('更新成功')
    } else {
      await addCollege(formData)
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

// 重置表单
const resetForm = () => {
  Object.assign(formData, { id: null, collegeName: '', description: '', dean: '' })
  formRef.value && formRef.value.resetFields()
}

onMounted(() => {
  loadData()
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
