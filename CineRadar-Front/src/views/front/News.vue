<template>
  <template class="main-box">
    <!-- 广告滚动栏展示 -->
    <el-carousel class="movie-carousel">
      <el-carousel-item v-for="item in carouselData" :key="item.id">
        <el-image :src="item.carouselCover" fit="fill" style="width: 1150px ;height: 220px">
        </el-image>
      </el-carousel-item>
    </el-carousel>
    <div class="main-container">
      <div class="news-container">
        <!-- 轮播图部分 -->
        <el-carousel :interval="3000" arrow="always" class="news-box">
          <el-carousel-item v-for="item in topCoverNews" :key="item.id">
            <router-link :to="'/newsDetail?id=' + item.id">
              <el-image :src="item.cover" fit="fill" style="width: 100%; height: 100%;">
              </el-image>
            </router-link>
          </el-carousel-item>
        </el-carousel>

        <!-- 新闻类别列表部分 -->
        <div class="news-box" v-for="(itemType,indexType) in typeNewsList" :key="itemType.id">
          <div class="news-type-box">
            <span class="news-type">{{itemType.typeName}}</span>
            <router-link :to="'/newsList?typeId=' + itemType.id">
              <span class="more">更多</span>
            </router-link>
          </div>
          <ul class="news-item-box">
            <li class="news-item" v-for="(itemNews,indexNews) in itemType.lstNews" :key="itemNews.id">
              <router-link :to="'/newsDetail?id=' + itemNews.id" class="title">
                <span>{{truncateText(itemNews.title)}}</span>
              </router-link>
              <span class="publisher">{{itemNews.publisher}}</span>
            </li>
          </ul>
        </div>

      </div>

    </div>
  </template>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import requestUser from "@/utils/RequestUser.js";
import {ElMessage} from "element-plus";

// 数据定义
const topCoverNews = ref([]) // 用于显示轮播图片
const carouselData = ref([]);
const typeNewsList = ref([]) // 用于显示类别对应的公告列表

// 加载轮播图数据
const loadCarouseData = async () => {
  try {
    const res = await requestUser.get("/carousel/select");
    if (res.code === "000000") {
      carouselData.value = res.data || [];
    }
  } catch (error) {
    ElMessage.error("加载轮播图数据失败");
  }
};

// 加载置顶、图片显示的公告新闻
const loadTopCoverNews = () => {
  requestUser
      .get('index/news/selectNewsByPage', {
        params: {
          pageNum: 1,
          pageSize: 5,
          top: 1,
          cover: 'cover',
        },
      })
      .then((res) => {
        topCoverNews.value = res.data?.list;
      });
};

// 加载类别对应的公告列表
const loadTypeNewsList = () => {
  requestUser.get('/index/news/typeNewsList').then((res) => {
    typeNewsList.value = res.data;
  });
};

onMounted(() => {
  loadTopCoverNews();
  loadCarouseData();
  loadTypeNewsList();
});

const truncateText = (text, maxLength = 22) => {
  if (!text) return ''; // 处理空值
  return text.length <= maxLength ? text : text.slice(0, maxLength) + '...';
};
</script>

<style scoped>
@import '@/assets/css/front/front.css';
</style>