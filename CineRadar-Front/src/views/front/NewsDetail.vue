<template>
  <div class="main-container">
    <div class="news-detail-header">
      <h2 class="news-detail-title">{{news.title}}</h2>
      <span>浏览数:{{news.hits}}  发布人:{{news.publisher}}</span>
    </div>

    <div class="new-detail-content" v-html="news.content"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import requestUser from "@/utils/RequestUser.js";

const route = useRoute();
const newsId = ref(null);
const news = ref({});

// 加载新闻详情
const loadNewsDetail = () => {
  if (newsId.value) {
    requestUser.get('/index/news/detail/' + newsId.value).then((res) => {
      if (res.code === '000000') {
        news.value = res.data;
      }
    });
  }
};

// 初始化
onMounted(() => {
  newsId.value = route.query.id; // 从路由参数中获取新闻 ID
  loadNewsDetail();
});
</script>

<style scoped>
@import '@/assets/css/front/front.css';
</style>
