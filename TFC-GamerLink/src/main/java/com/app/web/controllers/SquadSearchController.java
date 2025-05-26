package com.app.web.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.web.entities.SquadSearch;
import com.app.web.entities.SquadSearchResult;
import com.app.web.entities.User;
import com.app.web.service.FileChatService;
import com.app.web.service.FriendshipService;
import com.app.web.service.GameService;
import com.app.web.service.SquadSearchService;
import com.app.web.service.UserService;

@Controller
@RequestMapping("/squad")
public class SquadSearchController {

	   
	    private final SquadSearchService squadSearchService;
	    private final UserService userService;
	    private final FileChatService chatService;
		private final FriendshipService friendService;
		private final GameService gameService;
		public SquadSearchController(SquadSearchService squadSearchService, UserService userService, FriendshipService friendService, GameService gameService, FileChatService chatService) {
	        this.squadSearchService = squadSearchService;
	        this.userService = userService;
	        this.friendService = friendService;
	        this.gameService = gameService;
	        this.chatService = chatService;
	    }

	    // Mostrar formulario de búsqueda
	    @GetMapping("/search")
	    public String showSearchForm(Model model) {
	        User currentUser = userService.getUserDetails();
	        model.addAttribute("currentUser", currentUser);
	        model.addAttribute("numberChats", chatService.getChatFilesForUser(currentUser.getId()).size());
	        model.addAttribute("games", gameService.getAllGames());
	        model.addAttribute("friends", friendService.getUserFriends(currentUser));
	        model.addAttribute("activeSearches", squadSearchService.getUserActiveSquads(currentUser.getId()));
	        return "squad/search";
	    }

	    // Procesar búsqueda/creación de escuadrón
	    @PostMapping("/search")
	    public String processSearch(
	            @RequestParam Integer gameId,
	            @RequestParam Integer teamSize,
	            @RequestParam(required = false) List<String> friends,
	            RedirectAttributes redirectAttributes) {
	    	if (teamSize <= 1 + (friends != null ? friends.size() : 0)) {
	            redirectAttributes.addFlashAttribute("error", "El tamaño del equipo debe ser mayor que el número de amigos seleccionados");
	            return "redirect:/squad/search";
	        }
	        User currentUser = userService.getUserDetails();
	        SquadSearchResult result = squadSearchService.findOrCreateSquad(
	                currentUser.getId(), gameId, teamSize, friends);
	        if (result.isJoinedExisting()) {
	            redirectAttributes.addFlashAttribute("success", "Te has unido a un escuadrón existente");
	        } else {
	            redirectAttributes.addFlashAttribute("success", "Se ha creado un nuevo escuadrón");
	        }
	        redirectAttributes.addFlashAttribute("squad", result.getSquad());
	        redirectAttributes.addFlashAttribute("joinedExisting", result.isJoinedExisting());
	        return "redirect:/squad/search";
	    }

	    // Ver escuadrones activos del usuario
	    @GetMapping("/my-squads")
	    public String viewMySquads(Model model) {
	        User currentUser = userService.getUserDetails();
	        List<SquadSearch> squads = squadSearchService.getUserActiveSquads(currentUser.getId());
	        model.addAttribute("squads", squads);
	        return "squad/my-squads";
	    }

	    // Cancelar búsqueda de escuadrón
	    @PostMapping("/cancel/{squadId}")
	    public String cancelSquadSearch(@PathVariable Integer squadId, RedirectAttributes redirectAttributes) {
	        User currentUser = userService.getUserDetails();
	        boolean success = squadSearchService.cancelSquadSearch(squadId, currentUser.getId());

	        if (success) {
	            redirectAttributes.addFlashAttribute("success", "Búsqueda de escuadrón cancelada");
	        } else {
	            redirectAttributes.addFlashAttribute("error", "No se pudo cancelar la búsqueda");
	        }

	        return "redirect:/squad/my-squads";
	    }

	    // Ver detalles de un escuadrón
	    @GetMapping("/view/{squadId}")
	    public String viewSquad(@PathVariable Integer squadId, Model model) {
	        Optional<SquadSearch> squad = squadSearchService.getSquadById(squadId);
	        if (squad.isPresent()) {
	            model.addAttribute("squad", squad.get());
	            return "squad/view";
	        }
	        return "redirect:/squad/my-squads";
	    }
}
	    
