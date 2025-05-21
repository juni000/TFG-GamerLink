package com.app.web.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.web.entities.Friendship;
import com.app.web.entities.User;
import com.app.web.enums.NotificationType;
import com.app.web.service.FriendshipService;
import com.app.web.service.NotificationService;
import com.app.web.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/")
public class FriendsController {

    private final FriendshipService friendshipService;
    private final UserService userService;
    private final NotificationService notificationService;

    public FriendsController(FriendshipService friendshipService, 
                              UserService userService,
                              NotificationService notificationService) {
        this.friendshipService = friendshipService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    // Enviar solicitud de amistad
    @PostMapping("/request")
    public String sendFriendRequest(@Valid String friendId,
                                             @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.findByUserName(userDetails.getUsername());
            
            User friend = userService.findByUserName(friendId);
            // Verificar si ya existe una solicitud
            if (friendshipService.existsFriendshipRequest(currentUser, friend)) {
            	redirectAttributes.addFlashAttribute("error", "Ya has enviado una solicitud de amistad a este usuario");
                return "redirect:/friends";
            }

            Friendship friendship = friendshipService.sendFriendRequest(currentUser, friend);
            
            // Crear notificación
            notificationService.createNotification(
                friend, 
                currentUser.getUserName() + " te ha enviado una solicitud de amistad",
                NotificationType.FRIEND_REQUEST
            );
            redirectAttributes.addFlashAttribute("success", "Solicitud de amistad enviada");
            return "redirect:/friends"; // Redirigir a la página de amigos
        } catch (Exception e) {
        	redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/friends";
        }
    }

    // Aceptar solicitud de amistad
    @PostMapping("/accept/{friendshipId}")
    public String acceptFriendRequest(@PathVariable Long friendshipId,
                                               @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.findByUserName(userDetails.getUsername());
            
            Friendship friendship = friendshipService.findById(friendshipId);

            // Verificar que el usuario actual es el destinatario de la solicitud
            if (!friendship.getFriend().equals(currentUser)) {
            	redirectAttributes.addFlashAttribute("error", "No tienes permiso para aceptar esta solicitud");
                return "redirect:/friends";
            }

            Friendship acceptedFriendship = friendshipService.acceptFriendRequest(friendship);
            
            // Crear notificación de aceptación
            notificationService.createNotification(
                friendship.getUser(), 
                currentUser.getUserName() + " ha aceptado tu solicitud de amistad",
                NotificationType.FRIEND_REQUEST_ACCEPTED
            );
            redirectAttributes.addFlashAttribute("success", "Solicitud de amistad aceptada");
            return "redirect:/friends"; // Redirigir a la página de amigos
        } catch (Exception e) {
        	redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/friends";
        }
    }

    // Rechazar solicitud de amistad
    @PostMapping("/reject/{friendshipId}")
    public String rejectFriendRequest(@PathVariable Long friendshipId,
                                              @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.findByUserName(userDetails.getUsername());
            
            Friendship friendship = friendshipService.findById(friendshipId);

            // Verificar que el usuario actual es el destinatario de la solicitud
            if (!friendship.getFriend().equals(currentUser)) {
            	redirectAttributes.addFlashAttribute("error", "No tienes permiso para rechazar esta solicitud");
                return "redirect:/friends";
            }
            
            friendshipService.rejectFriendRequest(friendship);
            // Crear notificación de declinación
            notificationService.createNotification(
				friendship.getUser(), 
				currentUser.getUserName() + " ha rechazado tu solicitud de amistad",
				NotificationType.FRIEND_REQUEST_DECLINED
			);
            redirectAttributes.addFlashAttribute("success", "Solicitud de amistad rechazada");
            return "redirect:/friends";
        } catch (Exception e) {
        	redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/friends";
        }
    }

    // Obtener lista de amigos
    @GetMapping
    public ResponseEntity<?> getFriends(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User currentUser = ( userService.findByUserName(userDetails.getUsername()));
            
            List<Friendship> friendships = friendshipService.getUserFriendships(currentUser);
            return ResponseEntity.ok(friendships);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Obtener solicitudes pendientes
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingRequests(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User currentUser = userService.findByUserName(userDetails.getUsername());
            
            List<Friendship> pendingRequests = friendshipService.getPendingRequests(currentUser);
            return ResponseEntity.ok(pendingRequests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/friends")
    public String friends(Model model, Principal principal) {
        User currentUser = userService.getUserDetails();
        
        model.addAttribute("user", currentUser);
        model.addAttribute("userRole", currentUser.getRole().getName().toString());
        model.addAttribute("friends", friendshipService.getUserFriends(currentUser));
        model.addAttribute("pendingRequests", friendshipService.getPendingRequests(currentUser));
        return "friends";
	}
}
