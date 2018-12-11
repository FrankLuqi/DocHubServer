package com.example.dochubserver.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${com.privateKey}")
    private String privateKey;

    @Autowired
    UserService userService;

    /**
     * 生成jwt token
     * @param infos 需要放在jwt token中的属性
     * @param validtime 设置有效时间 如7代表7天
     * @param userId 用户id
     * @return
     * @throws Exception
     */
    public String GenerateToken(Map<String,Object> infos,int validtime,long userId) throws Exception
    {
        Date date = new Date();
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.DATE,validtime);//当前时间加上有效时间得到过期时间
        Date expareTime = nowTime.getTime();//过期时间

        Map<String,Object> map = new HashMap<>();
        map.put("alg","HS256");
        map.put("typ","JWT");
        JWTCreator.Builder builder= JWT.create();
        builder.withHeader(map);
        for (Map.Entry info:infos.entrySet())
        {
            builder.withClaim((String)info.getKey(),(String)info.getValue());
        }
        builder.withExpiresAt(expareTime);//设置过期时间
        String token = builder.withIssuedAt(date).sign(Algorithm.HMAC256(privateKey));//签发token
        System.out.println("token"+token);
        //签发token同时将用户的最近修改时间同步至缓存，达到使之前的token失效的效果
        userService.modifyUserChangeDate(date,userId);
        return token;
    }

    public DecodedJWT VerifyToken(String token)
    {
        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(privateKey)).build();
            jwt = verifier.verify(token);
        }catch (Exception e)
        {
            logger.error("解析token失败"+e.getMessage());
        }
        return jwt;
    }

}
