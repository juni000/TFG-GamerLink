package com.app.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {
	@NotBlank(message = "El nombre de usuario es obligatorio")
	private String username;

	@NotBlank(message = "La contraseña es obligatoria")
	@Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
	private String password;
	@NotBlank(message = "La confirmación de contraseña es obligatoria")
	@Size(min = 8, message = "La confirmación de contraseña debe tener al menos 8 caracteres")
	private String confirmPassword;
}
