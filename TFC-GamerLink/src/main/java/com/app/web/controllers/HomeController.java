package com.app.web.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.web.entities.Notification;
import com.app.web.entities.SquadChat;
import com.app.web.entities.User;
import com.app.web.service.FileChatService;
import com.app.web.service.FriendshipService;
import com.app.web.service.NotificationService;
import com.app.web.service.SquadChatService;
import com.app.web.service.UserService;

import jakarta.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/home")
public class HomeController {
	private final UserService userService;
	private final NotificationService notificationService;
	private final FriendshipService friendService;
    private final FileChatService chatService;
    private final SquadChatService squadChatService;


    public HomeController(UserService userService, NotificationService notificationService,
						  FriendshipService friendService, FileChatService chatService, SquadChatService squadChatService) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.friendService = friendService;
        this.chatService = chatService;
        this.squadChatService = squadChatService;
    }
	
	@GetMapping("/home")
	public String home(Model model, Principal principal) {
        User user = userService.getUserDetails();
        List<Notification> notifications = notificationService.getUserAllNotifications(user);
        int unreadCount = notificationService.getUnreadCount(user);
        List<User> friends = friendService.getUserFriends(user);
        List<SquadChat> squadChats = squadChatService.getUserSquadChats(user.getId());
        model.addAttribute("squadChats", squadChats);
        model.addAttribute("numberChats", chatService.getChatFilesForUser(user.getId()).size());
        model.addAttribute("user", user);
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("friends", friends);
        model.addAttribute("userRole", user.getRole().getName().toString());
        
        return "home";
	}
	
	@PostMapping("/notifications/markAsRead/{id}")
    public String markNotificationAsRead(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserDetails();
            notificationService.markAsRead(id, user);
            redirectAttributes.addFlashAttribute("success", "Notificación marcada como leída");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Notificación no encontrada");
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/home/home";
    }

    @PostMapping("/notifications/markAllAsRead")
    public String markAllNotificationsAsRead(@AuthenticationPrincipal UserDetails userDetails,
                                           RedirectAttributes redirectAttributes) {
        User user = userService.getUserDetails();
        notificationService.markAllAsRead(user);
        redirectAttributes.addFlashAttribute("success", "Todas las notificaciones marcadas como leídas");
        return "redirect:/home/home";
    }
}
