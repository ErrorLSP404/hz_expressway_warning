package net.huizhu.security.handler;


import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import net.huizhu.common.api.CommonResult;
import net.huizhu.common.config.JWTConfig;
import net.huizhu.common.util.MD5Utils;
import net.huizhu.security.entity.SelfUserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 登出成功处理类
 * @Author Sans
 * @CreateTime 2019/10/3 9:42
 */
@Component
public class UserLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 用户登出返回结果
     * 这里应该让前端清除掉Token
     * @Author Sans
     * @CreateTime 2019/10/3 9:50
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication){
        String authorization = request.getHeader("Authorization");
        // 获取toke_key
        String toke_key = MD5Utils.MD5(authorization);
        redisTemplate.delete(toke_key);
        String token = authorization.replace(JWTConfig.tokenPrefix, "");
        // 解析JWT
        Claims claims = Jwts.parser()
                .setSigningKey(JWTConfig.secret)
                .parseClaimsJws(token)
                .getBody();
        // 获取用户名
        String username = claims.getSubject();
        redisTemplate.delete(username);
        SecurityContextHolder.clearContext();
        CommonResult.responseJson(response,CommonResult.resultCode(200,"操作成功"));
    }
}