import { createRouter, createWebHistory } from 'vue-router';

const routes = [
  /*{path: '/', name: '首页', component: () => import('../views/HomeView.vue')},*/
  {
    path: '/',
    name: 'Front',
    redirect: '/home',
    meta: { name: '首页框架' },
    component: () => import('../views/front/Front.vue'),
    children: [
      { path: 'home', name: 'Home', meta: { name: '系统首页' }, component: () => import('../views/front/Home.vue') },
      { path: 'movieList', name: 'MovieList', meta: { name: '电影列表' }, component: () => import('../views/front/MovieList.vue') },
      { path: 'movieDetail', name: 'MovieDetail', meta: { name: '电影详情' }, component: () => import('../views/front/MovieDetail.vue') },
      { path: 'messageBoard', name: 'MessageBoard', meta: { name: '留言板' }, component: () => import('../views/front/MessageBoard.vue') },
      { path: 'aichat', name: 'AiChat', meta: { name: 'Ai聊天' }, component: () => import('../views/front/AiChat.vue') },
      { path: 'news', name: 'News', meta: { name: '公告' }, component: () => import('../views/front/News.vue') },
      { path: 'newsDetail', name: 'NewsDetail', meta: { name: '公告详情' }, component: () => import('../views/front/NewsDetail.vue') },
      { path: 'newsList', name: 'NewsList', meta: { name: '公告列表' }, component: () => import('../views/front/NewsList.vue') },
      {
        path: 'user',
        name: 'User',
        component: () => import('../views/front/user/Index.vue'),
        children: [
          { path: 'userInfo', name: 'UserInfo', meta: { name: '个人信息' }, component: () => import('../views/front/user/UserInfo.vue') },
          { path: 'userPassword', name: 'UserPassword', meta: { name: '修改密码' }, component: () => import('../views/front/user/UserPassword.vue') },
          { path: 'collection', name: 'Collection', meta: { name: '我的收藏' }, component: () => import('../views/front/user/Collect.vue') },
          { path: 'history', name: 'History', meta: { name: '我的足迹' }, component: () => import('../views/front/user/History.vue') },
          { path: 'comments', name: 'Comments', meta: { name: '我的评论' }, component: () => import('../views/front/user/Comments.vue') },
          { path: 'myMessageBoard', name: 'MyMessageBoard', meta: { name: '我的留言' }, component: () => import('../views/front/user/MyMessageBoard.vue') },
        ],
      },
    ],
  },
  { path: '/user/register', name: 'Register', meta: { name: '注册' }, component: () => import('../views/front/Register.vue') },
  { path: '/user/login', name: 'Login', meta: { name: '登录' }, component: () => import('../views/front/Login.vue') },
  { path: '/about', name: '关于', component: () => import('../views/AboutView.vue') },
  {
    path: '/manage',
    name: 'Manage',
    component: () => import('../views/manage/Manage.vue'),
    children: [
      { path: 'home', name: 'ManageHome', meta: { name: '管理系统首页' }, component: () => import('../views/manage/ManageHome.vue') },
      { path: 'admin', name: 'Admin', meta: { name: '管理员管理' }, component: () => import('../views/manage/Admin.vue') },
      { path: 'adminInfo', name: 'AdminInfo', meta: { name: '管理员信息' }, component: () => import('../views/manage/AdminInfo.vue') },
      { path: 'adminPassword', name: 'AdminPassword', meta: { name: '修改密码' }, component: () => import('../views/manage/AdminPassword.vue') },
      { path: 'user', name: 'UserManagement', meta: { name: '用户管理' }, component: () => import('../views/manage/User.vue') },
      { path: 'category', name: 'Category', meta: { name: '类型管理' }, component: () => import('../views/manage/Category.vue') },
      { path: 'movies', name: 'Movies', meta: { name: '电影管理' }, component: () => import('../views/manage/Movies.vue') },
      { path: 'carousel', name: 'Carousel', meta: { name: '轮播图管理' }, component: () => import('../views/manage/Carousel.vue') },
      { path: 'comments', name: 'CommentsManagement', meta: { name: '评论管理' }, component: () => import('../views/manage/Comments.vue') },
      { path: 'messageBoard', name: 'ManageMessageBoard', meta: { name: '留言板管理' }, component: () => import('../views/manage/MessageBoard.vue') },
      { path: 'newsType', name: 'NewsType', meta: { name: '公告类型管理' }, component: () => import('../views/manage/NewsType.vue') },
      { path: 'news', name: 'ManageNews', meta: { name: '公告管理' }, component: () => import('../views/manage/News.vue') },
      { path: 'sysLog', name: 'SysLog', meta: { name: '系统日志' }, component: () => import('../views/manage/SysLog.vue') },
    ],
  },
  { path: '/manage/login', name: 'ManageLogin', meta: { name: '登录' }, component: () => import('../views/manage/Login.vue') },
  { path: '/:pathMatch(.*)*', name: 'NotFound', meta: { name: '无法访问' }, component: () => import('../views/404.vue') },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

/*router.afterEach((to, from, next) => {
  document.title = to.meta.title;
})*/

export default router;

/*import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('@/views/front/Home.vue'),
    },
  ],
})

export default router*/
