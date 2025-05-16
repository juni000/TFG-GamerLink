package com.app.web.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.web.dto.UpdateUserDto;
import com.app.web.entities.User;
import com.app.web.service.ProfileService;
import com.app.web.service.UserService;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    
    @Autowired
    private ProfileService profileService;
    @Autowired
    private UserService userService;
    
    @GetMapping("/profile")
    public String getProfilePage() {
		return "profile";
	}
    @GetMapping
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUserName(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }
    
    @PutMapping
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute UpdateUserDto updateDto) {
        
        try {
            User updatedUser = profileService.updateProfile(
                userDetails.getUsername(), 
                updateDto
            );
            
            return ResponseEntity.ok(Map.of(
                "message", "Perfil actualizado",
                "avatar", updatedUser.getAvatar() != null ? 
                         "/uploads/" + updatedUser.getAvatar() : null
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
}
