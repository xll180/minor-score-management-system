<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header-row">
          <span class="card-header">个人资料</span>
          <el-button v-if="!editing" type="primary" :icon="Edit" @click="startEdit">编辑资料</el-button>
          <div v-else>
            <el-button @click="cancelEdit">取消</el-button>
            <el-button type="primary" :loading="submitting" @click="handleSave">保存</el-button>
          </div>
        </div>
      </template>

      <el-form v-if="profile" :model="profile" :disabled="!editing" label-width="120px" class="profile-form">
        <el-divider content-position="left">基本信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工号">
              <el-input :model-value="profile.teacherNo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户名">
              <el-input :model-value="profile.username" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="真实姓名">
              <el-input v-model="profile.realName" placeholder="请输入真实姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="profile.gender" placeholder="请选择性别" style="width: 100%">
                <el-option :value="1" label="男" />
                <el-option :value="2" label="女" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">联系方式</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input v-model="profile.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="profile.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">所属信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属学院">
              <el-input :model-value="profile.collegeName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-tag :type="profile.status === 1 ? 'success' : 'danger'">
                {{ profile.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Edit } from '@element-plus/icons-vue'
import { getTeacherProfile, updateTeacherProfile } from '../../api'

const loading = ref(true)
const submitting = ref(false)
const editing = ref(false)
const profile = ref(null)

const fetchProfile = async () => {
  loading.value = true
  try {
    const res = await getTeacherProfile()
    profile.value = res.data
  } catch (e) {
    ElMessage.error('获取个人资料失败')
  } finally {
    loading.value = false
  }
}

const startEdit = () => { editing.value = true }
const cancelEdit = () => { editing.value = false; fetchProfile() }

const handleSave = async () => {
  submitting.value = true
  try {
    await updateTeacherProfile({
      realName: profile.value.realName,
      gender: profile.value.gender,
      phone: profile.value.phone,
      email: profile.value.email
    })
    ElMessage.success('保存成功')
    editing.value = false
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => { fetchProfile() })
</script>

<style scoped>
.page-container { background: #fff; padding: 20px; border-radius: 8px; }
.card-header-row { display: flex; justify-content: space-between; align-items: center; }
.card-header { font-weight: 600; font-size: 16px; }
.profile-form { max-width: 800px; }
</style>
