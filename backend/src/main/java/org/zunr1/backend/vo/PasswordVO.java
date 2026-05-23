package org.zunr1.backend.vo;

import lombok.Data;

@Data
public class PasswordVO {
    private String password;
    private String salt;
}
