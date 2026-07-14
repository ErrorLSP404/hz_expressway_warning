package net.huizhu.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.huizhu.core.entity.SysRole;

import java.util.List;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author huizhu
 * @since 2021-06-17
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * 根据用户ID获取角色集合
     * @param userid
     * @return
     */
    public List<SysRole> selectSysRoleByUserId(Long userid);

}
