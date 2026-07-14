package net.huizhu.core.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.huizhu.core.dao.OfficeDao;
import net.huizhu.core.entity.Office;
import net.huizhu.core.service.IOfficeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 科室表 服务实现类
 * </p>
 *
 * @author huizhu
 * @since 2022-04-28
 */
@Service
public class OfficeServiceImpl extends ServiceImpl<OfficeDao, Office> implements IOfficeService {

}
