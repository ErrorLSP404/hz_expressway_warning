package net.huizhu.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkNodeEnum {

    ADMIN_NODE(1,"第一节点"),DEPT_NODE(2,"第二节点"),OFFICE_NODE(3,"第三节点");

    private Integer code;
    private String desc;

}
