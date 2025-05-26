package com.app.web.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.web.entities.ChatMessage;
import com.app.web.entities.SquadChat;
import com.app.web.entities.User;
import com.app.web.service.FriendshipService;
import com.app.web.service.SquadChatService;
import com.app.web.service.SquadSearchService;
import com.app.web.service.UserService;

@Controller
@RequestMapping("/squad")
public class SquadChatController {
		private final SquadChatService squadChatService;
	private final UserService userService;
	private final FriendshipService friendService;

	public SquadChatController(SquadChatService squadChatService, UserService userService, SquadSearchService squadSearchService, FriendshipService friendshipService) {
		this.squadChatService = squadChatService;
		this.userService = userService;
		this.friendService = friendshipService;
	}
	// Mostrar chat de un escuadrón específico
    @GetMapping("/{squadChatId}")
    public String getSquadChat(
            @PathVariable String squadChatId, 
            Model model) {
        User currentUser = userService.getUserDetails();
        // Verificar que el usuario es miembro del chat
        if (!squadChatService.isMember(squadChatId, currentUser)) {
            return "redirect:/squad/list";
        }
        
        Optional<SquadChat> squadChatOptional = squadChatService.getSquadChatById(squadChatId);
        if (squadChatOptional.isEmpty()) {
			return "redirect:/squad/list"; // Si no existe el chat, redirigir a la lista
		}
        List<ChatMessage> messages = squadChatService.getSquadChatHistory(squadChatId);
        SquadChat squadChat = squadChatOptional.get();
        
        // Map senderId a userName para mostrar en la vista
        Map<String, String> userNames = new HashMap<>();
        for (ChatMessage message : messages) {
			if (!userNames.containsKey(message.getSenderId())) {
				User sender = userService.getUserById(message.getSenderId());
				userNames.put(message.getSenderId(), sender != null ? sender.getUserName() : "Desconocido");
			}
		}
        model.addAttribute("userNames", userNames);
        model.addAttribute("squadChat", squadChat);
        model.addAttribute("messages", messages);
        model.addAttribute("user", currentUser);
        model.addAttribute("friends", friendService.getUserFriends(currentUser));
        
        return "squad/squad-conversation";
    }
 // Enviar mensaje al chat del escuadrón
    @PostMapping("/{squadChatId}/send")
    public String sendMessage(
            @PathVariable String squadChatId,
            @RequestParam String content) {
        User currentUser = userService.getUserDetails();
        ChatMessage message = new ChatMessage(
            currentUser.getId(), 
            "SQUAD", 
            content
        );
        squadChatService.saveSquadMessage(squadChatId, message);
        
        return "redirect:/squad/" + squadChatId;
    }

    // Renombrar chat de escuadrón
    @PostMapping("/{squadChatId}/rename")
    public String renameChat(
            @PathVariable String squadChatId,
            @RequestParam String newName,
            RedirectAttributes redirectAttributes) {
        User currentUser = userService.getUserDetails();
        try {
            squadChatService.updateChatName(squadChatId, newName, currentUser);
            redirectAttributes.addFlashAttribute("success", "Nombre del chat actualizado");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/squad/" + squadChatId;
    }

    // Listar todos los chats de escuadrón del usuario
    @GetMapping("/list")
    public String listSquadChats(Model model) {
    	User currentUser = userService.getUserDetails();
        List<SquadChat> squadChats = squadChatService.getUserSquadChats(currentUser.getId());
        
        model.addAttribute("squadChats", squadChats);
        model.addAttribute("user", currentUser);
        model.addAttribute("friends", friendService.getUserFriends(currentUser));
        return "squad/squad-chats";
    }
}
