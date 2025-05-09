<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入用户名关键词" v-model="userName"></el-input>
      <el-input placeholder="请输入电影名称关键词" v-model="goodsName"></el-input>
      <el-input placeholder="请输入评论内容关键词" v-model="content"></el-input>
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
        <el-table-column prop="goodsName" label="电影名称"></el-table-column>
        <el-table-column prop="content" label="评论"></el-table-column>
        <el-table-column prop="rate" label="评分"></el-table-column>
        <el-table-column prop="level" label="评论级别">
          <template v-slot="scope">
            {{scope.row.level === 1 ? '一级评论' : '二级评论'}}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template v-slot="scope">
            <el-button size="small" type="primary" plain @click="editDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" plain @click="deleteData(scope.row)">删除</el-button>
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

    <el-dialog :title="form.id ? '修改评论' : '新增评论'" v-model="dialogVisible" width="50%" :close-on-click-modal="false" @close="onClose">
      <el-form :model="form" label-width="100px" style="padding-right: 50px;" ref="formRef" :rules="rules">
        <el-form-item label="用户名">
          <el-input v-model="form.userName" placeholder="用户名" :readonly="true"></el-input>
        </el-form-item>
        <el-form-item label="电影名称">
          <el-input v-model="form.goodsName" placeholder="密码" :readonly="true"></el-input>
        </el-form-item>
        <el-form-item label="评分" v-if="form.level===1">
          <el-rate v-model="form.rate" show-score :max="10" style="margin-top: 8px;" :allow-half="true"></el-rate>
        </el-form-item>
        <el-form-item label="评论" prop="content">
          <el-input v-model="form.content" placeholder="评论" type="textarea" :rows="3"></el-input>
        </el-form-item>
        <el-form-item label="图片" v-if="form.level==1">
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

const userName = ref('');
const goodsName = ref('');
const content = ref('');
const tableData = ref([]);  //数据集
const pageNum = ref(1);   //当前的页码
const pageSize = ref(10);  //每页显示的个数
const total = ref(0);  //总条数
const ids = ref([]);  //元素id数组, 用于删除
const dialogVisible = ref(false);
const form = reactive({});  //表单,
const formRef = ref(null);
const fileList = ref([]);  //关联附件

onMounted(() => {
  loadData(1);
});

const reset = () => {
  userName.value = '';
  goodsName.value = '';
  content.value = '';
  loadData(1);
};

const loadData = (pageNum) => {
  requestAdmin
      .get('/comments/selectByPage', {
        params: {
          pageNum: pageNum,
          pageSize: pageSize.value,
          userName: userName.value,
          goodsName: goodsName.value,
          content: content.value,
          level: 1,
        },
      })
      .then((res) => {
        if (res.code === '000000') {
          tableData.value = res.data?.list;
          total.value = res.data?.total;
          console.log(res.data);
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
  //给form对象赋值, 注意要深度拷贝
  Object.assign(form, JSON.parse(JSON.stringify(row)));
  //回显图片
  if (form.attachList) {
    fileList.value = form.attachList.map((attach) => ({
      url: attach.attachFile,
    }));
  }
  dialogVisible.value = true;
};

const saveData = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      requestAdmin
          .post(form.id ? '/comments/update' : '/comments/add', form)
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

const deleteData = (row) => {
  ElMessageBox.confirm(
      row.level === 1 ? '删除一级评论会连带删除其下的所有二级评论？' : '确定要删除吗',
      '确认删除',
      { type: 'warning' }
  )
      .then(() => {
        requestAdmin.get('/comments/delete/' + row.id).then((res) => {
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
        requestAdmin.post('/comments/delete/batch', ids.value).then((res) => {
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

// 图片移除
const handleRemove = (file) => {
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
</script>

<style scoped>
@import '@/assets/css/manage/manage.css';
</style>