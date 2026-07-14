package net.huizhu.controller.vo;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AlgorithmVo {

    /**
     * 算法ID
     */
    private Integer code;

    /**
     * 算法名称
     */
    private String desc;
}
