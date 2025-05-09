<template>
  <div>
    <h3>我的评论</h3>

    <div class="search">
      <el-input placeholder="请输入电影名称关键词" v-model="goodsName"></el-input>
      <el-input placeholder="请输入评论内容关键词" v-model="content"></el-input>
      <el-button type="info" plain @click="loadData(1)">查询</el-button>
      <el-button type="warning" plain @click="reset">重置</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe>
        <el-table-column prop="id" label="编号"></el-table-column>
        <el-table-column prop="parentId" label="父编号"></el-table-column>
        <el-table-column prop="goodsName" label="电影名称"></el-table-column>
        <el-table-column prop="content" label="评论"></el-table-column>
        <el-table-column prop="rate" label="评分"></el-table-column>
        <el-table-column prop="level" label="评论级别">
          <template v-slot="scope">
            {{scope.row.level === 1 ? '一级评论' : '二级评论'}}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template v-slot="scope">
            <el-button size="small" type="primary" plain @click="editDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" plain @click="deleteData(scope.row)">删除</el-button>
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

    <el-dialog :title="form.id ? '修改评论' : '新增评论'" v-model="dialogVisible" width="50%" :close-on-click-modal="false" @close="onClose">
      <el-form :model="form" label-width="100px" style="padding-right: 50px;" ref="formRef" :rules="rules">
        <el-form-item label="用户名">
          <el-input v-model="form.userName" placeholder="用户名" :readonly="true"></el-input>
        </el-form-item>
        <el-form-item label="电影名称">
          <el-input v-model="form.goodsName" placeholder="密码" :readonly="true"></el-input>
        </el-form-item>
        <el-form-item label="评分" v-if="form.level===1">
          <el-rate v-model="form.rate" show-score :max="10" style="margin-top: 8px;" :allow-half="true"></el-rate>
        </el-form-item>
        <el-form-item label="评论" prop="content">
          <el-input v-model="form.content" placeholder="评论" type="textarea" :rows="3"></el-input>
        </el-form-item>
        <el-form-item label="图片" v-if="form.level===1">
          <div style="display: flex;">
            <el-upload
                class="avatar-uploader"
                :action="$baseUrl + '/files/minio/upload'"
                list-type="picture-card"
                :file-list="fileList"
                multiple
                :on-remove="handleRemove"
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
import { Plus } from '@element-plus/icons-vue'
import requestUser from "@/utils/RequestUser.js";
const { $baseUrl } = getCurrentInstance().appContext.config.globalProperties;

const goodsName = ref(null)
const content = ref(null)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const form = ref({})
const fileList = ref([])  //关联附件
const formRef = ref(null)

const rules = {
  content: [{ required: true, message: '评论内容不能为空', trigger: 'blur' }]
}

onMounted(() => {
  loadData(1)
})

const reset = () => {
  goodsName.value = null
  content.value = null
  loadData(1)
}

const loadData = async (pageNum) => {
  try {
    const res = await requestUser.get('/index/comments/selectCommentsByPage', {
      params: {
        pageNum: pageNum,
        pageSize: pageSize.value,
        goodsName: goodsName.value,
        content: content.value,
      }
    })
    tableData.value = res.data?.list
    total.value = res.data?.total
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const deleteData = async (row) => {
  try {
    await ElMessageBox.confirm(
        row.level === 1 ? '删除一级评论会连带删除其下的所有二级评论？' : '确定要删除吗',
        '确认删除',
        { type: 'warning' }
    )
    const res = await requestUser.get('/comments/delete/' + row.id)
    if (res.code === '000000') {
      ElMessage.success('删除成功')
      loadData(1)
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    // 用户取消删除操作
  }
}

const pageChange = (newPageNum) => {
  pageNum.value = newPageNum;
  loadData(newPageNum);
};

const editDialog = (row) => {
  form.value = JSON.parse(JSON.stringify(row))
  //回显图片
  if (form.value.attachList) {
    fileList.value = form.value.attachList.map(attach => ({
      url: attach.attachFile
    }))
  }
  dialogVisible.value = true
}

const saveData = async () => {
  try {
    await formRef.value.validate()
    const res = await requestUser.post(
        form.value.id ? '/comments/update' : '/comments/add',
        form.value
    )
    if (res.code === '000000') {
      ElMessage.success('操作成功')
      loadData(1)
      dialogVisible.value = false
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    // 验证失败或请求失败
  }
}

const onClose = () => {
  fileList.value = []
}

const beforeAvatarUpload = (file) => {
  const isJPGOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPGOrPng) {
    ElMessage.error('上传图片只能是 jpg 或者 png 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!')
    return false
  }
  return isJPGOrPng && isLt2M
}

const handleRemove = (file, fileList) => {
  form.value.attachList = []
  fileList.forEach((item) => {
    const url = item.response ? item.response.data : item.url
    const urlSplit = url.split('/')
    const attachFile = urlSplit[urlSplit.length - 1]
    form.value.attachList.push({ attachFile })
  })
}

const handleAvatarSuccess = (response, file, fileList) => {
  // 从URL中提取文件名
  const url = file.response.data;
  const fileName = url.substring(url.lastIndexOf('/') + 1);
  // 使用axios发送删除请求
  requestUser.delete('/files/minio/delete/' + fileName ).then(res => {
    if (res.code === '000000') {
      ElMessage.success('删除成功')
    } else {
      ElMessage.error('删除失败')
    }
  })
}
</script>

<style scoped>
@import '@/assets/css/front/front.css';
</style>