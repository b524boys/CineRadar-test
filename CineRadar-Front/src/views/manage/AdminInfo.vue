<template>
  <div style="background-color:#fff;width: 60%;padding: 10px;">
    <el-form :model="admin" label-width="100px" style="padding-right: 50px" ref="formRef" :rules="rules">
      <el-form-item>
        <div style="display: flex;">
          <img v-if="headImgUrl" :src="headImgUrl" class="avatar">
            <el-upload
                class="avatar-uploader"
                :action="$baseUrl + '/files/minio/upload'"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload">
              <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </div>
      </el-form-item>
      <el-form-item label="用户名" prop="userName">
        <el-input v-model="admin.userName" placeholder="用户名" disabled></el-input>
      </el-form-item>
      <el-form-item label="昵称" prop="nickName">
        <el-input v-model="admin.nickName" placeholder="姓名"></el-input>
      </el-form-item>
      <el-form-item label="电话" prop="phone">
        <el-input v-model="admin.phone" placeholder="电话"></el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="admin.email" placeholder="邮箱"></el-input>
      </el-form-item>
      <div style="text-align: center; margin-bottom: 20px">
        <el-button type="primary" @click="saveData">保 存</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import {ref, onMounted, getCurrentInstance} from 'vue'
import { ElMessage } from 'element-plus'
import requestAdmin from "@/utils/RequestAdmin.js";
import EventBus from "@/utils/eventBus.js";

// 管理员信息
const admin = ref(JSON.parse(localStorage.getItem('admin-token') || '{}'))
const headImgUrl = ref('')
const { $baseUrl } = getCurrentInstance().appContext.config.globalProperties;

// 表单引用
const formRef = ref(null)

// 表单验证规则
const rules = ref({
  nickName: [
    { required: true, message: '昵称不能为空', trigger: 'blur' },
  ],
  phone: [
    { message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[34578]\d{9}$/, message: '手机号码格式错误', trigger: ['blur', 'change'] },
  ],
  email: [
    { message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址格式', trigger: ['blur', 'change'] },
  ],
})

// 初始化头像 URL
onMounted(() => {
  if (admin.value.headImg) {
    headImgUrl.value =  admin.value.headImg
  }
})

// 保存数据
const saveData = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      // 更新用户信息
      requestAdmin.post('/admin/update', admin.value).then((res) => {
        if (res.code === '000000') {
          ElMessage.success('操作成功')
          // 更新缓存用户信息
          localStorage.setItem('admin-token', JSON.stringify(admin.value))
          // 触发事件更新父组件数据
          //消息总线: 父级数据更新
          EventBus.emit('refreshAdmin')
        } else {
          ElMessage.error(res.message)
        }
      })
    }
  })
}

// 图片上传前的校验
const beforeAvatarUpload = (file) => {
  const isJPGOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPGOrPng) {
    ElMessage.error('上传头像图片只能是 jpg 或者 png 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
    return false
  }
  return isJPGOrPng && isLt2M
}

// 图片上传成功
const handleAvatarSuccess = (res) => {
  admin.value.headImg = res.data
  headImgUrl.value = admin.value.headImg
}
</script>

<style scoped>
@import '@/assets/css/manage/manage.css';
</style>