package com.app.web.service;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.web.dto.UserEditDto;
import com.app.web.entities.Role;
import com.app.web.entities.User;
import com.app.web.enums.RoleList;
import com.app.web.repositories.RoleRepository;
import com.app.web.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getName().toString());

        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassword(),
                Collections.singleton(authority)
        );
    }

    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public boolean existsByUserName(String username) {
        return userRepository.existsByUserName(username);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public User getUserDetails() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return findByUserName(username);
    }

    public void updateUser(String id, UserEditDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
        // Validación: No permitir duplicados de username (excepto si es el mismo usuario)
        if (userRepository.existsByUserNameAndIdNot(userDto.getUserName(), id)) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }

        // Validación: Un ADMIN no puede auto-degradarse a USER
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && 
            authentication.getName().equals(user.getUserName()) && 
            userDto.getRole() != RoleList.ROLE_ADMIN) {
            throw new IllegalStateException("No puedes cambiar tu propio rol de ADMIN");
        }

        // Actualizar campos
        user.setUserName(userDto.getUserName());
        Role role = roleRepository.findByName(userDto.getRole())
				.orElseThrow(() -> new EntityNotFoundException("Rol no encontrado"));
        user.setRole(role);
        
        // Auditoría
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    // Método auxiliar para obtener usuario
    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

}
