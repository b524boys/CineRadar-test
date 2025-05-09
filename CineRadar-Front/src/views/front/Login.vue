<template>
  <div class="container">
    <div class="login">
      <div class="title">用户登录</div>
      <el-form ref="formRef" :model="form" :rules="rules">
        <el-form-item prop="userName">
          <el-input prefix-icon="User" placeholder="请输入账号" clearable v-model="form.userName"></el-input>
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
      <div style="display: flex; align-items: center; margin:10px 0">
        <div style="flex: 1; "></div>
        <div style="flex: 1; text-align: right;" >
          还没有账号？请 <a href="/user/register">【注册】</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import requestUser from "@/utils/RequestUser.js";
import {useTokenStore} from "@/stores/token.js";
import {useUserStore} from "@/stores/user.js";
const userStore = useUserStore();
const tokenStore = useTokenStore();

// 路由实例
const router = useRouter();
// 表单引用
const formRef = ref(null);
// 表单数据
const form = ref({
  userName: '',
  password: '',
});
// 表单验证规则
const rules = ref({
  userName: [
    { required: true, message: '请输入账号', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
  ],
});
// 登录方法
const login = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      requestUser.post('/index/user/login', form.value).then((res) => {
        if (res.code === '000000') {
          ElMessage.success('登录成功');
          console.log(res);
          //localStorage.setItem('user-token', JSON.stringify(res.data));
          userStore.setUserInfo(res.data)
          tokenStore.setToken(res.data.token)
          router.push('/');
        } else {
          ElMessage.error('登录失败');
        }
      });
    }
  });
};

</script>

<style scoped>
@import '@/assets/css/front/front.css';
</style>