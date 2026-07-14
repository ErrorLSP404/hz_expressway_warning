package net.huizhu.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.huizhu.core.entity.WorkOrderLog;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 工单日志表 Mapper 接口
 * </p>
 *
 * @author huizhu
 * @since 2022-04-28
 */
public interface WorkOrderLogDao extends BaseMapper<WorkOrderLog> {

    @Update("update work_order_log set last_node = 0  where work_no = #{workNo}")
    public boolean updateNode(Long workNo);


}
