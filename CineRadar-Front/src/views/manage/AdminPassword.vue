<template>
  <div style="padding: 10px;background-color:#fff;width: 50%">
    <el-form ref="formRef" :model="admin" :rules="rules" label-width="100px" style="padding-right: 50px">
      <el-form-item label="原密码" prop="password">
        <el-input v-model="admin.password" placeholder="原始密码" show-password clearable></el-input>
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="admin.newPassword" placeholder="新密码" show-password clearable></el-input>
      </el-form-item>
      <el-form-item label="确认新密码" prop="confirmPassword">
        <el-input v-model="admin.confirmPassword" placeholder="确认密码" show-password clearable></el-input>
      </el-form-item>
      <div style="text-align: center; margin-bottom: 20px">
        <el-button type="primary" @click="saveData">确认修改</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import requestAdmin from "@/utils/RequestAdmin.js";

const router = useRouter();
const formRef = ref(null);

const admin = reactive({
  password: '',
  newPassword: '',
  confirmPassword: '',
});

const validateNewPassword = (rule, value, callback) => {
  if (value === admin.password) {
    callback(new Error('新密码不能与原密码一样'));
  } else {
    callback();
  }
};

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== admin.newPassword) {
    callback(new Error('两次输入的新密码不一致'));
  } else {
    callback();
  }
};

const rules = reactive({
  password: [
    { required: true, message: '请输入原始密码', trigger: 'blur' },
    { min: 6, max: 10, message: '长度在6到10个字符', trigger: 'blur' },
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 10, message: '长度在6到10个字符', trigger: 'blur' },
    { validator: validateNewPassword, trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { min: 6, max: 10, message: '长度在6到10个字符', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
});

const saveData = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      requestAdmin.post('manage/password', admin).then((res) => {
        if (res.code === '000000') {
          ElMessage.success('修改密码成功, 请重新登录');
          localStorage.removeItem('admin-token');
          router.push('/manage/login');
        } else {
          ElMessage.error(res.message);
        }
      });
    }
  });
};

onMounted(() => {
  Object.assign(admin, JSON.parse(localStorage.getItem('admin-token') || '{}'));
  admin.password = '';
});
</script>

<style scoped>
@import '@/assets/css/manage/manage.css';
</style>