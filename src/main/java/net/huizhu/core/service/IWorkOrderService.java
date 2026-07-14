package net.huizhu.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.huizhu.core.entity.WorkOrder;

import java.util.Map;

/**
 * <p>
 * 工单表 服务类
 * </p>
 *
 * @author huizhu
 * @since 2022-04-28
 */
public interface IWorkOrderService extends IService<WorkOrder> {

    public Long statisticsOrder(Map<String,Object> map);

}
