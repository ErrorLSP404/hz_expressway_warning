package net.huizhu.controller.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class PasswordRequest {

    private String username;
    private String oldPassword;

    private String newPassword;

    private String rePassword;
}
