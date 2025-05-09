<template>
  <div class="main-container" style="padding: 0px 80px 0px 80px">
    <div v-if="null == goods">暂无数据</div>
    <div v-else>
      <div class="goods-detail-info-box">
        <div class="cover">
          <el-skeleton style="height: 100%" :loading="loading" animated>
            <template #template>
              <el-skeleton-item class="movie-cover" variant="image" style="height: 100%" />
            </template>
            <template #default>
            <el-image class="movie-cover" :src="goods.cover" fit="cover" />
            </template>
          </el-skeleton>
        </div>

        <div class="field">
          <div class="goods-title">
            <div>
              <span style="color: #494949;margin-right: 15px">{{goods.goodsName}}</span>
              <span style="color: #888">({{goods.year}})</span>
            </div>
          </div>

          <div class="rate">
            <div style="margin-left: 5px;display: flex">
              <div>
              <span>综合评分: </span>
              <span style="font-size: 22px;font-weight: bold;">{{goods.rating}}</span>
              <span> 分</span>
              </div>
              <div class="collect">
                <el-button v-if="!isCollect" round plain type="primary" size="small" @click="addCollect()">＋ 收藏</el-button>
                <el-button v-else round plain type="info" size="small" @click="cancelCollect()">取消收藏</el-button>
              </div>
            </div>
            <el-rate
                v-model="fakeSCore"
                disabled :max="5"
                disabled-void-color="#DDDDDDFF"
                style="margin-top: 8px;margin-left: 0px"
                :allow-half="true"
                class="large-stars"
                :texts="['糟糕:(', '失望:(', '一般', '好看!', '超级好看!!']"
                show-text
                text-color="#494949"
            />
          </div>

          <ul class="goods-property-box">
            <li class="goods-property"><span class="property-name">类型</span><span class="property-value">{{goods.cateName}}</span></li>

            <li class="goods-property"><span class="property-name">演员</span><span class="property-value" style="line-height: 25px;">{{goods.casts}}</span></li>
            <li class="goods-property"><span class="property-name">导演</span><span class="property-value">{{goods.director}}</span></li>
            <li class="goods-property"><span class="property-name">国家</span><span class="property-value">{{goods.country}}</span></li>
            <li class="goods-property"><span class="property-name">时长(分钟)</span><span class="property-value">{{goods.duration}} 分钟</span></li>

            <li class="goods-property"><span class="property-name">点击次数</span><span class="property-value">{{goods.hits}} 次</span></li>
          </ul>
        </div>
      </div>

      <div class="tips-box">
        <el-icon size="28"><Ticket /></el-icon>
        <a class="title">电影详情</a>
      </div>

      <div class="goods-detail-content">
        <div class="video" v-if="goods.videoUrl">
          <video :src="goods.videoUrl" controls width="300px"></video>
<!--          <video :src="$baseUrl + '/files/' + goods.videoUrl" controls width="100%"></video>-->
        </div>
        <div class="desc">
          <div class="introduce" >
            <div v-html="goods.introduce"></div>
          </div>
        </div>
      </div>


    </div>

    <!--评论-->
    <div class="comments-container">
      <div class="comments-title-box">
        <el-icon size="28" style="margin-right: 9px"><ChatLineSquare /></el-icon>
        <span class="title-tip">所有评论 · · · · · · · · · · · · · · · · · · · · · · · · · <span style="color:#37a; font-size:15px;">(全部 {{ total }} 条)</span></span>
        <el-button plain round type="primary" :icon="Edit" @click="addComments(1, null, null)">发布评论</el-button>
      </div>

      <!--评论对话框-->
      <el-dialog :title="'新增评论'" v-model="dialogVisible" width="50%" :close-on-click-modal="false" @close="onClose">
        <el-form :model="form" label-width="100px" style="padding-right: 50px;" ref="formRef" :rules="rules">
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
          <el-button type="primary" @click="saveComments">确定</el-button>
        </div>
      </el-dialog>


      <div v-for="(item, index) in commentsData" :key="item.id">
        <div class="comments-head">
          <img :src="showImg(item.headImg)" class="img">
          <span class="nickname">{{ item.nickName }}</span>
          <el-rate v-model="item.rate" :max="10" :allow-half="true" disabled show-score></el-rate>
        </div>
        <div class="comments-content">
          <div>
            <div>
              <span>{{item.content}}</span>
              <el-icon size="16" @click="addComments(2, item.id, item.userId)" style="margin-left: 10px"><ChatDotRound /></el-icon>
              <el-icon size="16" v-if="user.id === item.userId" @click="deleteData(item.id)" style="margin-left: 10px"><Delete /></el-icon>
            </div>
            <p style="padding: 10px;" v-if="item.attachList && item.attachList.length > 0">
              <el-image v-for="(itemAttach, imgIndex) in item.attachList" :key="itemAttach.id"
                        style="width: 100px; height: 100px; margin-right: 5px;"
                        :src="itemAttach.attachFile"
              >
              </el-image>
            </p>
            <ul style="padding-left: 10px;">
              <li v-for="(itemChildren, childrenIndex) in item.children" :key="itemChildren.id" class="comments-head">
                <img :src="showImg(itemChildren.headImg)" class="img">
                <span class="nickname">{{ itemChildren.nickName }}@{{ itemChildren.replyNickName }}:</span>
                <!--<span style="font-weight: bold; color: #333; margin: 0 5px;">回复:</span>-->
                <span>{{ itemChildren.content }}</span>
                <el-icon @click="addComments(2, item.id, itemChildren.userId)"><ChatDotRound /></el-icon>
                <el-icon v-if="user.id === itemChildren.userId" @click="deleteData(itemChildren.id)"><Delete /></el-icon>
              </li>
            </ul>
          </div>
        </div>
      </div>
      <!--分页-->
      <div v-if="null != commentsData && commentsData.length > 0">
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

  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import requestUser from "@/utils/RequestUser.js";
import {useUserStore} from "@/stores/user.js";
import {Edit} from "@element-plus/icons-vue";

const userStore = useUserStore()
const route = useRoute()
// 电影详情相关数据属性
const goodsId = ref(null)
const goods = ref({})
const fakeSCore = ref(0)
const isCollect = ref(false)
const user = ref(userStore.getUserInfo)
// 评论相关数据属性
const form = ref({})
const dialogVisible = ref(false)
const rules = ref({
  content: [
    { required: true, message: '评论不能为空', trigger: 'blur' },
    { min: 5, message: '评论字符数不能低于5个', trigger: 'blur' }
  ]
})
const commentsData = ref([])
const pageNum = ref(1)
const pageSize = ref(5)
const total = ref(0)
const formRef = ref(null)
const loading = ref(true);

onMounted(() => {
  goodsId.value = route.query.id
  loadGoodsData()
  isCollected()
  addHistory()
  loadCommentsData(1)
})

// 加载电影详情
const loadGoodsData = () => {
  if (goodsId.value) {
    requestUser.get('/index/goods/detail/' + goodsId.value).then(res => {
      console.log(res.data)
      if (res.code === '000000') {
        goods.value = res.data
        fakeSCore.value = goods.value.rating / 2;
        loading.value = false;
      }
    })
  }
}

// 判断是否收藏
const isCollected = () => {
  //用户登录后, 根据获取的电影id,用户id, 判断用户是否收藏
  //根据用户对象的属性是否有值来判断用户是否登录（愚蠢的逻辑）
  if (Object.keys(user.value).length > 0) {
    requestUser.post('/index/collect/isCollect', {
      goodsId: goodsId.value,
    }).then(res => {
      if (res.code === '000000') {
        isCollect.value = true
      } else {
        isCollect.value = false
      }
    })
  }
}

// 添加收藏
const addCollect = () => {
  if (Object.keys(user.value).length === 0) {
    ElMessage.info("请先登录")
    return
  }
  requestUser.post('index/collect/addCollect', {
    goodsId: goodsId.value,
  }).then(res => {
    if (res.code === '000000') {
      ElMessage.success("收藏成功")
      isCollect.value = true
    } else {
      ElMessage.error(res.message)
    }
  })
}
// 取消收藏
const cancelCollect = () => {
  if (Object.keys(user.value).length === 0) {
    ElMessage.info("请先登录")
    return
  }
  requestUser.post('/index/collect/cancelCollect', {
    goodsId: goods.value.id,
  }).then(res => {
    if (res.code === '000000') {
      ElMessage.success("取消收藏成功")
      isCollect.value = false
    } else {
      ElMessage.error(res.message)
    }
  })
}
// 添加足迹（历史记录）
const addHistory = () => {
  if (Object.keys(user.value).length > 0) {
    requestUser.post('/index/history/addHistory', {
      goodsId: goodsId.value,
    }).then(res => {
      if (res.code === '000000') {
        console.log('加入足迹')
      } else {
        ElMessage.error(res.message)
      }
    })
  }
}
// 打开新增评论对话框
const addComments = (level, parentId, replyUserId) => {
  //用户必须登录后才能发表评论
  if (Object.keys(user.value).length === 0) {
    ElMessage.info("请先登录")
    return
  }
  form.value.level = level
  if (form.value.level === 1) {
    //一级评论
    form.value.goodsId = goodsId.value //当前电影id
    form.value.parentId = parentId
  } else {
    //二级评论
    form.value.goodsId = goodsId.value //当前电影id
    form.value.parentId = parentId
    form.value.replyUserId = replyUserId
  }
  //打开对话框
  dialogVisible.value = true
}

// 保存评论
const saveComments = () => {
  //用户必须登录后才能发表评论
  if (Object.keys(user.value).length === 0) {
    ElMessage.info("请先登录")
    return
  }
  formRef.value.validate((valid) => {
    if (valid) {
      requestUser.post('index/comments/addComments', form.value).then(res => {
        //操作成功
        if (res.code === '000000') {
          ElMessage.success('操作成功')
          loadCommentsData(1)
          dialogVisible.value = false
        } else {
          ElMessage.error(res.message)
        }
      })
    }
  })
}

// 关闭对话框
const onClose = () => {
  form.value = {}
}

// 显示图片
const showImg = (imgUrl) => {
  if (imgUrl) {
    return imgUrl
  } else {
    return new URL('@/assets/imgs/front/head-default.png', import.meta.url).href;

  }
}

// 加载评论列表数据
const loadCommentsData = (pageNum) => {
  //pageNum.value = pageNum
  requestUser.get('/comments/selectByPage', {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      level: 1,
      goodsId: goodsId.value
    }
  }).then(res => {
    console.log(res.data)
    commentsData.value = res.data?.list
    total.value = res.data?.total
  })
}

// 分页切换
const pageChange = (newPageNum) => {
  pageNum.value = newPageNum;
  loadData(newPageNum);
};

// 图片上传前的校验
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
  return isJPGOrPng && isLt2M;
}

// 图片上传成功
const handleAvatarSuccess = (response, file, fileList) => {
  form.value.attachList = []
  fileList.forEach( item => {
    let attachFile = item.response ? item.response.data : "Failed"
    form.value.attachList.push({
      attachFile: attachFile
    })
  })
}

// 图片移除
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

// 删除评论
const deleteData = (id) => {
  ElMessageBox.confirm('删除一级评论会连带删除其下的所有二级评论', '确认删除', { type: "warning" }).then(() => {
    requestUser.get('/comments/delete/' + id).then(res => {
      if (res.code === '000000') {
        ElMessage.success('删除成功')
        loadCommentsData(1)
      } else {
        ElMessage.error(res.message)
      }
    })
  }).catch(() => {})
}
</script>

<style scoped>
@import '@/assets/css/front/front.css';
.large-stars :deep(.el-icon) {
  font-size: 35px;
}
</style>