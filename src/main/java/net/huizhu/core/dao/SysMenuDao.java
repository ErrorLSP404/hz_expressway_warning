package net.huizhu.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.huizhu.core.entity.SysMenu;

import java.util.List;


/**
 * <p>
 * 权限表 Mapper 接口
 * </p>
 *
 * @author huizhu
 * @since 2021-06-17
 */
public interface SysMenuDao extends BaseMapper<SysMenu> {

    /**
     * 查询用户的所有权限
     * @param userid
     * @return
     */
    public List<SysMenu> selectSysMenuByUserId(Long userid);
}
