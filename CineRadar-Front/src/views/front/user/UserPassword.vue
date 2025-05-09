<template>
  <div style="width: 50%">
    <h3>设置密码</h3>
    <el-form ref="formRef" :model="user" :rules="rules" label-width="100px" style="padding-right: 50px; margin-top: 30px;">
      <el-form-item label="原密码" prop="password">
        <el-input v-model="user.password" placeholder="原始密码"  show-password clearable></el-input>
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="user.newPassword" placeholder="新密码" show-password clearable></el-input>
      </el-form-item>
      <el-form-item label="确认新密码" prop="confirmPassword">
        <el-input v-model="user.confirmPassword" placeholder="确认密码" show-password clearable></el-input>
      </el-form-item>
      <div style="text-align: center; margin-bottom: 20px">
        <el-button type="primary" @click="saveData">确认修改</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import requestUser from "@/utils/RequestUser.js";
import {useUserStore} from "@/stores/user.js";

const userStore = useUserStore();
const router = useRouter()
const formRef = ref(null)
const user = ref(userStore.getUserInfo)
user.value.password = '' // 将密码重置为空

const validateNewPassword = (rule, value, callback) => {
  if (value === user.value.password) {
    callback(new Error('新密码不能与原密码一样'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== user.value.newPassword) {
    callback(new Error('两次输入的新密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  password: [
    { required: true, message: '请输入原始密码', trigger: 'blur' },
    { min: 6, max: 10, message: '长度在6到10个字符', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 10, message: '长度在6到10个字符', trigger: 'blur' },
    { validator: validateNewPassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { min: 6, max: 10, message: '长度在6到10个字符', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
}

const saveData = async () => {
  try {
    await formRef.value.validate()
    const res = await requestUser.post('/index/user/updatePassword', user.value)
    if (res.code === '000000') {
      ElMessage.success('修改密码成功, 请重新登录')
      localStorage.removeItem('user-token')
      router.push('/user/login')
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    // 验证失败或请求失败
  }
}
</script>

<style scoped>
@import '@/assets/css/front/front.css';
</style>