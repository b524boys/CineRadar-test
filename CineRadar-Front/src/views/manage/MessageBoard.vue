<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入用户名关键词" v-model="userName"></el-input>
      <el-input placeholder="请输入留言内容关键词" v-model="content"></el-input>
      <el-button type="info" plain @click="loadData(1)">查询</el-button>
      <el-button type="warning" plain @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="danger" plain @click="deleteBatch">批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe @selection-change="selectionChange" row-key="id" :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="userName" label="用户名"></el-table-column>
        <el-table-column prop="nickName" label="用户昵称"></el-table-column>
        <el-table-column prop="content" label="留言"></el-table-column>
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

    <el-dialog :title="form.id ? '修改留言板' : '新增留言板'" v-model="dialogVisible" width="50%" :close-on-click-modal="false" @close="onClose">
      <el-form :model="form" label-width="100px" style="padding-right: 50px;" ref="formRef" :rules="rules">
        <el-form-item label="用户名">
          <el-input v-model="form.userName" placeholder="用户名" :readonly="true"></el-input>
        </el-form-item>
        <el-form-item label="留言" prop="content">
          <el-input v-model="form.content" placeholder="留言" type="textarea" :rows="3"></el-input>
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
import {ref, reactive, onMounted, getCurrentInstance} from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import requestAdmin from "@/utils/RequestAdmin.js";
const { $baseUrl } = getCurrentInstance().appContext.config.globalProperties;

const userName = ref('');
const content = ref('');
const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const ids = ref([]);
const dialogVisible = ref(false);
const form = reactive({});
const formRef = ref(null);
const fileList = ref([]);  //关联附件

onMounted(() => {
  loadData(1);
});

const reset = () => {
  userName.value = '';
  content.value = '';
  loadData(1);
};

const loadData = (pageNum) => {
  requestAdmin
      .get('/messageBoard/selectByPage', {
        params: {
          pageNum: pageNum,
          pageSize: pageSize.value,
          userName: userName.value,
          content: content.value,
          level: 1,
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

const deleteBatch = () => {
  if (!ids.value.length) {
    ElMessage.warning('请选择需要批量删除的数据');
    return;
  }
  ElMessageBox.confirm('您确定批量删除这些数据吗？', '确认删除', { type: 'warning' })
      .then(() => {
        requestAdmin.post('/messageBoard/delete/batch', ids.value).then((res) => {
          if (res.code === '000000') {
            ElMessage.success('操作成功');
            loadData(1);
          } else {
            ElMessage.error(res.message);
          }
        });
      })
      .catch(() => {});
};

const deleteData = (id) => {
  ElMessageBox.confirm('您确定要删除吗？', '确认删除', { type: 'warning' })
      .then(() => {
        requestAdmin.get('/messageBoard/delete/' + id).then((res) => {
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

const selectionChange = (rows) => {
  ids.value = rows.map((item) => item.id);
};

const saveData = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      requestAdmin
          .post(form.id ? '/messageBoard/update' : '/messageBoard/add', form)
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

const editDialog = (row) => {
  Object.assign(form, JSON.parse(JSON.stringify(row)));
  if (form.attachList) {
    fileList.value = form.attachList.map((attach) => ({
      url: attach.attachFile,
    }));
  }
  dialogVisible.value = true;
};

const onClose = () => {
  Object.keys(form).forEach(key => {
    form[key] = null; // 清空字符串
  });
  fileList.value = [];
};

const pageChange = (newPageNum) => {
  pageNum.value = newPageNum;
  loadData(newPageNum);
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

const handleRemove = (file, fileList) => {
  // 从URL中提取文件名
  const url = file.response.data;
  const fileName = url.substring(url.lastIndexOf('/') + 1);
  // 使用axios发送删除请求
  requestAdmin.delete('/files/minio/delete/' + fileName ).then(res => {
    if (res.code === '000000') {
      ElMessage.success('删除成功')
    } else {
      ElMessage.error('删除失败')
    }
  })
};

const handleAvatarSuccess = (response, file, fileList) => {
  form.attachList = []
  fileList.forEach( item => {
    let attachFile = item.response ? item.response.data : "Failed"
    form.attachList.push({
      attachFile: attachFile
    })
  })
};
</script>

<style scoped>
@import '@/assets/css/manage/manage.css';
</style>