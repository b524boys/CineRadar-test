<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入公告标题查询" v-model="title"></el-input>
      <el-select v-model="newsTypeId" placeholder="请选择公告类别查询">
        <el-option v-for="item in lstNewsType" :label="item.typeName" :value="item.id" :key="item.id"></el-option>
      </el-select>
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
        <el-table-column prop="title" label="公告标题"></el-table-column>
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

    <el-dialog :title="form.id ? '修改公告':'新增公告'" v-model="dialogVisible" width="80%" :close-on-click-modal="false" @close="onClose">
      <el-form :model="form" label-width="100px" style="padding-right: 50px;" ref="formRef" :rules="rules">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="公告标题"></el-input>
        </el-form-item>
        <el-form-item label="公告类别" prop="newsTypeId">
          <el-select v-model="form.newsTypeId" placeholder="公告类别">
            <el-option v-for="item in lstNewsType" :label="item.typeName" :value="item.id" :key="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否置顶" prop="top">
          <el-radio v-model="form.top" :label="1">是</el-radio>
          <el-radio v-model="form.top" :label="0">否</el-radio>
        </el-form-item>
        <el-form-item label="封面图片" >
          <div style="display: flex;">
            <img v-if="coverImgUrl" :src="coverImgUrl" class="avatar">
            <el-upload
                class="avatar-uploader"
                :action="$baseUrl + '/files/minio/upload'"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload">
              <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <div id="editor"></div>
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
import {ref, reactive, onMounted, nextTick, getCurrentInstance} from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import E from 'wangeditor';
import requestAdmin from "@/utils/RequestAdmin.js";
const { $baseUrl } = getCurrentInstance().appContext.config.globalProperties;

const title = ref('');
const newsTypeId = ref('');
const lstNewsType = ref([]);
const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const ids = ref([]);
const dialogVisible = ref(false);
const form = reactive({ top: 0 });
const formRef = ref(null);
const editor = ref(null);
const coverImgUrl = ref('');

const rules = reactive({
  title: [{ required: true, message: '公告标题不能为空', trigger: 'blur' }],
  newsTypeId: [{ required: true, message: '公告类别不能为空', trigger: 'change' }],
});

onMounted(() => {
  loadNewsType();
  loadData(1);
});

const reset = () => {
  title.value = '';
  newsTypeId.value = '';
  loadData(1);
};

const loadNewsType = () => {
  requestAdmin.get('/newsType/select').then((res) => {
    if (res.code === '000000') {
      lstNewsType.value = res.data;
    } else {
      ElMessage.error(res.message);
    }
  });
};

const loadData = (pageNum) => {
  requestAdmin
      .get('/news/selectByPage', {
        params: {
          pageNum: pageNum,
          pageSize: pageSize.value,
          title: title.value,
          newsTypeId: newsTypeId.value,
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

const initEditor = () => {
  nextTick(() => {
    editor.value = new E('#editor');
    editor.value.config.uploadImgServer = $baseUrl + '/files/editor/upload';
    editor.value.config.uploadFileName = 'file';
    editor.value.config.uploadImgParams = {
      token: localStorage.getItem('admin-token') || '',
      type: 'image',
    };
    editor.value.config.uploadVideoServer = $baseUrl + '/files/editor/upload';
    editor.value.config.uploadVideoName = 'file';
    editor.value.config.uploadVideoParams = {
      token: localStorage.getItem('admin-token') || '',
      type: 'video',
    };
    editor.value.create();
  });
};

const addDialog = () => {
  Object.assign(form, { top: 0 });
  dialogVisible.value = true;
  initEditor();
};

const editDialog = (row) => {
  Object.assign(form, JSON.parse(JSON.stringify(row)));
  if (form.cover) {
    coverImgUrl.value = form.cover;
  }
  dialogVisible.value = true;
  initEditor();
  nextTick(() => {
    editor.value.txt.html(row.content);
  });
};

const saveData = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      form.content = editor.value.txt.html();
      requestAdmin
          .post(form.id ? '/news/update' : '/news/add', form)
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
  ElMessageBox.confirm('您确定要删除吗？', '确认删除', { type: 'warning' })
      .then(() => {
        requestAdmin.get('/news/delete/' + id).then((res) => {
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
        requestAdmin.post('/news/delete/batch', ids.value).then((res) => {
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

const onClose = () => {
  Object.keys(form).forEach(key => {
    form[key] = null; // 清空字符串
  });
  console.log(form);
  coverImgUrl.value = '';
  editor.value?.destroy();
  editor.value = null;
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
  form.cover = res.data;
  coverImgUrl.value = form.cover;
};

const pageChange = (newPageNum) => {
  pageNum.value = newPageNum;
  loadData(newPageNum);
};

const formatTop = (row) => {
  return row.top === 1 ? '是' : '否';
};
</script>

<style scoped>
@import '@/assets/css/manage/manage.css';
</style>