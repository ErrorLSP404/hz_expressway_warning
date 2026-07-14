package net.huizhu.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.huizhu.core.entity.SysMenu;

import java.util.List;

/**
 * <p>
 * 权限表 服务类
 * </p>
 *
 * @author huizhu
 * @since 2021-06-17
 */
public interface ISysMenuService extends IService<SysMenu> {


    /**
     * 查询用户的所有权限
     * @param userid
     * @return
     */
    public List<SysMenu> selectSysMenuByUserId(Long userid);
}
