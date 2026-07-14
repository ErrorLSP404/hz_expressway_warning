package net.huizhu.core.service.Impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.huizhu.core.dao.SysUserRoleDao;
import net.huizhu.core.entity.SysUserRole;
import net.huizhu.core.service.ISysUserRoleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户与角色关系表 服务实现类
 * </p>
 *
 * @author huizhu
 * @since 2021-06-17
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleDao, SysUserRole> implements ISysUserRoleService {

}
