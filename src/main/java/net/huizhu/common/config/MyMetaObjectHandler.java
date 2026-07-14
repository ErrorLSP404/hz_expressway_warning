package net.huizhu.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Object gmtModified  = getFieldValByName("gmtCreate", metaObject);
        if(null == gmtModified){
            //字段为空，可以进行填充
            // 获取当前日期
            LocalDateTime now = LocalDateTime.now();
            setFieldValByName("gmtCreate",now, metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Object gmtModified = getFieldValByName("gmtModified", metaObject);
        if(null == gmtModified){
            //字段为空，可以进行填充
            // 获取当前日期
            LocalDateTime now = LocalDateTime.now();
            setFieldValByName("gmtModified",now, metaObject);
        }
    }
}
