import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', () => {
  const userInfo
      = ref({})

  const getUserInfo = computed(() =>{
      const storedInfo = localStorage.getItem('userInfo');
      if(storedInfo) {
          userInfo.value = JSON.parse(storedInfo);
      }
      return userInfo.value;
  })

  const setUserInfo = (data) =>{
      userInfo.value = data;
      localStorage.setItem('userInfo',JSON.stringify(data));
  }

  const updateUserInfo = () =>{
      const storedInfo = localStorage.getItem('userInfo');
      if(storedInfo) {
          userInfo.value = JSON.parse(storedInfo);
      }
  }

  const clearUserInfo = ()=>{
      userInfo.value = {}
      localStorage.removeItem('userInfo')
  }

  return {userInfo,getUserInfo,setUserInfo,updateUserInfo,clearUserInfo }
})

