import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useTokenStore = defineStore('token', () => {
  const token = ref('')

  const getToken = computed(()=>{
    if(!token.value){
      token.value = localStorage.getItem('token');
    }
    return token.value
  })

  const setToken = data =>{
    token.value = data
    localStorage.setItem('token',data);
  }

  const clearToken = ()=>{
    token.value = '';
    localStorage.removeItem('token')
  }

  return {token,getToken,setToken,clearToken }
})

