<template>
  <div class="container">
    <div class="register">
      <div class="title">用户注册</div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item prop="userName" label="用户名">
          <el-input v-model="form.userName" placeholder="请输入用户名称" clearable></el-input>
        </el-form-item>

        <el-form-item prop="nickName" label="昵称">
          <el-input v-model="form.nickName" placeholder="请输入昵称" clearable></el-input>
        </el-form-item>

        <el-form-item label="性别" prop="sex">
          <el-radio v-model="form.sex" label="M" >男</el-radio>
          <el-radio v-model="form.sex" label="F">女</el-radio>
        </el-form-item>

        <el-form-item prop="password" label="密码">
          <el-input v-model="form.password" placeholder="请输入用户密码" show-password></el-input>
        </el-form-item>

        <el-form-item prop="confirmPassword" label="确认密码">
          <el-input placeholder="请输入确认密码" v-model="form.confirmPassword" show-password></el-input>
        </el-form-item>
      </el-form>

      <div style="margin: 30px 0 20px 30px;">
        <el-button style="width: 100%; color: white;" type="primary" @click="register">注 册</el-button>
      </div>
      <div style="display: flex; align-items: center; margin:10px 0">
        <div style="flex: 1; "></div>
        <div style="flex: 1; text-align: right;" >
          已有账号去 <a href="/user/login">【登录】</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import requestUser from "@/utils/RequestUser.js";

const router = useRouter()

// 表单数据
const form = ref({
  sex: 'M',
})

// 表单引用
const formRef = ref(null)

// 确认密码验证
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.value.password) {
    callback(new Error('密码不一致'))
  } else {
    callback()
  }
}

// 表单验证规则
const rules = ref({
  userName: [
    { required: true, message: '请输入用户名称', trigger: 'blur' }
  ],
  nickName: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 10, message: '长度在6到10个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { min: 6, max: 10, message: '长度在6到10个字符', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
})

// 注册方法
const register = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      console.log(form.value)
      requestUser.post('/index/user/register', form.value).then(res => {
        if (res.code === "000000") {
          ElMessage.success('注册成功')
          router.push('/user/login')
        } else {
          ElMessage.error(res.message)
        }
      }).catch(() => {
        ElMessage.error('注册失败')
      })
    }
  })
}
</script>

<style scoped>
@import '@/assets/css/front/front.css';
</style>