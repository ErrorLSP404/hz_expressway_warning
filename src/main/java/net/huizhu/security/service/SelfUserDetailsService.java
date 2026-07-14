package net.huizhu.security.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.huizhu.core.entity.SysUser;
import net.huizhu.core.service.ISysUserService;
import net.huizhu.security.entity.SelfUserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * SpringSecurity用户的业务实现
 * @Author Sans
 * @CreateTime 2019/10/1 17:21
 */
@Component
public class SelfUserDetailsService implements UserDetailsService {

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 查询用户信息
     * @Author Sans
     * @CreateTime 2019/9/13 17:23
     * @Param  username  用户名
     * @Return UserDetails SpringSecurity用户信息
     */
    @Override
    public SelfUserEntity loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户信息
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(SysUser::getMobile,username);
        SysUser sysUser = sysUserService.getOne(queryWrapper);
        if (sysUser!=null){
            // 组装参数
            SelfUserEntity selfUserEntity = new SelfUserEntity();
            selfUserEntity.setUserId(sysUser.getId());
            selfUserEntity.setUsername(sysUser.getMobile());
            selfUserEntity.setPassword(sysUser.getPassword());
            Integer status = sysUser.getStatus();
            selfUserEntity.setStatus(status == 1?"NORMAL":"PROHIBIT");
            return selfUserEntity;
        }
        return null;
    }
}