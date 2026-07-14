package net.huizhu.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.huizhu.core.entity.SysRole;

import java.util.List;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @author huizhu
 * @since 2021-06-17
 */
public interface SysRoleDao extends BaseMapper<SysRole> {

    /**
     * 根据用户ID获取角色集合
     * @param userid
     * @return
     */
    public List<SysRole> selectSysRoleByUserId(Long userid);

}
