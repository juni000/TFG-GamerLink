package com.app.web.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.web.dto.UpdateUserDto;
import com.app.web.entities.User;
import com.app.web.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProfileService {
	@Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UploadFileService uploadFileService;

    public User updateProfile(String currentUsername, UpdateUserDto dto) {
        User user = userRepository.findByUserName(currentUsername)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        
        // Actualizar username si es diferente
        if (dto.getUsername() != null && !dto.getUsername().equals(currentUsername)) {
            if (userRepository.existsByUserName(dto.getUsername())) {
                throw new IllegalArgumentException("El nombre de usuario ya está en uso");
            }
            user.setUserName(dto.getUsername());
        }
        
        // Actualizar contraseña si se proporciona
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                throw new SecurityException("Contraseña actual incorrecta");
            }
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        
        // Actualizar avatar si se sube uno nuevo
        if (dto.getAvatar() != null && !dto.getAvatar().isEmpty()) {
            // Eliminar avatar anterior si existe
            if (user.getAvatar() != null) {
                uploadFileService.delete(user.getAvatar());
            }
            try {
                String newAvatar = uploadFileService.save(dto.getAvatar());
                user.setAvatar(newAvatar);
            } catch (IOException e) {
                throw new RuntimeException("Error al guardar el avatar", e);
            }
        }
        
        return userRepository.save(user);
    }
}
