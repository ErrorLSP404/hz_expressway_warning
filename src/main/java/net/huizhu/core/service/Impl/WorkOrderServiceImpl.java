package net.huizhu.core.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.huizhu.core.dao.WorkOrderDao;
import net.huizhu.core.entity.WorkOrder;
import net.huizhu.core.service.IWorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 工单表 服务实现类
 * </p>
 *
 * @author huizhu
 * @since 2022-04-28
 */
@Service
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderDao, WorkOrder> implements IWorkOrderService {

    @Autowired
    private WorkOrderDao workOrderDao;

    @Override
    public Long statisticsOrder(Map<String, Object> map) {
        return workOrderDao.statisticsOrder(map);
    }
}
