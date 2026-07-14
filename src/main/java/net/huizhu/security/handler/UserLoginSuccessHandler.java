package net.huizhu.security.handler;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import net.huizhu.common.api.CommonResult;
import net.huizhu.common.config.JWTConfig;
import net.huizhu.common.constant.RedisConstant;
import net.huizhu.common.util.JWTTokenUtil;
import net.huizhu.common.util.MD5Utils;
import net.huizhu.security.entity.SelfUserEntity;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description 登录成功处理类
 * @Author Sans
 * @CreateTime 2019/10/3 9:13
 */
@Slf4j
@Component
public class UserLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    /**
     * 登录成功返回结果
     * @Author Sans
     * @CreateTime 2019/10/3 9:27
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication){
        // 组装JWT
        SelfUserEntity selfUserEntity =  (SelfUserEntity) authentication.getPrincipal();
        String username = selfUserEntity.getUsername();
        //查询缓存是否存在
        String token = (String) redisTemplate.opsForValue().get(username);
        if(StrUtil.isBlank(token)){
            //不存在
            //生成新token
            token = JWTTokenUtil.createAccessToken(selfUserEntity);
            token = JWTConfig.tokenPrefix + token;
            //存入Redis
            redisTemplate.opsForValue().set(username,token);
            String toke_key = MD5Utils.MD5(token);
            redisTemplate.opsForValue().set(toke_key,token);
            redisTemplate.expire(username,JWTConfig.expiration,TimeUnit.MILLISECONDS);
            redisTemplate.expire(toke_key,JWTConfig.expiration,TimeUnit.MILLISECONDS);
        }
        // 封装返回参数
        Map<String,Object> resultData = new HashMap<>();
        resultData.put("code",200);
        resultData.put("message", "登录成功");
        resultData.put("token",token);
        CommonResult.responseJson(response,resultData);
    }
}