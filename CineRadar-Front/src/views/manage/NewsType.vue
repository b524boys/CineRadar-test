<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入公告类型查询" v-model="typeName"></el-input>
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
        <el-table-column prop="typeName" label="公告类型名称"></el-table-column>
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

    <el-dialog :title="form.id ? '修改公告类型':'新增公告类型'" v-model="dialogVisible" width="50%" :close-on-click-modal="false" @close="onClose">
      <el-form :model="form" label-width="100px" style="padding-right: 50px;" ref="formRef" :rules="rules">
        <el-form-item label="类型名称" prop="typeName">
          <el-input v-model="form.typeName" placeholder="公告类型名称"></el-input>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import requestAdmin from "@/utils/RequestAdmin.js";

// 响应式数据
const typeName = ref(null)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const ids = ref([])
const dialogVisible = ref(false)
const form = reactive({})
const formRef = ref(null)

// 验证规则
const rules = reactive({
  typeName: [
    { required: true, message: '公告类型名称不能为空', trigger: 'blur' }
  ]
})

// 生命周期钩子
onMounted(() => {
  loadData(1)
})

const reset = () => {
  typeName.value = null
  loadData(1)
}

const loadData = (pageNum) => {
  requestAdmin
      .get('/newsType/selectByPage', {
        params: {
          pageNum: pageNum,
          pageSize: pageSize.value,
          typeName: typeName.value
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
  dialogVisible.value = true
}

const saveData = () => {
  formRef.value.validate(valid => {
    if (valid) {
      const url = form.id ? '/newsType/update' : '/newsType/add'
      requestAdmin.post(url, form).then(res => {
        if (res.code === '000000') {
          ElMessage.success('操作成功')
          loadData(form.id ? pageNum.value : 1)
          dialogVisible.value = false
        } else {
          ElMessage.error(res.message)
        }
      })
    }
  })
}

const deleteData = (id) => {
  ElMessageBox.confirm('您确定要删除吗？', '确认删除', { type: "warning" })
      .then(() => {
        requestAdmin.get(`/newsType/delete/${id}`).then(res => {
          if (res.code === '000000') {
            ElMessage.success('删除成功')
            loadData(pageNum.value)
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
        requestAdmin.post('/newsType/delete/batch', ids.value).then(res => {
          if (res.code === '000000') {
            ElMessage.success('操作成功')
            loadData(pageNum.value)
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
}

const pageChange = (newPageNum) => {
  pageNum.value = newPageNum;
  loadData(newPageNum);
};
</script>

<style scoped>
@import '@/assets/css/manage/manage.css';
</style>