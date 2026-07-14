package net.huizhu.core.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* <p>
    * 用户基本信息
    * </p>
*
* @author huizhu
* @since 2021-06-17
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
    * 创建时间
    */
    private LocalDateTime gmtCreate;

    /**
    * 修改时间
    */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime gmtModified;

    /**
    * 用户编号
    */
    private Long userNo;

    /**
    * 手机号码
    */
    private String mobile;

    /**
    * 登录密码
    */
    private String password;

    /**
    * 状态(1:正常，0:禁用)
    */
    private Integer status;

    /**
    * 用户类型(0:管理员)
    */
    private Integer userType;

    /**
    * 名称
    */
    private String name;

    /**
    * 备注
    */
    private String remark;


}
