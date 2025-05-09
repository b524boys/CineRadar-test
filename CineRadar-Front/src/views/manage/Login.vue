<template>
  <div class="container">
    <div class="login">
      <div class="title">管理员登录</div>
      <el-form ref="formRef" :model="form" :rules="rules">
        <el-form-item prop="userName">
          <el-input prefix-icon="User" placeholder="请输入账号"
                    clearable v-model="form.userName" @keyup.enter="login">
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input prefix-icon="Lock" placeholder="请输入密码"
                    clearable show-password v-model="form.password" @keyup.enter="login">
          </el-input>
        </el-form-item>
      </el-form>

      <div style="margin: 30px 0 20px 0px;">
        <el-button style="width: 100%; color: white" type="primary" @click="login">登 录</el-button>
      </div>
      <div style="display: flex; align-items: center;">
        <div style="flex: 1; "></div>
        <div style="flex: 1; text-align: right; display: none;" >
          还没有账号？请 <a href="/register">注册</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import requestAdmin from "@/utils/RequestAdmin.js";

const router = useRouter()

// 表单数据
const form = ref({
  userName: '',
  password: '',
})

// 表单验证规则
const rules = ref({
  userName: [
    { required: true, message: '请输入账号', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
  ],
})

// 表单引用
const formRef = ref(null)

// 登录方法
const login = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      // 验证通过
      requestAdmin.post('/manage/login', form.value).then((res) => {
        if (res.code === '000000') {
          localStorage.setItem('admin-token', JSON.stringify(res.data))
          ElMessage.success('登录成功')
          router.push('/manage/home')
        } else {
          ElMessage.error(res.message)
        }
      })
    } else {
      ElMessage.warning('请填写完整登录信息')
    }
  })
}
</script>

<style scoped>
@import '@/assets/css/manage/manage.css';
</style>