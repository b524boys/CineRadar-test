<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入账号查询" v-model="userName"></el-input>
      <el-input placeholder="请输入昵称查询" v-model="nickName"></el-input>
      <el-button type="info" plain @click="loadData(1)">查询</el-button>
      <el-button type="warning" plain @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="primary" plain @click="addDialog">新增</el-button>
      <el-button type="danger" plain @click="deleteBatch">批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" strip @selection-change="selectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="userName" label="账号"></el-table-column>
        <el-table-column prop="nickName" label="昵称"></el-table-column>
        <el-table-column prop="sex" label="性别" :formatter=formatSex></el-table-column>
        <el-table-column prop="phone" label="手机号码"></el-table-column>
        <el-table-column prop="email" label="邮箱"></el-table-column>
        <el-table-column label="头像">
          <template v-slot="scope">
            <div style="display: flex; align-items: center">
              <el-image style="width: 40px; height: 40px; border-radius: 20%" v-if="scope.row.headImg"
                        :src="scope.row.headImg" :preview-src-list="[scope.row.headImg]"></el-image>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="180">
          <template v-slot="scope">
            <el-button size="small" type="primary" plain @click="editDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" plain @click="deleteData(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
            @current-change="pageChange"
            :current-page="pageNum"
            :page-sizes="[5, 10, 20]"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="total">
        </el-pagination>
      </div>
    </div>

    <el-dialog title="用户" v-model="dialogVisible" width="50%" :close-on-click-modal="false" destroy-on-close @close="onClose">
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="账号" prop="userName">
          <el-input v-model="form.userName" placeholder="用户名" clearable></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" placeholder="密码" clearable show-password></el-input>
        </el-form-item>
        <el-form-item label="昵称" prop="nickName">
          <el-input v-model="form.nickName" placeholder="昵称" clearable></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio v-model="form.sex" label="M">男</el-radio>
          <el-radio v-model="form.sex" label="F">女</el-radio>
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="form.phone" placeholder="手机号码"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="邮箱"></el-input>
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
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveData">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted, getCurrentInstance} from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import requestAdmin from "@/utils/RequestAdmin.js";

// 响应式数据
const userName = ref(null)
const nickName = ref(null)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const form = reactive({ sex: 'M' })
const formRef = ref(null)
const ids = ref([])
const headImgUrl = ref('')
const { $baseUrl } = getCurrentInstance().appContext.config.globalProperties;

// 验证规则
const rules = reactive({
  userName: [
    { required: true, message: '账号不能为空', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '密码不能为空', trigger: 'blur' },
    { min:6, max:10, message:'长度在6到10个字符', trigger: 'blur' }
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
  ]
})

// 生命周期
onMounted(() => {
  loadData(1)
})

// 方法
const reset = () => {
  userName.value = null
  nickName.value = null
  loadData(1)
}

const loadData = (pageNumVal) => {
  requestAdmin.get('/user/selectByPage', {
    params: {
      pageNum: pageNumVal,
      pageSize: pageSize.value,
      userName: userName.value,
      nickName: nickName.value
    }
  }).then(res => {
    if (res.code === '000000') {
      tableData.value = res.data?.list || []
      total.value = res.data?.total || 0
    } else {
      ElMessage.error(res.message)
    }
  })
}

const selectionChange = (rows) => {
  ids.value = rows.map(item => item.id)
}

const addDialog = () => {
  dialogVisible.value = true
}

const editDialog = (row) => {
  Object.assign(form, JSON.parse(JSON.stringify(row)))
  headImgUrl.value = row.headImg
  dialogVisible.value = true
}

const saveData = () => {
  formRef.value.validate(valid => {
    if (valid) {
      const url = form.id ? '/user/update' : '/user/add'
      requestAdmin.post(url, form).then(res => {
        if (res.code === '000000') {
          ElMessage.success('操作成功')
          loadData(1)
          dialogVisible.value = false
        } else {
          ElMessage.error(res.message)
        }
      })
    }
  })
}

const deleteData = (id) => {
  ElMessageBox.confirm('您确定删除吗？', '确认删除', { type: 'warning' })
      .then(() => {
        requestAdmin.get(`/user/delete/${id}`).then(res => {
          if (res.code === '000000') {
            ElMessage.success('删除成功')
            loadData(1)
          } else {
            ElMessage.error(res.message)
          }
        })
      })
      .catch(() => {})
}

const deleteBatch = () => {
  if (!ids.value.length) {
    ElMessage.warning('请选择需要批量删除的数据')
    return
  }
  ElMessageBox.confirm('您确定批量删除这些数据吗？', '确认删除', { type: 'warning' })
      .then(() => {
        requestAdmin.post('/user/delete/batch', ids.value).then(res => {
          if (res.code === '000000') {
            ElMessage.success('操作成功')
            loadData(1)
          } else {
            ElMessage.error(res.message)
          }
        })
      })
      .catch(() => {})
}

const onClose = () => {
  Object.keys(form).forEach(key => {
    form[key] = null; // 清空字符串
  });
  Object.assign(form, { sex: 'M' })
  headImgUrl.value = ''
}

const pageChange = (newPageNum) => {
  pageNum.value = newPageNum;
  loadData(newPageNum);
};

const beforeAvatarUpload = (file) => {
  const isJPGOrPng = ['image/jpeg', 'image/png'].includes(file.type)
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
  form.headImg = res.data
  headImgUrl.value = form.headImg
}

const formatSex = (row) => {
  return { M: '男', F: '女' }[row.sex] || '无'
}
</script>

<style scoped>
@import '@/assets/css/manage/manage.css';
</style>