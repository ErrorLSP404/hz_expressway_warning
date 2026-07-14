package net.huizhu.core.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.huizhu.core.dao.WorkOrderLogDao;
import net.huizhu.core.entity.WorkOrderLog;
import net.huizhu.core.service.IWorkOrderLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 工单日志表 服务实现类
 * </p>
 *
 * @author huizhu
 * @since 2022-04-28
 */
@Service
public class WorkOrderLogServiceImpl extends ServiceImpl<WorkOrderLogDao, WorkOrderLog> implements IWorkOrderLogService {

    @Autowired
    private WorkOrderLogDao workOrderLogDao;

    @Override
    public boolean updateNode(Long workNo) {
        return workOrderLogDao.updateNode(workNo);
    }
}
