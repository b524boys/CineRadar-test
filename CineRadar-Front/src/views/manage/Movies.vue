<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入电影名称" v-model="goodsName"></el-input>
      <el-select v-model="cateName" placeholder="请选择分类查询">
        <el-option v-for="item in lstCategory" :label="item.cateName" :value="item.cateName" :key="item.id"></el-option>
      </el-select>
      <el-button type="info" plain @click="loadData(1)">查询</el-button>
      <el-button type="warning" plain @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="primary" plain @click="addDialog">新增</el-button>
      <el-button type="danger" plain @click="deleteBatch">批量删除</el-button>
      <el-button type="info" plain @click="exportData">导出</el-button>
      <el-upload
          ref="uploadExcelRef"
          :limit="1"
          show-file-list:false
          :action="$baseUrl +'/excel/importGoodsExcel'"
          :on-success="handleExcelSuccess"
          :before-upload="beforeExcelUpload" style="display: inline; margin-left: 10px">
        <el-button type="info" plain>导入</el-button>
      </el-upload>
    </div>

    <div class="table">
      <el-table :data="tableData" strip @selection-change="selectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="goodsName" label="电影名称" show-overflow-tooltip>
        </el-table-column>
        <el-table-column prop="cateName" label="类型" show-overflow-tooltip></el-table-column>

        <el-table-column prop="casts" width="200" label="演员"  show-overflow-tooltip></el-table-column>
        <el-table-column prop="year" width="100" label="发行年份(年)"></el-table-column>
        <el-table-column prop="country" width="120" label="国家"></el-table-column>
        <el-table-column prop="duration" width="80" label="时长(分钟)"></el-table-column>
        <el-table-column prop="rating" width="80" label="综合评分" ></el-table-column>


        <el-table-column prop="hits" label="点击量"></el-table-column>
        <el-table-column label="封面图片">
          <template v-slot="scope">
            <el-image style="width: 40px; height: 40px; border-radius: 20%;" v-if="scope.row.cover"
                      :src="scope.row.cover"
                      :preview-src-list="[scope.row.cover]">
            </el-image>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="220">
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

    <el-dialog :title="form.id ? '修改电影' : '新增电影'" v-model="dialogVisible" width="80%" :close-on-click-modal="false" destroy-on-close @close="onClose">
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="电影名称" prop="goodsName">
          <el-input v-model="form.goodsName" placeholder="电影名称"></el-input>
        </el-form-item>
        <el-form-item label="类型" prop="cateNameList">
          <el-select v-model="form.cateNameList" placeholder="请选择电影分类" multiple allow-create filterable>
            <el-option v-for="item in lstCategory" :label="item.cateName" :value="item.cateName" :key="item.id"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="导演" prop="director">
          <el-input v-model="form.director" placeholder="导演" type="text"></el-input>
        </el-form-item>
        <el-form-item label="演员" prop="casts">
          <el-input v-model="form.casts" placeholder="演员"></el-input>
        </el-form-item>
        <el-form-item label="发行年份" prop="year">
          <el-input v-model="form.year" placeholder="发行年份"></el-input>
        </el-form-item>
        <el-form-item label="国家" prop="country">
          <el-input v-model="form.country" placeholder="国家"></el-input>
        </el-form-item>
        <el-form-item label="时长(分钟)" prop="duration">
          <el-input v-model="form.duration" placeholder="时长(分钟)" type="number"></el-input>
        </el-form-item>
        <el-form-item label="综合评分" prop="rating">
          <el-input v-model="form.rating" placeholder="综合评分" type="number"></el-input>
        </el-form-item>


        <el-form-item label="封面图片" prop="cover">
          <div style="display: flex;">
            <el-image v-if="coverImgUrl" :src="coverImgUrl" class="avatar" />
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

        <el-form-item label="电影介绍" prop="introduce">
          <div id="editor"></div>
        </el-form-item>

<!--        <el-form-item label="视频" prop="videoUrl">
          <div style="display: flex;">
            <video v-if="form.videoUrl && !videoProcessFlag " :src="$baseUrl + '/files/' + form.videoUrl"
                   class="avatar" controls>
              您的浏览器不支持视频播放
            </video>
            <el-upload
                class="avatar-uploader"
                :action="$baseUrl + '/files/upload'"
                :before-upload="beforeUploadVideo"
                :on-progress="uploadVideoProcess"
                :on-success="uploadVideoSuccess"
                :show-file-list="false">
              <el-icon v-if="videoProcessFlag ===false" class="avatar-uploader-icon"><Plus /></el-icon>
              <el-progress v-if="videoProcessFlag === true" type="circle" :percentage="videoUploadPercent" style="margin-top:10px;float: left"></el-progress>
            </el-upload>
          </div>
        </el-form-item>-->

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
import {ref, reactive, onMounted, nextTick, getCurrentInstance} from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import E from 'wangeditor'
import requestAdmin from "@/utils/RequestAdmin.js";
import requestUser from "@/utils/RequestUser.js";
const { $baseUrl } = getCurrentInstance().appContext.config.globalProperties;

const goodsName = ref('');
const cateName = ref('');
const lstCategory = ref([]);  //电影类型数组
const tableData = ref([]);  // 数据集
const pageNum = ref(1);  // 当前的页码
const pageSize = ref(10);  // 每页显示的个数
const total = ref(0);  // 总条数
const dialogVisible = ref(false);  // 对话框显示
const form = reactive({});
const formRef = ref(null);

const ids = ref([]);  //元素id数组,用于删除
const coverImgUrl = ref('');  //封面url
const editor = ref(null);
const videoProcessFlag = ref(false);
const videoUploadPercent = ref(0);
const uploadExcelRef = ref(null);

const rules = reactive({
  goodsName: [{ required: true, message: '电影名称不能为空', trigger: 'blur' }],
  cateNameList: [{ required: true, message: '电影类型不能为空', trigger: 'change' }],
});

onMounted(() => {
  loadCategory();
  loadData(1);
});

const reset = () => {
  goodsName.value = '';
  cateName.value = '';
  loadData(1);
};

//加载电影类型数据, 用于下拉框显示
const loadCategory = () => {
  //查询所有电影分类信息
  requestAdmin.get('/category/select').then((res) => {
    if (res.code === '000000') {
      lstCategory.value = res.data;
    } else {
      ElMessage.error(res.message);
    }
  });
};

//加载电影列表数据
const loadData = (pageNum) => {
  requestAdmin
      .get('/goods/selectByPage', {
        params: {
          pageNum: pageNum,
          pageSize: pageSize.value,
          searchGoodsName: goodsName.value,
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
  // 当前选中的所有的行数据
  ids.value = rows.map((item) => item.id);
};

const addDialog = () => {
  //打开对话框
  dialogVisible.value = true;
  initEditor();
  console.log(form);
};

const editDialog = (row) => {
  // 给form对象赋值  注意要深拷贝数据
  Object.assign(form, JSON.parse(JSON.stringify(row)));
  form.cateNameList = row.cateName.split(',').map(item => item.trim());
  console.log(form);
  if (form.cover) {
    //显示封面图片
    coverImgUrl.value = form.cover;
  }
  dialogVisible.value = true;
  initEditor();
  nextTick(() => {
    editor.value.txt.html(row.introduce);
  });
};

const saveData = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      if (!form.cover) {
        ElMessage.info('请上传电影的封面图片');
        return;
      }
      form.cateName = form.cateNameList.join(',');
      form.introduce = editor.value.txt.html();
      requestAdmin
          .post(form.id ? '/goods/update' : '/goods/add', form)
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
        requestAdmin.get('/goods/delete/' + id).then((res) => {
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
        requestAdmin.post('/goods/delete/batch', ids.value).then((res) => {
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
  //清空表单数据
  Object.keys(form).forEach(key => {
    form[key] = null; // 清空字符串
  });
  coverImgUrl.value = ''; // 清空封面图片
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
  form.cover = res ? res.data : "Failed";
  coverImgUrl.value = form.cover;
  console.log(form.cover)
};

const pageChange = (newPageNum) => {
  pageNum.value = newPageNum;
  loadData(newPageNum);
};

const initEditor = () => {
  //dom元素渲染到页面之后再执行
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

const beforeUploadVideo = (file) => {
  const isMp4 = file.type === 'video/mp4';
  if (!isMp4) {
    ElMessage.error('上传视频只能是mp4格式!');
    return false;
  }
  const isLt100M = file.size / 1024 / 1024 < 200;

  if (!isLt100M) {
    ElMessage.error('上传视频大小不能超过200MB!');
    return false;
  }
  return isMp4 && isLt100M;
};

//视频上传进度显示
const uploadVideoProcess = (event) => {
  videoProcessFlag.value = true;
  videoUploadPercent.value = Math.floor(event.percent);
};

// 视频上传成功回调
const uploadVideoSuccess = (res) => {
  form.videoUrl = res.data;
  videoProcessFlag.value = false;
  videoUploadPercent.value = 0;
};

const exportData = () => {
  requestAdmin.get('/excel/exportGoodsExcel').then((res) => {
    if (res.code === '000000') {
      window.open($baseUrl + '/files/' + res.data);
    } else {
      ElMessage.error(res.message);
    }
  });
};

const beforeExcelUpload = (file) => {
  const isExcel =
      file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
      file.type === 'application/vnd.ms-excel';
  const isLt10M = file.size / 1024 / 1024 < 10;

  if (!isExcel) {
    ElMessage.error('上传的文件不是Excel!');
    return false;
  }
  if (!isLt10M) {
    ElMessage.error('上传的文件大小不能超过10MB!');
    return false;
  }
  return isExcel && isLt10M;
};

const handleExcelSuccess = (response) => {
  if (response.code === '000000') {
    loadData(1);
    ElMessage.success('导入数据成功');
  } else {
    ElMessage.error('文件上传失败');
  }
  uploadExcelRef.value.clearFiles();
};
</script>

<style scoped>
@import '@/assets/css/manage/manage.css';
</style>