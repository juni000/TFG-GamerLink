package com.app.web.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDto {
	@Size(min = 3, max = 20, message = "El nombre debe tener entre 3 y 20 caracteres")
    private String username;
    
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    
    private String currentPassword;
    private MultipartFile avatar;
}
