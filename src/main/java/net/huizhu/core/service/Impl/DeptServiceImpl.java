package net.huizhu.core.service.Impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.huizhu.core.dao.DeptDao;
import net.huizhu.core.entity.Dept;
import net.huizhu.core.service.IDeptService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author huizhu
 * @since 2022-04-28
 */
@Service
public class DeptServiceImpl extends ServiceImpl<DeptDao, Dept> implements IDeptService {

}
