package com.wdy.module.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Admin {
    private long id;
    @NotBlank(message = "{user.username,notBlank}")
    private String username;
    @NotBlank(message = "{user.password,notBlank}")
    private String password;
    public Admin(){}
    public Admin(String username,String password){
        this.username = username;
        this.password = password;
    }
}
