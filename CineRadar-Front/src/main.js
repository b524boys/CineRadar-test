/*import './assets/main.css'*/
import '@/assets/css/global.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
//import requestAdmin from "@/utils/RequestAdmin"
//import requestUser from "@/utils/RequestUser"

const app = createApp(App)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.use(createPinia())
app.use(ElementPlus)
app.use(router)

//app.config.globalProperties.$requestAdmin = requestAdmin;
//app.config.globalProperties.$requestUser = requestUser;
app.config.globalProperties.$baseUrl = "http://localhost:5000";


app.mount('#app')
