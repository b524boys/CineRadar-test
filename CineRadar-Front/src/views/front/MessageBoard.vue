<template>
  <div class="main-container">
    <!--动态圈-->
    <div class="comments-container">
      <div class="comments-title-box">
        <span class="title-tip">所有动态 · · · · · · · · · · · · · · · · · · · · · · · ·  <span style="color:#37a;font-size:15px;">(全部 {{ total }} 条)</span></span>
        <el-button plain type="primary" @click="addMessageBoard(1, null, null)">发布动态</el-button>
      </div>

      <div v-for="(item, index) in tableData" :key="item.id">
        <div class="comments-head">
          <img :src="showImg(item.headImg)" class="img">
          <span class="nickname">{{ item.nickName }}</span>
        </div>
        <div class="comments-content">
          <div>
            <div>
              <span>{{item.content}}</span>
              <el-icon @click="addMessageBoard(2, item.id, item.userId)"><ChatDotRound /></el-icon>
              <el-icon v-if="user.id === item.userId" @click="deleteData(item.id)"><Delete /></el-icon>
            </div>
            <p style="padding: 10px;" v-if="item.attachList && item.attachList.length > 0">
              <el-image v-for="(itemAttach, imgIndex) in item.attachList" :key="itemAttach.id"
                        style="width: 100px; height: 100px; margin-right: 5px;"
                        :src="itemAttach.attachFile"
                        fit="cover">
              </el-image>
            </p>
          </div>
          <ul style="padding-left: 10px;">
            <li v-for="(itemChildren, childrenIndex) in item.children" :key="itemChildren.id" class="comments-head">
              <img :src="showImg(itemChildren.headImg)" class="img">
              <span class="nickname">{{ itemChildren.nickName }}@{{ itemChildren.replyNickName }}:</span>
              <!--<span style="font-weight: bold; color: #333; margin: 0 5px;">回复:</span>-->
              <span>{{ itemChildren.content }}</span>
              <el-icon @click="addMessageBoard(2, item.id, itemChildren.userId)"><ChatDotRound /></el-icon>
              <el-icon v-if="user.id === itemChildren.userId" @click="deleteData(itemChildren.id)"><Delete /></el-icon>
            </li>
          </ul>
        </div>
      </div>
      <!--分页-->
      <div v-if="null != tableData && tableData.length > 0">
        <el-pagination
            @current-change="pageChange"
            :current-page="pageNum"
            :page-sizes="[5,10,20,30,50]"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="total">
        </el-pagination>
      </div>


      <el-dialog :title="'Hi'" v-model="dialogVisible" width="50%" :close-on-click-modal="false" @close="onClose">
        <el-form :model="form" label-width="100px" style="padding-right: 50px;" ref="formRef" :rules="rules">
          <el-form-item label="内容" prop="content">
            <el-input v-model="form.content" placeholder="请输入文本" type="textarea" :rows="3"></el-input>
          </el-form-item>
          <el-form-item label="图片" v-if="form.level===1">
            <div style="display: flex;">
              <el-upload
                  class="avatar-uploader"
                  :action="$baseUrl + '/files/minio/upload'"
                  list-type="picture-card"
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
          <el-button type="primary" @click="saveMessageBoard">确定</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted, getCurrentInstance} from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import requestUser from "@/utils/RequestUser.js";
import {useUserStore} from "@/stores/user.js";

const userStore = useUserStore()
// 响应式数据
const user = ref(userStore.getUserInfo)
const { $baseUrl } = getCurrentInstance().appContext.config.globalProperties;
const form = reactive({})
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(5)
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)

// 验证规则
const rules = reactive({
  content: [
    { required: true, message: '留言内容不能为空', trigger: 'blur' },
    { min: 5, message: '留言字符数不能低于5', trigger: 'blur' }
  ]
})

// 生命周期
onMounted(() => {
  loadData(1)
})

// 方法
const showImg = (imgUrl) => {
  if(imgUrl){
    return imgUrl
  }else{
    return new URL('@/assets/imgs/front/head-default.png', import.meta.url).href
  }
}

const loadData = (pageNumVal) => {
  requestUser.get('/messageBoard/selectByPage', {
    params: {
      pageNum: pageNumVal,
      pageSize: pageSize.value,
      level: 1
    }
  }).then(res => {
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  })
}

const addMessageBoard = (level, parentId, replyUserId) => {
  //用户必须登录后才能发表留言
  if (Object.keys(user.value).length === 0) {
    ElMessage.info("请先登录")
    return
  }

  form.level = level
  if (form.level === 1) {
    //一级留言
    form.parentId = parentId
  } else {
    //二级留言
    form.parentId = parentId
    form.replyUserId = replyUserId
  }
  dialogVisible.value = true
}

const saveMessageBoard = () => {
  formRef.value.validate(valid => {
    if (valid) {
      requestUser.post('index/messageBoard/addMessageBoard', form).then(res => {
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

const pageChange = (newPageNum) => {
  pageNum.value = newPageNum;
  loadData(newPageNum);
};

const onClose = () => {
  Object.assign(form, {})
}

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

const handleRemove = (file) => {
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

const handleAvatarSuccess = (response, file, fileList) => {
  form.attachList = []
  fileList.forEach( item => {
    let attachFile = item.response ? item.response.data : "Failed"
    form.attachList.push({ attachFile })
  })
}

const deleteData = (id) => {
  ElMessageBox.confirm('删除一级留言会连带删除其下的所有二级留言', '确认删除', { type: 'warning' })
      .then(() => {
        requestUser.get(`/messageBoard/delete/${id}`).then(res => {
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
</script>

<style scoped>
@import '@/assets/css/front/front.css';
</style>