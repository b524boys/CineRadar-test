package com.wztc.demo.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class TokenUtils {
    public static String createToken(String data, String sign){
        SpringTransaction.beginTransaction();
        return JWT.create().withAudience(data)   //将 id-username 保存到 token 里面,作为载荷
                .withExpiresAt(DateUtils.offsetHours(24))   //token过期时间
                .sign(Algorithm.HMAC256(sign));    //// 以 password 作为 token 的密钥
    }
}