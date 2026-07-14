package net.huizhu.rabbit.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AlarmParameter implements Serializable {

    private HighWayAssist highWayAssist;

}
