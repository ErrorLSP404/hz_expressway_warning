package net.huizhu.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.huizhu.core.entity.WorkOrderLog;

/**
 * <p>
 * 工单日志表 服务类
 * </p>
 *
 * @author huizhu
 * @since 2022-04-28
 */
public interface IWorkOrderLogService extends IService<WorkOrderLog> {

    /**
     *  修改节点状态
     */
    public boolean updateNode(Long workNo);

}
