<template>
  <div class="main-container">
    <div class="user-main-box">
      <div class="user-main-left">
        <h2 class="title">用户中心</h2>
        <el-menu style="border: none;" router :default-active="activeUserMenu">
          <el-menu-item class="item" index="/user/userInfo">
            <el-icon><User /></el-icon>
            <span slot="title">个人信息</span>
          </el-menu-item>
          <el-menu-item class="item" index="/user/userPassword">
            <el-icon><Lock /></el-icon>
            <span slot="title">修改密码</span>
          </el-menu-item>
          <el-menu-item class="item" index="/user/collection">
            <el-icon><Collection /></el-icon>
            <span slot="title">我的收藏</span>
          </el-menu-item>
          <el-menu-item class="item" index="/user/history">
            <el-icon><Location /></el-icon>
            <span slot="title">我的足迹</span>
          </el-menu-item>
          <el-menu-item class="item" index="/user/comments">
            <el-icon><Edit /></el-icon>
            <span slot="title">我的评论</span>
          </el-menu-item>
          <el-menu-item class="item" index="/user/myMessageBoard">
            <el-icon><Mic /></el-icon>
            <span slot="title">我的留言</span>
          </el-menu-item>

        </el-menu>
      </div>

      <div class="user-main-right">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  User,
  Lock,
  Collection,
  Location,
  Edit,
  Mic
} from '@element-plus/icons-vue'

const route = useRoute()

// 计算当前激活的菜单项
const activeUserMenu = computed(() => route.path)
</script>

<style scoped>
@import '@/assets/css/front/front.css';
.item {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: saturate(150%) blur(8px);
  border-radius: 20px;
}
</style>