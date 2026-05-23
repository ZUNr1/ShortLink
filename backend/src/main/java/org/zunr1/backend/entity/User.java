package org.zunr1.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String salt;
    private Integer role;
    private Integer status;
    private LocalDateTime createdAt;
}
