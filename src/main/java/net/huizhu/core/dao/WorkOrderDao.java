package net.huizhu.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.huizhu.core.entity.WorkOrder;

import java.util.Map;

/**
 * <p>
 * 工单表 Mapper 接口
 * </p>
 *
 * @author huizhu
 * @since 2022-04-28
 */
public interface WorkOrderDao extends BaseMapper<WorkOrder> {

    public Long statisticsOrder(Map<String,Object> map);

}
