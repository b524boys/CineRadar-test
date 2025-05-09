<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入搜索内容" v-model="carouselName" clearable> </el-input>
      <el-button type="info" plain  @click="loadData(1)">查询</el-button>
      <el-button type="warning" plain @click="reset">重置</el-button>
    </div>
    <div class="operation">
      <el-button type="primary" plain @click="addDialog" >新增</el-button>
      <el-button type="danger" plain @click="deleteBatch" >批量删除</el-button>
    </div>
    <div class="table">
      <el-table :data="tableData" style="width: 100%" @selection-change="selectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="carouselName" width="380" label="轮播图名称"  show-overflow-tooltip></el-table-column>
        <el-table-column  label="轮播图图片">
          <template  v-slot="scope">
            <el-image :src="scope.row.carouselCover"
                      style="width: 200px; height: 70px; border-radius: 20%;"
                      fit="cover"
                      :preview-src-list="[scope.row.carouselCover]">
            </el-image>
          </template>

        </el-table-column>
        <el-table-column  label="操作" >
          <template v-slot="scope">
            <el-button type="primary" plain @click="editDialog(scope.row)">编辑</el-button>
            <el-button type="danger" plain @click="deleteData(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
            @current-change="pageChange"
            :current-page="pageNum"
            :page-size="pageSize"
            :page-sizes="[5, 10, 20]"
            layout="total, prev, pager, next"
            :total="total">

        </el-pagination>
      </div>
    </div>

    <el-dialog :title="form.id ? '修改轮播图':'新增轮播图'" v-model="dialogVisible"  :close-on-click-modal="false" destroy-on-close @close="onClose">
      <el-form :model="form" label-width="120px" ref="formRef" :rules="rules">
        <el-form-item label="轮播图名称" prop="carouselName">
          <el-input v-model="form.carouselName" placeholder="请输入轮播图名称"></el-input>
        </el-form-item>
        <el-form-item label="轮播图图片" prop="carouselCover">
          <div style="display: flex;">
            <img  v-if="carouselCoverUrl" :src="carouselCoverUrl" class="avatar"/>
            <el-upload
                class ="avatar-uploader"
                :action="$baseUrl + '/files/minio/upload'"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload">
              <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveData">确定</el-button>
      </div>
      </template>
    </el-dialog>
  </div>
</template>


<script setup>
import {ref, reactive, onMounted, getCurrentInstance} from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import requestAdmin from "@/utils/RequestAdmin.js";

const { $baseUrl } = getCurrentInstance().appContext.config.globalProperties;

const carouselName = ref('');
const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const ids = ref([]);
const dialogVisible = ref(false);
const form = reactive({});
const formRef = ref();
const carouselCoverUrl = ref('');
const rules = reactive({
  carouselName: [{ required: true, message: '请输入轮播图名称', trigger: 'blur' }],
});

onMounted(() => {
  loadData(1);
});

const reset = () => {
  carouselName.value = '';
  loadData(1);
};

const loadData = (pageNum) => {
  requestAdmin
      .get('/carousel/selectByPage', {
        params: {
          pageNum: pageNum,
          pageSize: pageSize.value,
          carouselName: carouselName.value,
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
  dialogVisible.value = true;
};

const editDialog = (row) => {
  Object.assign(form, JSON.parse(JSON.stringify(row)));
  if (form.carouselCover) {
    carouselCoverUrl.value = row.carouselCover;
  }
  dialogVisible.value = true;
};

const saveData = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      if (!form.carouselCover) {
        ElMessage.error('请上传图片');
        return;
      }
      requestAdmin
          .post(form.id ? '/carousel/update' : '/carousel/add', form)
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
  ElMessageBox.confirm('确定要删除吗？', '确认删除', { type: 'warning' })
      .then(() => {
        requestAdmin.get('/carousel/delete/' + id).then((res) => {
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
  if (ids.value.length === 0) {
    ElMessage.error('请选择要删除的数据');
    return;
  }
  ElMessageBox.confirm('确定要删除吗？', '确认删除', { type: 'warning' })
      .then(() => {
        requestAdmin.post('/carousel/delete/batch', ids.value).then((res) => {
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

const onClose = () => {
  Object.keys(form).forEach(key => {
    form[key] = null; // 清空字符串
  });
  carouselCoverUrl.value = '';
};

const beforeAvatarUpload = (file) => {
  const isJPGOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPGOrPng) {
    ElMessage.error('上传图片只能是 jpg 或者 png 格式!');
    return false;
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!');
    return false;
  }
  return isJPGOrPng && isLt2M;
};

const handleAvatarSuccess = (res) => {
  form.carouselCover = res ? res.data : "Failed";
  carouselCoverUrl.value = form.carouselCover;
};

const pageChange = (newPageNum) => {
  pageNum.value = newPageNum;
  loadData(newPageNum);
};
</script>

<style scoped>
@import '@/assets/css/manage/manage.css';
</style>