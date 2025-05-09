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
      <el-table :data="tableData" stripe @selection-change="selectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="userName" label="账号"></el-table-column>
        <el-table-column prop="password" label="密码"></el-table-column>
        <el-table-column prop="nickName" label="昵称"></el-table-column>
        <el-table-column prop="phone" label="手机号码"></el-table-column>
        <el-table-column prop="email" label="邮箱"></el-table-column>
        <el-table-column prop="role" label="角色"></el-table-column>
        <el-table-column label="头像">
          <template v-slot="scope">
            <el-image style="width: 40px; height: 40px; border-radius: 20%;" v-if="scope.row.headImg"
                      :src="scope.row.headImg"
                      :preview-src-list="[scope.row.headImg]">
            </el-image>
          </template>
        </el-table-column>
        <el-table-column label="操作">
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
            :page-sizes="[5,10,20,30,50]"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="total">
        </el-pagination>
      </div>
    </div>

    <el-dialog title="管理员" v-model="dialogVisible" width="50%" :close-on-click-modal="false" @close="onClose">
      <el-form :model="form" label-width="100px" style="padding-right: 50px;" ref="formRef" :rules="rules">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="form.userName" placeholder="用户名" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" placeholder="密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="昵称" prop="nickName">
          <el-input v-model="form.nickName" placeholder="昵称"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="手机号"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="邮箱"></el-input>
        </el-form-item>
        <el-form-item label="头像" prop="headImg">
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
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveData">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, onMounted, getCurrentInstance} from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import requestAdmin from "@/utils/RequestAdmin.js";
import eventBus from "@/utils/eventBus.js";

const router = useRouter()
// 当前登录管理员信息
const admin = ref(JSON.parse(localStorage.getItem('admin-token') || '{}'))
const { $baseUrl } = getCurrentInstance().appContext.config.globalProperties

// 搜索条件
const userName = ref(null)
const nickName = ref(null)

// 表格数据
const tableData = ref([])  //数据集
const pageNum = ref(1)  //当前的页码
const pageSize = ref(10)  //每页显示的个数
const total = ref(0)  //总条数

// 对话框状态
const dialogVisible = ref(false)
//表单
const form = ref({})
const formRef = ref(null)
const headImgUrl = ref('')
// 批量删除的 ID 数组
const ids = ref([])  //元素id数组, 用于删除

// 表单验证规则
const rules = ref({
  userName: [
    { required: true, message: '用户名不能为空', trigger: 'blur' },
  ],
  nickName: [
    { required: true, message: '昵称不能为空', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '密码不能为空', trigger: 'blur' },
    { min: 6, max: 10, message: '长度在6到10个字符', trigger: 'blur' },
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

// 加载数据
const loadData = (pageNum) => {
  //pageNum.value = pageNum
  requestAdmin
      .get('/admin/selectByPage', {
        params: {
          //pageNum: pageNum.value,
          pageNum: pageNum,
          pageSize: pageSize.value,
          userName: userName.value,
          nickName: nickName.value,
        },
      })
      .then((res) => {
        console.log(res.data)
        if (res.code === '000000') {
          tableData.value = res.data?.list || []
          total.value = res.data?.total || 0
        } else {
          ElMessage.error(res.message)
        }
      })
}

// 重置搜索条件
const reset = () => {
  userName.value = null
  nickName.value = null
  loadData(1)
}

// 表格多选
const selectionChange = (rows) => {
  ids.value = rows.map((item) => item.id)
}

// 打开新增对话框
const addDialog = () => {
  form.value = {}
  headImgUrl.value = ''
  dialogVisible.value = true
}

// 打开编辑对话框
const editDialog = (row) => {
  form.value = JSON.parse(JSON.stringify(row))
  headImgUrl.value = form.value.headImg ? form.value.headImg : ''
  dialogVisible.value = true
}

// 保存数据
const saveData = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      const url = form.value.id ? '/admin/update' : '/admin/add'
      requestAdmin.post(url, form.value).then((res) => {
        if (res.code === '000000') {
          ElMessage.success('操作成功')
          loadData(1)
          dialogVisible.value = false

          // 更新当前登录管理员信息
          if (admin.value.id === form.value.id) {
            const token = admin.value.token
            Object.assign(admin.value, form.value)
            admin.value.token = token
            localStorage.setItem('admin-token', JSON.stringify(admin.value))
            // 触发事件更新父组件数据
            eventBus.emit('refreshAdmin')
          }
        } else {
          ElMessage.error(res.message)
        }
      })
    }
  })
}

// 删除数据
const deleteData = (id) => {
  ElMessageBox.confirm('您确定要删除吗？', '确认删除', { type: 'warning' })
      .then(() => {
        requestAdmin.get(`/admin/delete/${id}`).then((res) => {
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

// 批量删除
const deleteBatch = () => {
  if (!ids.value.length) {
    ElMessage.warning('请选择需要批量删除的数据')
    return
  }
  ElMessageBox.confirm('您确定批量删除这些数据吗？', '确认删除', { type: 'warning' })
      .then(() => {
        requestAdmin.post('/admin/delete/batch', ids.value).then((res) => {
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

// 分页切换
const pageChange = (newPageNum) => {
  pageNum.value = newPageNum;
  loadData(newPageNum);
};

// 关闭对话框
const onClose = () => {
  form.value = {}
  headImgUrl.value = ''
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
  form.value.headImg = res.data
  headImgUrl.value = form.value.headImg
}

// 初始化加载数据
onMounted(() => {
  loadData(1)
})

</script>

<style scoped>
@import '@/assets/css/manage/manage.css';
</style>