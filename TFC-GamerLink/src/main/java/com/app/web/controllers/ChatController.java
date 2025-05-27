package com.app.web.controllers;

import java.nio.file.Path;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.web.entities.ChatMessage;
import com.app.web.entities.SquadChat;
import com.app.web.entities.User;
import com.app.web.service.FileChatService;
import com.app.web.service.FriendshipService;
import com.app.web.service.SquadChatService;
import com.app.web.service.UserService;

@Controller
@RequestMapping("/chat")
public class ChatController {
    
    private final FileChatService chatService;
    private final FriendshipService friendService;
    private final UserService userService;
    private final SquadChatService squadChatService;
    public ChatController(FileChatService chatService, FriendshipService friendService, UserService userService, SquadChatService squadChatService) {
		this.chatService = chatService;
		this.friendService = friendService;
		this.userService = userService;
		this.squadChatService = squadChatService;
	}
    
    @PostMapping("/send")
    public String sendMessage(
        @RequestParam String senderId,
        @RequestParam String receiverId,
        @RequestParam String content) {
        
        ChatMessage message = new ChatMessage(senderId, receiverId, content);
        chatService.saveMessage(message);
        return "redirect:/chat/conversation?user1Id=" + senderId + "&user2Id=" + receiverId;
    }
    
    @GetMapping("/conversation")
    public String getConversation(
        @RequestParam String user1Id,
        @RequestParam String user2Id,
        Model model) {
        
        List<ChatMessage> messages = chatService.getChatHistory(user1Id, user2Id);
        User currentUser = userService.getUserDetails();
        List<SquadChat> squadChats = squadChatService.getUserSquadChats(currentUser.getId());
        model.addAttribute("squadChats", squadChats);
        model.addAttribute("numberChats", chatService.getChatFilesForUser(currentUser.getId()).size());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("friends", friendService.getUserFriends(currentUser));
        model.addAttribute("messages", messages);
        //determinar el otro usuario
        if (user1Id.equals(currentUser.getId())) {
        	User user2 = userService.getUserById(user2Id);
			model.addAttribute("friend", user2);
		} else {
			User user1 = userService.getUserById(user1Id);
			model.addAttribute("friend", user1);
		}
        return "chat/chat";
    }
    
    @GetMapping("/recentchats")
    public String getRecentChats(Model model) {
        User currentUser = userService.getUserDetails();
        
        // Obtener lista de archivos de chat del usuario
        List<Path> chatFiles = chatService.getChatFilesForUser(currentUser.getId());
        
        // Obtener amigos con los que tiene conversación
        List<User> friendsWithChats = chatService.getFriendsWithChats(currentUser, chatFiles);
        List<SquadChat> squadChats = squadChatService.getUserSquadChats(currentUser.getId());
        model.addAttribute("squadChats", squadChats);
        model.addAttribute("numberChats", chatService.getChatFilesForUser(currentUser.getId()).size());
        model.addAttribute("friendsWithChats", friendsWithChats);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("friends", friendService.getUserFriends(currentUser));
        return "chat/recentchats";
    }
}
