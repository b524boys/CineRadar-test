<template>
  <div class="front">
    <div class="nav-container">
      <div id="nav-logo" >光影雷达</div>
      <div class="nav-bar">
        <router-link to="/home" class="nav-item" active-class="active"><el-icon style="margin-top: 3px;margin-right: 2px"><HomeFilled /></el-icon>首页</router-link>
        <router-link to="/movieList" class="nav-item" active-class="active"><el-icon style="margin-top: 3px;margin-right: 2px"><Menu /></el-icon>分类</router-link>
        <router-link to="/aichat" class="nav-item" active-class="active"><el-icon style="margin-top: 3px;margin-right: 2px"><ChatDotRound /></el-icon>AI聊电影</router-link>
        <router-link to="/messageBoard" class="nav-item" active-class="active"><el-icon style="margin-top: 3px;margin-right: 2px"><HelpFilled /></el-icon>动态</router-link>
        <router-link to="/news" class="nav-item" active-class="active"><el-icon style="margin-top: 2px;margin-right: 2px"><Promotion /></el-icon>影视新闻</router-link>
      </div>

      <div style="display: flex;align-items: center;padding-right: 10px">
        <div class="input-group">
          <el-icon :size="14" style="margin-left: 6px;margin-right: 3px"><Search /></el-icon>
          <input style="color: #919191;border-radius: 0;" type="search" placeholder="搜索电影" v-model="keyword" @keyup.enter="searchMovieListByMovieName"/>
        </div>

        <div v-if="!isLogin">
          <el-link type="primary" href="/user/login" underline="hover">登录</el-link>
          <el-divider  direction="vertical"></el-divider>
          <el-link type="primary" href="/user/register" underline="hover">注册</el-link>
        </div>

        <div v-if="isLogin" class="avatar-menu">
          <el-avatar class="nav-item-avater" :size="48" @click="showMenu=true">
            <img :src="headImg" alt="User Avatar" />
          </el-avatar>
          <div v-if="showMenu" class="menu"
               @mouseover="clearHideMenu"
               @mouseout="scheduleHideMenu"
          >
            <div class="menuli" style="color:#69b4ff;" @click = "router.push('/user/userInfo')">个人信息</div>
            <div class="menuli" style="color:#69b4ff;" @click = "router.push('/user/userPassword')">修改密码</div>
            <div class="menuli" style="color:#69b4ff;" @click = "router.push('/user/collection')">我的收藏</div>
            <div class="menuli" style="color:#69b4ff;" @click = "router.push('/user/history')">我的足迹</div>
            <div class="menuli" style="color:#69b4ff;" @click = "router.push('/user/comments')">我的评论</div>
            <div class="menuli" style="color:#69b4ff;" @click = "router.push('/user/myMessageBoard')">我的留言</div>
            <div class="menuli" style="color:#ff6969;" @click="logout">退出登录</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 路由出口 -->
    <div>
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
import {ElAvatar, ElMessage, ElMessageBox} from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import { useRouter, useRoute } from 'vue-router'
import eventBus from '@/utils/eventBus'
import {useUserStore} from "@/stores/user.js";
import {useTokenStore} from "@/stores/token.js";

const userStore = useUserStore();
const router = useRouter()
const route = useRoute()
const user = computed(() => userStore.getUserInfo);
const isLogin = computed(() => Object.keys(user.value).length > 0)

const keyword = ref('');
const showMenu = ref(false);
let timeoutId = null;

const searchMovieListByMovieName = () => {
  const currPath = route.path
  if (currPath === "/movieList") {
    eventBus.emit('searchMovieList', keyword.value)
  } else {
    router.push({ path: "/movieList", query: { goodsName: keyword.value } })
  }
}

const headImg = computed(() => {
  if (user.value.headImg) {
    return user.value.headImg
  }
  return new URL('@/assets/imgs/manage/head-default.png', import.meta.url).href
})

const logout = () => {
  ElMessageBox.confirm('确认退出吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    const tokenStore = useTokenStore();
    userStore.clearUserInfo()
    tokenStore.clearToken();
    router.push("/home")
    user.value = {}
  }).catch(() => {
    ElMessage.info('已取消退出')
  })
}
function scheduleHideMenu() {
  // 鼠标离开菜单时，设置一个定时器，延迟隐藏菜单
  timeoutId = setTimeout(() => {
    showMenu.value = false;
  }, 400); // 延迟时间可以根据需要调整
}

function clearHideMenu() {
  // 如果鼠标再次进入菜单，清除定时器，不隐藏菜单
  if (timeoutId) {
    clearTimeout(timeoutId);
  }
}
</script>

<style scoped>
.front {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.nav-container {
  width: 1400px; /* 设置固定宽度 */
  height: 56px;
  display: flex;
  justify-content: space-around;
  align-items: center;
  margin-top: 15px;
  margin-bottom: 12px;
  background-color: rgba(255, 255, 255, 0.6);
  backdrop-filter: saturate(150%) blur(8px);
  border-radius: 20px;
  padding: 4px;
  box-shadow: 0px 0px 30px 0px rgba(59, 163, 221, 0.29);
  /*margin-top: 2px;*/
  border-top-style:solid;
  border-left-style:solid;
  border-color: rgba(255, 255, 255, 0.84);
  border-width:1.5px;
}
.nav-bar{
  display: flex;
  align-items: center;
  gap: 20px;
}
#nav-logo {
  font-size: 26px;
  font-weight: bold;
  color: #0bb8ff;
  /*color: #2c90fb;*/
  /*color:#42b983;*/
 /* -webkit-text-stroke: 1.2px #42b983;*/
}

.nav-item {
  display: flex;
  text-decoration: none;
  padding: 13px 36px;
  border-radius: 15.5px;
  font-size: 15px;
  color: #000;
}

.nav-item:hover {
  background: linear-gradient(to right bottom,
  rgba(255, 255, 255, 0.66), rgba(255, 255, 255, 0.5), rgba(255, 255, 255, 0.66));
  border-top-style:solid;
  border-left-style:solid;
  border-color: rgba(255, 255, 255, 0.84);
  border-width:1.5px;
}
.nav-item.active {
  color: #40c6ff;
  font-size: 16.5px;
  font-weight: bold;
}
.nav-item-avater:hover {
  border:solid;
  border-color: rgba(0, 126, 255, 0.84);
  border-width:1.5px;
  box-shadow: 0 0 30px 0px rgba(59, 163, 221, 0.29);
  cursor: pointer;
}
.input-group {
  display: flex;
  align-items:center;
  width: 120px;
  height: 25px;
  border: 1px solid;
  border-radius: 50px;
  font-size: 12px;
  color: #919191;
  background-color: transparent;
  outline: none;
  margin-right: 30px;
}
.input-group input[type="search"] {
  width: 80%;
  border: 0px;
  border-radius: 50px;
  font-size: 12px;
  background-color: transparent;
  outline: none;
}
.avatar-menu {
  position: relative;
  margin-top: 3px;
  display: inline-block;
}
.menu {
  position: absolute;
  top: 110%;
  width: 100px;
  left: -24px;
  background: linear-gradient(to right bottom,
  rgba(255, 255, 255, 0.7), rgba(255, 255, 255, 0.6), rgba(255, 255, 255, 0.8));
  border: 1px solid #ddd;
  border-radius: 15px;
  padding: 8px;
}

.menuli {
  font-size: 14px;
  text-align: center;
  padding: 4px 8px;
  border-radius: 10px;
  cursor: pointer;
}

.menuli:hover {
  background: linear-gradient(to right bottom,
  rgba(255, 255, 255, 0.66), rgba(255, 255, 255, 0.5), rgba(255, 255, 255, 0.66));
  border-top-style:solid;
  border-left-style:solid;
  border-color: rgba(255, 255, 255, 0.84);
}
</style>