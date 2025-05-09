<template>
  <div>
    <h3>我的足迹</h3>
    <div class="search">
      <el-input placeholder="请输入电影名称查询" v-model="goodsName" clear></el-input>
      <el-button type="info" plain @click="loadData(1)">查询</el-button>
      <el-button type="warning" plain @click="reset">重置</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe>
        <el-table-column label="电影名称" prop="goodsName" show-overflow-tooltip></el-table-column>
        <el-table-column label="电影图片">
          <template v-slot="scope">
            <el-image :src="scope.row.cover " style="width: 40px;height: 40px;"
                      :preview-src-list="[scope.row.cover]">
            </el-image>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template v-slot="scope">
            <el-button type="primary" plain @click="movieDetail(scope.row.goodsId)">详情</el-button>
            <el-button type="danger" plain  @click="deleteData(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div>
        <el-pagination
            :current-page="pageNum"
            :page-size="pageSize"
            layout="total,prev,pager,next"
            :total="total"
            @current-change="pageChange">
        </el-pagination>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import requestUser from "@/utils/RequestUser.js";

const router = useRouter()

//const user = ref(JSON.parse(localStorage.getItem('user-token') || '{}'))
const goodsName = ref(null)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

onMounted(() => {
  loadData(1)
})

const reset = () => {
  goodsName.value = null
  loadData(1)
}

const loadData = async (pageNum) => {
  try {
    const res = await requestUser.get('/index/history/selectHistoryByPage', {
      params: {
        pageNum: pageNum,
        pageSize: pageSize.value,
        goodsName: goodsName.value
      }
    })
    tableData.value = res.data?.list
    total.value = res.data?.total
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const movieDetail = (id) => {
  router.push({ path: '/movieDetail', query: { id: id } })
}

const deleteData = async (id) => {
  try {
    await ElMessageBox.confirm('您确定要删除吗？', '确认删除', { type: 'warning' })
    const res = await requestUser.get('/history/delete/' + id)
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
</script>

<style scoped>
@import '@/assets/css/front/front.css';
</style>