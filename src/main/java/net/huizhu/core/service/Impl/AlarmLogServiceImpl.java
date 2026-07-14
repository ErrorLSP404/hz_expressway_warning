package net.huizhu.core.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.huizhu.core.dao.AlarmLogDao;
import net.huizhu.core.entity.AlarmLog;
import net.huizhu.core.service.IAlarmLogService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lsp
 * @since 2021-11-30
 */
@Service
public class AlarmLogServiceImpl extends ServiceImpl<AlarmLogDao, AlarmLog> implements IAlarmLogService {

}
