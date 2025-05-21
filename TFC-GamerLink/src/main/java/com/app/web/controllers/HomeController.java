package com.app.web.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.web.entities.Notification;
import com.app.web.entities.User;
import com.app.web.service.FriendshipService;
import com.app.web.service.NotificationService;
import com.app.web.service.UserService;

@Controller
@RequestMapping("/")
public class HomeController {
	private final UserService userService;
	private final NotificationService notificationService;
	private final FriendshipService friendService;

    public HomeController(UserService userService, NotificationService notificationService,
						  FriendshipService friendService) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.friendService = friendService;
    }
	
	@GetMapping("/home")
	public String home(Model model, Principal principal) {
        User user = userService.getUserDetails();
        List<Notification> notifications = notificationService.getUserAllNotifications(user);
        int unreadCount = notificationService.getUnreadCount(user);
        List<User> friends = friendService.getUserFriends(user);
        
        model.addAttribute("user", user);
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("friends", friends);
        model.addAttribute("userRole", user.getRole().getName().toString());
        
        return "home";
	}
}
