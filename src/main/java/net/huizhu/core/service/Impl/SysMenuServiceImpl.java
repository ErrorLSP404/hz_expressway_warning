package net.huizhu.core.service.Impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.huizhu.core.dao.SysMenuDao;
import net.huizhu.core.entity.SysMenu;
import net.huizhu.core.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author huizhu
 * @since 2021-06-17
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuDao, SysMenu> implements ISysMenuService {

    @Autowired
    private SysMenuDao sysMenuDao;
    @Override
    public List<SysMenu> selectSysMenuByUserId(Long userid) {
        return sysMenuDao.selectSysMenuByUserId(userid);
    }
}
