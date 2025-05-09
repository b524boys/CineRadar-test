<template>
  <div style="width: 60%">
    <h3>个人信息</h3>
    <el-form :model="user" label-width="100px" style="padding-right: 50px; margin-top: 30px;" :rules="rules" ref="formRef">
      <el-form-item label="账号" prop="userName">
        <el-input v-model="user.userName" placeholder="用户名" disabled></el-input>
      </el-form-item>
      <el-form-item label="昵称" prop="nickName">
        <el-input v-model="user.nickName" placeholder="昵称"></el-input>
      </el-form-item>
      <el-form-item label="性别" prop="sex">
        <el-radio v-model="user.sex" label="M">男</el-radio>
        <el-radio v-model="user.sex" label="F">女</el-radio>
      </el-form-item>
      <el-form-item label="手机号码" prop="phone">
        <el-input v-model="user.phone" placeholder="手机号码"></el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="user.email" placeholder="邮箱"></el-input>
      </el-form-item>

      <el-form-item label="头像">
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
      <el-form-item>
        <el-button type="primary" @click="save">确认修改</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>


<script setup>
import {ref, onMounted, getCurrentInstance} from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import requestUser from "@/utils/RequestUser.js";
import EventBus from "@/utils/eventBus.js";  // 假设你使用了事件总线
import { useUserStore } from "@/stores/user.js";
const { $baseUrl } = getCurrentInstance().appContext.config.globalProperties;

const userStore = useUserStore()
const formRef = ref(null)
// const user = ref([])
const user = ref({ ...userStore.getUserInfo });
const headImgUrl = ref('')

const rules = {
  userName: [
    { required: true, message: '请输入用户名称', trigger: 'blur' }
  ],
  nickName: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  email: [
    { message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址格式', trigger: ['blur', 'change'] }
  ],
  phone: [
    { message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[34578]\d{9}$/, message: '手机号码格式错误', trigger: ['blur', 'change'] }
  ],
}

onMounted(() => {
  if (user.value.headImg) {
    headImgUrl.value = user.value.headImg
  }
})

const save = async () => {
  try {
    await formRef.value.validate()
    const res = await requestUser.post('/index/user/updateInfo', user.value)
    if (res.code === '000000') {
      ElMessage.success('修改成功')
      userStore.setUserInfo(user.value)
      // localStorage.setItem('user-token', JSON.stringify(user.value))
      // EventBus.emit('refreshUser') // 触发事件总线更新父级数据
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    // 验证失败或请求失败
  }
}

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

const handleAvatarSuccess = (res) => {
  user.value.headImg = res.data
  headImgUrl.value = user.value.headImg
}
</script>

<style scoped>
@import '@/assets/css/front/front.css';
</style>