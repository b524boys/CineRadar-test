<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入账号查询" v-model="name"></el-input>
      <el-input placeholder="请输入操作行为查询" v-model="operation"></el-input>
      <el-button type="info" plain @click="loadData(1)">查询</el-button>
      <el-button type="warning" plain @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="danger" plain @click="deleteBatch">批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe @selection-change="selectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="name" label="用户"></el-table-column>
        <el-table-column prop="operation" label="操作行为"></el-table-column>
        <el-table-column prop="method" label="调用函数"></el-table-column>
        <el-table-column label="参数">
          <template v-slot="scope">
            <el-tooltip :content="scope.row.params" placement="bottom" effect="light" open-delay="2000">
              <span>{{scope.row.params?.slice(0, 60) ?? ''}}</span>
            </el-tooltip>
          </template>
        </el-table-column >
        <el-table-column prop="respTime" label="请求时长(毫秒)"></el-table-column>
        <el-table-column label="操作">
          <template v-slot="scope">
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import requestAdmin from "@/utils/RequestAdmin.js";

// 响应式数据
const name = ref(null)
const operation = ref(null)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const ids = ref([])

// 生命周期钩子
onMounted(() => {
  loadData(1)
})

const reset = () => {
  name.value = null
  operation.value = null
  loadData(1)
}

const loadData = (pageNumVal) => {
  requestAdmin.get('/sysLog/selectByPage', {
    params: {
      pageNum: pageNumVal,
      pageSize: pageSize.value,
      name: name.value,
      operation: operation.value
    }
  }).then(res => {
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  })
}

const selectionChange = (rows) => {
  ids.value = rows.map(item => item.id)
}

const deleteData = (id) => {
  ElMessageBox.confirm('您确定要删除吗？', '确认删除', { type: "warning" })
      .then(() => {
        requestAdmin.get(`/sysLog/delete/${id}`).then(res => {
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
  ElMessageBox.confirm('您确定批量删除这些数据吗？', '确认删除', { type: "warning" })
      .then(() => {
        requestAdmin.post('/sysLog/delete/batch', ids.value).then(res => {
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

const pageChange = (newPageNum) => {
  pageNum.value = newPageNum;
  loadData(newPageNum);
};
</script>

<style scoped>
@import '@/assets/css/manage/manage.css';
</style>