package com.wdy.module.utils;

import io.jsonwebtoken.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @auther: dong_wu
 * @date: 2019/4/2 14:50
 * @description:
 */

public class JWTTokenUtil {
    private final static String base64Secret = "MDk4ZjZiY2Q0NjIxZDM3M2NhZGU0ZTgzMjYyN2I0WDY=";
    public static String createJWTToken(Map<String,Object> claims,Long expireMillisSeconds){
//        iss: jwt签发者
//        sub: jwt所面向的用户
//        aud: 接收jwt的一方
//        exp: jwt的过期时间，这个过期时间必须要大于签发时间
//        nbf: 定义在什么时间之前，该jwt都是不可用的.
//        iat: jwt的签发时间
//        jti: jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击。
        HashMap<String, Object> header = new HashMap<>();
        header.put("alg",SignatureAlgorithm.HS256.getJcaName());
        header.put("typ","JWT");
        JwtBuilder jwtBuilder = Jwts.builder()
                // 头部
                .setHeader(header)
                // 载荷
                .addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expireMillisSeconds))
                // 签名这个部分需要base64加密后的header和base64加密后的payload使用.连接组成的字符串，然后通过header中声明的加密方式进行加盐secret组合加密，然后就构成了jwt的第三部分
                .signWith(SignatureAlgorithm.HS256, base64Secret);
        return jwtBuilder.compact();
    }
    public static Map parseJWToken(String jwtWebToken){
        Claims body = Jwts.parser().setSigningKey(base64Secret)
                .parseClaimsJws(jwtWebToken).getBody();
        return body;
    }
}
