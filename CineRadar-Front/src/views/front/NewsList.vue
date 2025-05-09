<template>
  <div class="main-container">
    <div class="news-list">
      <div class="search">
        <el-input placeholder="请输入公告标题查询" v-model="title"></el-input>
        <el-select v-model="newsTypeId" placeholder="请选择公告类别查询">
          <el-option v-for="item in lstNewsType" :label="item.typeName" :value="item.id" :key="item.id"></el-option>
        </el-select>
        <el-button type="info" plain @click="loadData(1)">查询</el-button>
        <el-button type="warning" plain @click="reset">重置</el-button>
      </div>

      <div class="table">
        <el-table :data="tableData" stripe>
          <el-table-column prop="title" label="公告标题">
            <template v-slot="scope">
              <router-link :to="'/newsDetail?id=' + scope.row.id" style="font-size:15px; text-decoration: underline;">{{scope.row.title}}</router-link>
            </template>
          </el-table-column>
          <el-table-column prop="typeName" label="公告类型"></el-table-column>
          <el-table-column prop="top" label="是否置顶" :formatter=formatTop></el-table-column>
          <el-table-column label="封面">
            <template v-slot="scope">
              <el-image style="width: 40px; height: 40px; border-radius: 20%;" v-if="scope.row.cover"
                        :src="scope.row.cover"
                        :preview-src-list="[scope.row.cover]">
              </el-image>
            </template>
          </el-table-column>
          <el-table-column prop="hits" label="点击次数"></el-table-column>
          <el-table-column prop="publisher" label="发布人"></el-table-column>
        </el-table>

        <div>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import requestUser from "@/utils/RequestUser.js";

const route = useRoute()

// 响应式数据
const title = ref('')
const newsTypeId = ref(null)
const lstNewsType = ref([])
const tableData = ref([])  //数据集
const pageNum = ref(1)  //当前的页码
const pageSize = ref(10)  //每页显示的个数
const total = ref(0)  //总条数

onMounted(() => {
  newsTypeId.value = parseInt(route.query.typeId)
  loadNewsType()
  loadData(1)
})

const reset = () => {
  title.value = null
  newsTypeId.value = null
  loadData(1)
}

const loadNewsType = async () => {
  try {
    const res = await requestUser.get('/newsType/select')
    if (res.code === '000000') {
      lstNewsType.value = res.data
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('加载公告类型失败')
  }
}

const loadData = async (pageNum) => {
  try {
    const res = await requestUser.get('/news/selectByPage', {
      params: {
        pageNum: pageNum,
        pageSize: pageSize.value,
        title: title.value,
        newsTypeId: newsTypeId.value
      }
    })
    tableData.value = res.data?.list
    total.value = res.data?.total
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const pageChange = (newPageNum) => {
  pageNum.value = newPageNum;
  loadData(newPageNum);
};

const formatTop = (row, column) => {
  return row.top === '1' ? '是' : '否'
}
</script>

<style scoped>
@import '@/assets/css/front/front.css';
</style>