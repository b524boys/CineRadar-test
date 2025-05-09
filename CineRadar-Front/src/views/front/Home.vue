<template>
  <template class="main-box">

    <div class="ai-box">
      <div class="ai-search" style="display: flex;margin-top: 14px;">
        <div class="ai-input">
          <input style="color: #4a4a4a" type="search" placeholder="随便说点什么，让Ai来帮你推荐" v-model="input" @keyup.enter="AiRecommend"/>
          <div class="cbtn" @click="AiRecommend">
            <el-icon size="20"><ArrowRightBold /></el-icon>
          </div>
        </div>
<!--        <el-button type="danger" plain @click="SwitchAiRecommendList">Switch</el-button>-->
      </div>

      <Transition name="fade-slide">
      <div v-if="isOpen" class="ai-content">
        <!-- 骨架屏 -->
        <div v-if="loading" class="ai-card" v-for="n in skeletonCount" :key="'skeleton-'+n">
          <GlowCard>
            <div class="card-content skeleton-content">
              <el-skeleton class="cover" animated>
                <template #template>
                  <el-skeleton-item variant="image" style="width: 100%; height: 100%" />
                </template>
              </el-skeleton>
              <div class="movie-name">
                <el-skeleton animated>
                  <template #template>
                    <el-skeleton-item variant="text" style="width: 60px;height: 13px;" />
                  </template>
                </el-skeleton>
              </div>
              <div class="movie-reason">
                <el-skeleton animated>
                  <template #template>
                    <el-skeleton-item variant="text" style="width: 100px;height: 10px;" />
                  </template>
                </el-skeleton>
              </div>
            </div>
          </GlowCard>
        </div>
        <!-- 实际内容 -->
        <div class="ai-card" v-for="item in airecommendMoviesData" :key="item.id" v-show="!loading">
          <GlowCard @click="ToAiMovieDetail(item.url)">
            <div class="card-content">
              <el-image :src="item.cover" class="cover"/>
              <div class="movie-name">{{item.name}}</div>
              <div class="movie-reason">"{{item.reason}}"</div>
            </div>
          </GlowCard>
        </div>
      </div>
      </Transition>
    </div>

    <div class="main-container">
      <div class="tips-box">
        <el-icon size="20px" color="#474747"><Histogram /></el-icon>
        <a class="title">推荐电影</a>
        <a class="description">根据与您相似的用户推荐</a>
      </div>

      <!-- 推荐电影列表 -->
<!--      <el-row style="margin-bottom:15px">-->
<!--        <el-col :span="6" v-for="item in recommendGoodsData" :key="item.id">-->
<!--          <el-card :body-style="{padding:'0px'}" class="goods-item" @click="movieDetail(item.id)">-->
<!--            <div class="item-card-body">-->
<!--              <el-image :src="item.cover" class="cover"/>-->
<!--              <el-tooltip effect="light" :content="'评分: '+item.rating" placement="top" open-delay="1000">-->
<!--                <div class="goods-name">-->
<!--                  {{item.goodsName}}-->
<!--                </div>-->
<!--              </el-tooltip>-->
<!--            </div>-->
<!--          </el-card>-->
<!--        </el-col>-->
<!--      </el-row>-->


        <!-- 推荐电影列表 -->
      <el-skeleton :loading="loadingRec" animated>
        <!-- 数据加载时的骨架屏 -->
        <template #template>
          <el-row style="margin-bottom:15px">
            <el-col :span="6" v-for="i in 8" :key="'skeleton-'+i">
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
            <el-row style="margin-bottom:15px">
              <el-col :span="6" v-for="item in recommendGoodsData" :key="item.id">
                <el-card :body-style="{padding:'0px'}" class="goods-item" @click="movieDetail(item.id)">
                  <div class="item-card-body">
                    <el-image :src="item.cover" class="cover" />
                    <el-tooltip effect="light" :content="'评分: '+item.rating" placement="top" open-delay="1000">
                      <div class="goods-name">
                        {{item.goodsName}}
                      </div>
                    </el-tooltip>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </template>
      </el-skeleton>



      <div class="tips-box">
        <el-icon size="20px" color="#474747"><GoldMedal /></el-icon>
        <a class="title">热门电影</a>
        <a class="description">当前实时热度排行榜</a>
      </div>

      <!-- 热门电影列表展示 -->
<!--      <el-row>-->
<!--        <el-col :span="6" v-for="item in hotGoodsData" :key="item.id">-->
<!--          <el-card :body-style="{padding:'0px'}" class="goods-item" @click="movieDetail(item.id)">-->
<!--            <el-image :src="item.cover" class="cover"/>-->
<!--            <el-tooltip effect="light" :content="'评分: '+item.rating" placement="top" open-delay="1000">-->
<!--              <div class="goods-name">-->
<!--                {{item.goodsName}}-->
<!--              </div>-->
<!--            </el-tooltip>-->
<!--          </el-card>-->
<!--        </el-col>-->
<!--      </el-row>-->

      <!-- 热门电影列表展示 -->
      <el-skeleton :loading="loadingHot" animated>
        <!-- 数据加载时的骨架屏 -->
        <template #template>
          <el-row style="margin-bottom:15px">
            <el-col :span="6" v-for="i in 8" :key="'skeleton-'+i">
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
            <el-col :span="6" v-for="item in hotGoodsData" :key="item.id">
              <el-card :body-style="{padding:'0px'}" class="goods-item" @click="movieDetail(item.id)">
                <el-image :src="item.cover" class="cover"/>
                <el-tooltip effect="light" :content="'评分: '+item.rating" placement="top" open-delay="1000">
                  <div class="goods-name">
                    {{item.goodsName}}
                  </div>
                </el-tooltip>
              </el-card>
            </el-col>
          </el-row>
        </template>
      </el-skeleton>



    </div>
  </template>
</template>

<script setup>
import {ref, onMounted,} from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import requestUser from "@/utils/RequestUser.js";
import {useUserStore} from "@/stores/user.js";
import GlowCard from "@/components/GlowCard.vue";

    const router = useRouter();
    const userStore = useUserStore();

    // 定义响应式数据
    const isOpen = ref(false);
    const loading = ref(true);
    const loadingRec = ref(true);
    const loadingHot = ref(true);
    const hotGoodsData = ref([]);
    const recommendGoodsData = ref([]);
    const airecommendMoviesData = ref([]);
    const input = ref('')
    const user = ref(userStore.getUserInfo);

    // 骨架屏显示的数量，可以根据需要调整
    const skeletonCount = ref(3)

    // 加载热门电影数据
    const loadHotGoodsData = async () => {
      try {
        const res = await requestUser.get('/index/goods/selectHotGoods');
        if (res.code === '000000') {
          hotGoodsData.value = res.data || [];
          setTimeout(() => {
            loadingHot.value = false;
          }, 1500);
          // loadingHot.value = false;
        }
      } catch (error) {
        ElMessage.error("加载热门电影数据失败");
      }
    };

    // 加载推荐电影数据
    const loadRecommendGoodsData = async () => {
      try {
        const res = await requestUser.get('/index/goods/queryRecommendGoods/' + user.value.id);
        if (res.code === '000000') {
          recommendGoodsData.value = res.data || [];
          setTimeout(() => {
            loadingRec.value = false;
          }, 1500);
          // loadingRec.value = false;
        }
      } catch (error) {
        ElMessage.error("加载推荐电影数据失败");
      }
    };

    // 跳转到电影详情页
    const movieDetail = (id) => {
      const route = router.resolve({
        name: 'MovieDetail',
        query: {id}
      })
      window.open(route.href, '_blank')
      //router.push({ name: 'MovieDetail', query: { id: id } });
    };

    const ToAiMovieDetail = (url) => {
      window.open( url ,'_blank');
    }

    const AiRecommend = async () => {
      //用户必须登录后才能调用ai推荐
      if (Object.keys(user.value).length === 0) {
        ElMessage.info("请先登录")
        return
      }
      try {
        if(input.value === null || input.value === "") {
          ElMessage.warning("请输入内容")
        }
        else{
          loading.value = true;
          isOpen.value = true;
          const res = await requestUser.get('/ai/recommend?message=' + input.value);
          if (res.code === '000000') {
            console.log(res);
            airecommendMoviesData.value = res.data || [];
            loading.value = false;
          }
        }
      } catch (error) {
        ElMessage.error("获取Ai推荐电影列表失败");
      }
    }

    const SwitchAiRecommendList = () => {
      isOpen.value = isOpen.value !== true;
    }

    // 生命周期钩子：组件挂载时加载数据
    onMounted(() => {
      loadHotGoodsData();
      loadRecommendGoodsData();
    });
</script>

<style scoped>
@import '@/assets/css/front/front.css';

/* 淡入淡出 + 轻微上移动画 */
.fade-slide-enter-active {
  transition: all 3s ease;
}
.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(150px);
}

.ai-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 12px;
}
.ai-content {
  overflow: auto;
  display: flex;
  flex-direction: row;
  width: 815px;
}
.ai-content::-webkit-scrollbar {
  display: none;  /* Chrome, Safari and Opera */
}
.skeleton-content {
  opacity: 0.4;
}
.skeleton-content :deep(.el-skeleton__image) {
  border-radius: 9px;
}
.ai-card {
  padding: 10px 15px;
}
.card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 2;
  width: 202px;
  height: 307px;
}
/*图片*/
.card-content .cover {
  height: 250px;
  width: 180px;
  border-radius: 9px;
  border-width: 1px;
  border-style: solid;
  border-color: rgba(255, 123, 37, 0.5);
}
.card-content .movie-name {
  margin-top: 7px;
  line-height: 25px;
  height: 25px;
  font-size: 14.5px;
  font-weight: bold;
  /*font-family: 微软雅黑;*/
  color: #ffffff;
  text-align: center;
}
.card-content .movie-reason {
  line-height: 25px;
  height: 25px;
  font-size: 13px;
  color: #ffe3d5;
  text-align: center;
}
.ai-input {
  display: flex;
  align-items:center;
  width: 600px;
  height: 40px;
  border-width: 1px;
  border-color: #ffffff;
  border-style: solid;
  background-color: rgba(255, 255, 255, 0.8);
  backdrop-filter: saturate(150%) blur(8px);
  border-radius: 50px;
  margin-right: 30px;
  box-shadow: 0px 0px 20px 0px rgba(59, 163, 221, 0.19);
}
.ai-input input[type="search"] {
  width: 91.2%;
  height: 33px;
  border: 0px;
  font-size: 14px;
  background-color: transparent;
  outline: none;
  margin-left: 14px;
}
.cbtn{
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  border-width: 1px;
  border-color: rgba(110, 174, 248, 0.28);
  border-style: solid;
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
  background-color: white;
  color: #2c90fb;
}
.cbtn:hover{
  background-color: rgb(207, 241, 253);
  color: #ffffff;
  box-shadow: 0px 0px 20px 0px rgba(59, 163, 221, 0.39);
}
.cbtn:active {
  background-color: rgb(207, 241, 253);
  color:  #2c90fb;
}
</style>