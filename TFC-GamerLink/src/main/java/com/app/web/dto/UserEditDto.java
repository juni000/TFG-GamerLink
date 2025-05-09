package com.app.web.dto;

import com.app.web.enums.RoleList;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEditDto {
    @NotBlank(message = "Username es requerido")
    private String userName;
    
    private RoleList role;
    
}