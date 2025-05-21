package com.app.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.web.dto.UpdateUserDto;
import com.app.web.entities.User;
import com.app.web.service.ProfileService;
import com.app.web.service.UserService;

import jakarta.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    
    @Autowired
    private ProfileService profileService;
    @Autowired
    private UserService userService;
    
    @GetMapping("/edit")
    public String getProfilePage( Model model) {
    	User user = userService.getUserDetails();
    	model.addAttribute("user", user);
		return "profile";
	}
    @PostMapping("/edit")
    public String updateProfile(@ModelAttribute UpdateUserDto userDto, 
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        
        try {
            User user = userService.getUserDetails();
            User newUser = profileService.updateProfile(user.getUserName(), userDto);
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");
            return "redirect:/profile/edit";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("passwordError", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el avatar");
        }
        redirectAttributes.addFlashAttribute("userDto", userDto);
        return "redirect:/profile/edit";
    }
}
