package net.huizhu.core.service.Impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.huizhu.core.dao.SysUserDao;
import net.huizhu.core.entity.SysUser;
import net.huizhu.core.service.ISysUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author huizhu
 * @since 2021-06-17
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUser> implements ISysUserService {

}
