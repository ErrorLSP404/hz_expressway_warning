package net.huizhu.controller;

import cn.hutool.core.bean.BeanUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import net.huizhu.common.api.CommonResult;
import net.huizhu.common.config.JWTConfig;
import net.huizhu.common.util.JWTTokenUtil;
import net.huizhu.common.util.MD5Utils;
import net.huizhu.controller.dto.PasswordRequest;
import net.huizhu.controller.dto.UserResponse;
import net.huizhu.controller.vo.UserVo;
import net.huizhu.core.entity.SysUser;
import net.huizhu.core.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private ISysUserService iSysUserService;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;


    /**
     * 获取角色Tag
     */
    @PreAuthorize("hasAnyRole('ADMIN','DEPT','OFFICE')")
    @PostMapping(value = "/getUserTag")
    public CommonResult<UserResponse> getUserInfo(@RequestHeader("Authorization") String token){
        // 获取用户id 用于返回不同的页面
        Claims claims = JWTTokenUtil.getClaims(token);
        Long userId = Long.valueOf(claims.getId());

        UserResponse userResponse = new UserResponse();
        SysUser sysUser = iSysUserService.getById(userId);
        if (sysUser != null) {
            UserVo userVo = new UserVo();
            BeanUtil.copyProperties(sysUser,userVo);
            userResponse.setUserVo(userVo);
            return CommonResult.success(userResponse);
        } else {
            return CommonResult.failed("用户数据获取异常");
        }

    }

    /**
     * 修改密码
     */
    @PreAuthorize("hasAnyRole('ADMIN','DEPT','OFFICE')")
    @PostMapping(value = "/updatePassword")
    public CommonResult<UserResponse> updatePassword(@RequestHeader("Authorization") String token
            ,@RequestBody PasswordRequest passwordRequest) {
        Claims claims = JWTTokenUtil.getClaims(token);
        Long userId = Long.valueOf(claims.getId());

        String oldPassword = passwordRequest.getOldPassword();
        String newPassword = passwordRequest.getNewPassword();
        String rePassword = passwordRequest.getRePassword();

        // 获取用户信息
        SysUser sysUser = iSysUserService.getById(userId);
        // 比对原始密码
        if (!passwordEncoder.matches(oldPassword,sysUser.getPassword())) {
            return CommonResult.failed("旧密码输入错误！");
        }
        // 比对二次密码
        if (!newPassword.equals(rePassword)) {
            return CommonResult.failed("两次密码输入不一致！");
        }
        // 修改密码
        sysUser.setPassword(passwordEncoder.encode(newPassword));
        iSysUserService.updateById(sysUser);

        // 登出逻辑
        String toke_key = MD5Utils.MD5(token);
        redisTemplate.delete(toke_key);
        String username = claims.getSubject();
        redisTemplate.delete(username);
        SecurityContextHolder.clearContext();

        return CommonResult.success();
    }
}
