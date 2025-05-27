package com.app.web.controllers;

import java.util.Arrays;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.web.dto.UserEditDto;
import com.app.web.entities.Game;
import com.app.web.entities.User;
import com.app.web.enums.RoleList;
import com.app.web.service.FileChatService;
import com.app.web.service.FriendshipService;
import com.app.web.service.GameService;
import com.app.web.service.UploadFileService;
import com.app.web.service.UserService;

@Controller
@RequestMapping("/")
public class AdminController {

	private final UserService userService;
    private final FileChatService chatService;
	private final FriendshipService friendService;
	private final GameService gameService;
	private final UploadFileService uploadFileService;
	
	public AdminController(UserService userService, FriendshipService friendService, FileChatService chatService, GameService gameService, UploadFileService uploadFileService) {
		this.userService = userService;
		this.friendService = friendService;
		this.chatService = chatService;
		this.gameService = gameService;
		this.uploadFileService = uploadFileService;
	}
	
	@GetMapping("/admin/users")
	public String userManagement(Model model, 
	                           @AuthenticationPrincipal UserDetails userDetails) {
	    // Verificar si el usuario es ADMIN
	    if (!userDetails.getAuthorities().stream()
	            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
	        return "redirect:/home";
	    }
	    User currentUser = userService.getUserDetails();
        model.addAttribute("numberChats", chatService.getChatFilesForUser(currentUser.getId()).size());
        model.addAttribute("friends", friendService.getUserFriends(currentUser));
        model.addAttribute("user", currentUser);
	    model.addAttribute("users", userService.getAllUsers());
	    model.addAttribute("games", gameService.getAllGames());
	    return "admin/user-list";
	}

	@PostMapping("/admin/users/delete/{id}")
	public String deleteUser(@PathVariable String id,
	                        @AuthenticationPrincipal UserDetails userDetails) {
	    if (!userDetails.getAuthorities().stream()
	            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
	        return "redirect:/home";
	    }
	    userService.deleteUser(id);
	    return "redirect:/admin/users?deleteSuccess=true";
	}
	
	@GetMapping("/admin/users/edit/{id}")
	public String editUserForm(@PathVariable String id, Model model,
	                         @AuthenticationPrincipal UserDetails userDetails) {
	    // Verificar permisos
	    if (!userDetails.getAuthorities().stream()
	            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
	        return "redirect:/home";
	    }
	    
	    User userToModify = userService.getUserById(id);
	    model.addAttribute("userToModify", userToModify);
	    User currentUser = userService.getUserDetails();
        model.addAttribute("user", currentUser);
        model.addAttribute("numberChats", chatService.getChatFilesForUser(currentUser.getId()).size());
        model.addAttribute("friends", friendService.getUserFriends(currentUser));
	    model.addAttribute("roles", Arrays.asList(RoleList.values())); // Enum de roles
	    return "admin/edit-user";
	}

	@PostMapping("/admin/users/edit/{id}")
	public String updateUser(@PathVariable String id,
	                       @ModelAttribute("user") UserEditDto userDto,
	                       BindingResult result,
	                       RedirectAttributes redirectAttributes) {
	    
	    if (result.hasErrors()) {
	        return "admin/edit-user";
	    }
	    
	    try {
	        userService.updateUser(id, userDto);
	        redirectAttributes.addFlashAttribute("success", "Usuario actualizado correctamente");
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("error", e.getMessage());
	    }
	    
	    return "redirect:/admin/users";
	}
	
	@PostMapping("/addgame")
    public String addGame(
            @RequestParam String name,
            @RequestParam MultipartFile logoFile,
            @RequestParam String description,
            RedirectAttributes redirectAttributes) {
        
        try {
            Game game = new Game();
            game.setName(name);
            game.setDescription(description);
            if (logoFile != null && !logoFile.isEmpty()) {
				String logoUrl = uploadFileService.save(logoFile);
				game.setLogoUrl(logoUrl);
			}
            
            gameService.saveGame(game);
            redirectAttributes.addAttribute("successgame", true);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/users";
    }
}
