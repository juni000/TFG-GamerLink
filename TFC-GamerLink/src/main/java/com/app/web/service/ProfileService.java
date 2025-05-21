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
                throw new IllegalArgumentException("El nombre de usuario '" + dto.getUsername() + "' ya está en uso");
            }
            user.setUserName(dto.getUsername());
        }
        
        // Actualizar contraseña si se proporciona
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            if (dto.getCurrentPassword() == null || dto.getCurrentPassword().isEmpty()) {
                throw new SecurityException("Debe ingresar la contraseña actual para cambiarla");
            }
            if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                throw new SecurityException("La contraseña actual no es correcta");
            }
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        
        // Actualizar avatar
        if (dto.getAvatar() != null && !dto.getAvatar().isEmpty()) {
            try {
                if (user.getAvatar() != null) {
                    uploadFileService.delete(user.getAvatar());
                }
                String newAvatar = uploadFileService.save(dto.getAvatar());
                user.setAvatar(newAvatar);
            } catch (IOException e) {
                throw new RuntimeException("Error al guardar el avatar: " + e.getMessage(), e);
            }
        }
        
        return userRepository.save(user);
    }
}
