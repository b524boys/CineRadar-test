<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入名称查询" v-model="cateName"></el-input>
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
        <el-table-column prop="cateName" label="类型名称"></el-table-column>
        <el-table-column prop="intro" label="类型简介"></el-table-column>
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

    <el-dialog title="电影类型"  v-model="dialogVisible" width="50%" :close-on-click-modal="false" destroy-on-close @close="onClose">
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="类型名称" prop="cateName">
          <el-input v-model="form.cateName" placeholder="类型名称"></el-input>
        </el-form-item>
        <el-form-item label="类型简介" prop="intro">
          <el-input v-model="form.intro" placeholder="类型简介"></el-input>
        </el-form-item>
      </el-form>

      <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveData">确 定</el-button>
      </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import requestAdmin from "@/utils/RequestAdmin.js";

const cateName = ref('');
const tableData = ref([]);// 数据集
const pageNum = ref(1);// 当前的页码
const pageSize = ref(10);// 每页显示的个数
const total = ref(0);// 总条数
const dialogVisible = ref(false);// 对话框显示
const form = reactive({});
const formRef = ref(null);
const ids = ref([]);//元素id数组,用于删除

const rules = reactive({
  cateName: [{ required: true, message: '类型名称不能为空', trigger: 'blur' }],
});

onMounted(() => {
  loadData(1);
});

const reset = () => {
  cateName.value = '';
  loadData(1);
};

const loadData = (pageNum) => {
  requestAdmin
      .get('/category/selectByPage', {
        params: {
          pageNum: pageNum,
          pageSize: pageSize.value,
          cateName: cateName.value,
        },
      })
      .then((res) => {
        if (res.code === '000000') {
          tableData.value = res.data?.list;
          total.value = res.data?.total;
        } else {
          ElMessage.error(res.message);
        }
      });
};

const selectionChange = (rows) => {
  ids.value = rows.map((item) => item.id);
};

const addDialog = () => {
  //打开对话框
  dialogVisible.value = true;
};

const editDialog = (row) => {
  // 给form对象赋值  注意要深拷贝数据
  //this.form = row  这样写会有问题， 会把表单中的数据一同改了
  Object.assign(form, JSON.parse(JSON.stringify(row)));
  dialogVisible.value = true;
};

const saveData = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      requestAdmin
          .post(form.id ? '/category/update' : '/category/add', form)
          .then((res) => {
            if (res.code === '000000') {
              ElMessage.success('操作成功');
              loadData(1);
              dialogVisible.value = false;
            } else {
              ElMessage.error(res.message);
            }
          });
    }
  });
};

const deleteData = (id) => {
  // 根据id进行删除
  ElMessageBox.confirm('您确定删除吗？', '确认删除', { type: 'warning' })
      .then(() => {
        requestAdmin.get('/category/delete/' + id).then((res) => {
          if (res.code === '000000') {
            ElMessage.success('删除成功');
            loadData(1);
          } else {
            ElMessage.error(res.message);
          }
        });
      })
      .catch(() => {});
};

const deleteBatch = () => {
  if (!ids.value.length) {
    ElMessage.warning('请选择需要批量删除的数据');
    return;
  }
  ElMessageBox.confirm('您确定批量删除这些数据吗？', '确认删除', { type: 'warning' })
      .then(() => {
        requestAdmin.post('/category/delete/batch', ids.value).then((res) => {
          if (res.code === '000000') {
            // 批量删除成功
            ElMessage.success('操作成功');
            loadData(1);
          } else {
            ElMessage.error(res.message);
          }
        });
      })
      .catch(() => {});
};

const onClose = () => {
  //清空表单数据
  Object.keys(form).forEach(key => {
    form[key] = null; // 清空字符串
  });
};

const pageChange = (newPageNum) => {
  pageNum.value = newPageNum;
  loadData(newPageNum);
};
</script>

<style scoped>
@import '@/assets/css/manage/manage.css';
</style>