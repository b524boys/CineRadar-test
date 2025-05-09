<template>
  <div class="main-container">
    <div>
      <ul class="catalog-list">
        <li @click="categoryGoods('')" :class="{ selected : cateName==='' }">全部</li>
        <li v-for="(item,index) in lstCategory" :key="item.id" @click="categoryGoods(item.cateName)"
            :class="{ selected : cateName===item.cateName }">
          {{item.cateName}}
        </li>
      </ul>
    </div>

    <!-- 搜索电影列表展示 -->
    <el-skeleton :loading="loadingSearch" animated>
      <!-- 数据加载时的骨架屏 -->
      <template #template>
        <el-row style="margin-bottom:15px">
          <el-col :span="6" v-for="i in 12" :key="'skeleton-'+i">
            <el-card :body-style="{padding:'0px'}" class="goods-item">
              <div class="item-card-body">
                <el-skeleton-item variant="image" class="cover" />
                <el-tooltip effect="light" :content="'评分'" placement="top" open-delay="1000">
                  <el-skeleton-item variant="text" style="width: 80px;height: 16px;margin: 14px 0 7px 0" />
                </el-tooltip>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </template>
      <!-- 实际数据渲染 -->
      <template #default>
        <el-row>
          <el-col :span="6" v-for="item in tableData" :key="item.id">
            <el-card :body-style="{padding:'0px'}" class="goods-item" @click="movieDetail(item.id)">
              <el-image :src="item.cover" class="cover"/>
              <el-tooltip effect="light" :content="'评分: '+item.rating" placement="top" open-delay="1000">
                <div v-html="item.goodsName" class="goods-name"></div>
              </el-tooltip>
            </el-card>
          </el-col>
        </el-row>
      </template>
    </el-skeleton>

    <div>
      <el-pagination @current-change="pageChange" :current-page="pageNum"
                     :page-size="pageSize" layout="total, prev, pager, next" :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import requestUser from "@/utils/RequestUser.js";
import eventBus from "@/utils/eventBus.js";

const router = useRouter();
const route = useRoute();
// 响应式数据
const lstCategory = ref([]); // 分类数组
const tableData = ref([]); // 电影列表数据
const pageNum = ref(1); // 当前页码
const pageSize = ref(16); // 每页条数
const total = ref(0); // 总条数
const cateName = ref(''); // 电影类型
const goodsName = ref(route.query.goodsName || ''); // 电影名称
const loadingSearch = ref(true);

// 加载分类数据
const loadCategoryData = () => {
  requestUser.get('/category/select').then((res) => {
    if (res.code === '000000') {
      lstCategory.value = res.data;
    } else {
      ElMessage.error(res.message);
    }
  });
};

// 加载电影数据
const loadGoodsData = (pageNum) => {
  requestUser
      .get('/goods/selectByPageES', {
        params: {
          pageNum: pageNum,
          pageSize: pageSize.value,
          cateName: cateName.value,
          searchGoodsName: goodsName.value,
        },
      })
      .then((res) => {
        console.log(res.data);
        tableData.value = res.data?.list || [];
        total.value = res.data?.total || 0;
        setTimeout(() => {
          loadingSearch.value = false;
        }, 1500);
      });
};

// 分页切换
const pageChange = (newPageNum) => {
  pageNum.value = newPageNum;
  loadGoodsData(newPageNum);
};

// 查看分类下的电影列表
const categoryGoods = (name) => {
  cateName.value = name;
  pageNum.value = 1;
  loadGoodsData(1);
};

// 查看电影详情
const movieDetail = (id) => {
  const route = router.resolve({
    name: 'MovieDetail',
    query: {id}
  })
  window.open(route.href, '_blank')
};

// 监听搜索事件
const handleSearch = (val) => {
  goodsName.value = val;
  loadGoodsData(1);
};

// 生命周期钩子
onMounted(() => {
  loadCategoryData();
  loadGoodsData(pageNum.value);
  // 监听搜索事件
  eventBus.on('searchMovieList', handleSearch);
});

// 组件销毁前取消事件监听
onBeforeUnmount(() => {
  eventBus.off('searchMovieList', handleSearch);
});

</script>

<style scoped>
@import '@/assets/css/front/front.css';
</style>