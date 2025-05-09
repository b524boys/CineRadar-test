<template>
  <div class="manager-container">
    <div class="manager-top">
      <div class="manager-top-left">
        <div class="title">光影雷达管理系统</div>
      </div>

      <div class="manager-top-breadcrumb">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item :to="{ path:'/manage/home' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: $route.path }">{{ $route.meta.name }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <div class="manager-top-right">
        <el-dropdown>
          <div class="head">
            <img :src="headImg">
            <div>
              {{admin.nickName}}
              <el-icon><CaretBottom /></el-icon>
            </div>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="router.push('/manage/adminInfo')">个人信息</el-dropdown-item>
              <el-dropdown-item @click="router.push('/manage/adminPassword')">修改密码</el-dropdown-item>
              <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <div class="manager-main">
      <!--侧边栏-->
      <div class="manager-main-left">
        <el-menu :default-openeds="['info','system']" style="border: none;" router :default-active="activeMenu">
          <el-menu-item index="/manage/home">
            <el-icon><HomeFilled /></el-icon>
<!--            <span slot="title">系统首页</span>-->
            <template #title>系统首页</template>
          </el-menu-item>

          <el-sub-menu index="info">
            <template #title>
<!--              <el-icon color="#409efc"><Menu /></el-icon>-->
              <span style="color: white">信息管理</span>
            </template>
            <el-menu-item index="/manage/admin">管理员管理</el-menu-item>
            <el-menu-item index="/manage/user">用户管理</el-menu-item>
            <el-menu-item index="/manage/category">电影类型管理</el-menu-item>
            <el-menu-item index="/manage/movies">电影管理</el-menu-item>
            <el-menu-item index="/manage/comments">评论管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="system">
            <template #title>
<!--              <el-icon><Setting /></el-icon>-->
              <span style="color: white">系统管理</span>
            </template>
            <el-menu-item index="/manage/carousel">轮播图管理</el-menu-item>
            <el-menu-item index="/manage/messageBoard">动态管理</el-menu-item>
            <el-menu-item index="/manage/newsType">新闻类型管理</el-menu-item>
            <el-menu-item index="/manage/news">新闻管理</el-menu-item>
            <el-menu-item index="/manage/sysLog">系统日志</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </div>

      <!--右侧数据-->
      <div class="manager-main-right">
        <router-view />
      </div>
    </div>

  </div>
</template>

<script setup>
import {ref, computed, onMounted, onBeforeUnmount, getCurrentInstance} from 'vue'
import { useRouter, useRoute } from 'vue-router'
import eventBus from "@/utils/eventBus.js";
import {CaretBottom} from "@element-plus/icons-vue";

const router = useRouter()
const route = useRoute()
const { $baseUrl } = getCurrentInstance().appContext.config.globalProperties;

// 管理员信息
const admin = ref(JSON.parse(localStorage.getItem('admin-token') || '{}'))

// 计算当前激活的菜单
const activeMenu = computed(() => route.path)

// 头像显示
const headImg = computed(() => {
  if (admin.value.headImg) {
    return admin.value.headImg
  } else {
    return new URL('@/assets/imgs/manage/head-default.png', import.meta.url).href
  }
})

// 退出登录
const logout = () => {
  localStorage.removeItem('admin-token')
  router.push('/manage/login')
}

// 挂载时检查登录状态
onMounted(() => {
  if (Object.keys(admin.value).length === 0) {
    //未登录直接跳转到登录页面
    router.push('/manage/login')
  } else if (route.path === '/manage') {
    router.push('/manage/home')
  }
  // 监听刷新管理员信息的事件
  eventBus.on('refreshAdmin', () => {
    admin.value = JSON.parse(localStorage.getItem('admin-token') || '{}')
  })
})

// 组件销毁前移除事件监听
onBeforeUnmount(() => {
  eventBus.off('refreshAdmin')
})
</script>

<style scoped>
@import '@/assets/css/manage/manage.css';
</style>