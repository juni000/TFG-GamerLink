package com.app.web.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.app.web.entities.Game;
import com.app.web.entities.SquadSearch;
import com.app.web.entities.SquadSearchResult;
import com.app.web.enums.NotificationType;
import com.app.web.enums.SquadSearchStatus;
import com.app.web.repositories.SquadSearchRepository;

import jakarta.transaction.Transactional;

@Service
public class SquadSearchService {

    private final SquadSearchRepository squadSearchRepository;

    private final UserService userService;
    
    private final GameService gameService;
    
    private final NotificationService notificationService;
    
    private final SquadChatService squadChatService;
    
    public SquadSearchService(SquadSearchRepository squadSearchRepository, UserService userService, GameService gameService, NotificationService notificationService, SquadChatService squadChatService) {
		this.squadSearchRepository = squadSearchRepository;
		this.userService = userService;
		this.gameService = gameService;
		this.notificationService = notificationService;
		this.squadChatService = squadChatService;
	}
    
    // Crear o unirse a un escuadrón
    @Transactional
    public SquadSearchResult findOrCreateSquad(String leaderId, Integer gameId, Integer teamSize, List<String> friendIds) {
        // Validación básica
        if (teamSize < 2 || teamSize > 6) {
            throw new IllegalArgumentException("El tamaño del equipo debe estar entre 2 y 6");
        }
        
        // Buscar escuadrones compatibles
        List<SquadSearch> compatibleSquads = squadSearchRepository.findByGame_IdAndSearchSizeAndStatus(
        		gameId, teamSize, SquadSearchStatus.SEARCHING);
        
        // Intentar unirse a un escuadrón existente
        for (SquadSearch squad : compatibleSquads) {
        	boolean validSquad = true;
            if (squad.hasAvailableSlot() && (1 + (friendIds != null ? friendIds.size() : 0)) <= (squad.getSearchSize() - squad.getActualSize())) {
            	// verificar si el líder ya está en el escuadrón
            	if (squad.getPlayers().contains(leaderId)) {
            		validSquad = false;
            	}
            	if (friendIds !=null){
            		for (String friendId : friendIds) {
						if (squad.getPlayers().contains(friendId)) {
							validSquad = false;
						}
					}
            	}
            	if (validSquad) {
					return joinExistingSquad(squad, leaderId, friendIds);
				}
            }
        }
        Game game = gameService.getGameById(gameId);
        // Si no hay escuadrones disponibles, crear uno nuevo
        return createNewSquad(leaderId, game, teamSize, friendIds);
    }

    private SquadSearchResult joinExistingSquad(SquadSearch squad, String leaderId, List<String> friendIds) {
		notificationService.createNotification(
				userService.getUserById(leaderId), 
				"Te has unido al escuadrón de " + squad.getGame().getName() + " con " + squad.getActualSize() + " jugadores mas.",
				NotificationType.SQUAD_SEARCH);
		
        // Añadir líder
        squad.addPlayer(leaderId);
        
        // Añadir amigos si existen
        if (friendIds != null) {
            for (String friendId : friendIds) {
                squad.addPlayer(friendId);
                notificationService.createNotification(
						userService.getUserById(friendId), 
						"Se ha unido al escuadrón de " + squad.getGame().getName() + " con " + squad.getActualSize() + " jugadores mas.",
						NotificationType.SQUAD_SEARCH);
            }
        }
       
        // Actualizar el estado del escuadrón si está completo
        if (squad.getStatus() == SquadSearchStatus.COMPLETED) {
        	squadChatService.createSquadChat(squad);
        	for (String playerId : squad.getPlayers()) {
				// Notificar a todos los jugadores que se ha completado el escuadrón
				notificationService.createNotification(
						userService.getUserById(playerId), 
						"El escuadrón de " + squad.getGame().getName() + " está completo.",
						NotificationType.SQUAD_SEARCH);
			}
		} else {
			if(friendIds == null) {
				notificationService.createNotification(
						userService.getUserById(squad.getPlayer1()), 
						"Se ha unido " + (squad.getActualSize() - 1) + " jugador a tu escuadrón de " + squad.getGame().getName() + ".",
						NotificationType.SQUAD_SEARCH);
			} else {
				notificationService.createNotification(
						userService.getUserById(squad.getPlayer1()), 
						"Se han unido " + (squad.getActualSize() - 1) + " jugadores a tu escuadrón de " + squad.getGame().getName() + ".",
						NotificationType.SQUAD_SEARCH);
			}
		}

        squadSearchRepository.save(squad);
        return new SquadSearchResult(squad, true);
    }

    private SquadSearchResult createNewSquad(String leaderId, Game game, Integer teamSize, List<String> friendIds) {
        SquadSearch newSquad = new SquadSearch(game, teamSize, leaderId);
        
        // Añadir amigos si existen
        if (friendIds != null) {
            for (String friendId : friendIds) {
                newSquad.addPlayer(friendId);
                notificationService.createNotification(
            			userService.getUserById(friendId),
            			"Se ha creado un nuevo escuadrón de " + game.getName() + " con " + newSquad.getActualSize() + " jugadores.",
            			NotificationType.SQUAD_SEARCH);
            }
        }
        notificationService.createNotification(
			userService.getUserById(leaderId), 
			"Has creado un nuevo escuadrón de " + game.getName() + " con " + newSquad.getActualSize() + " jugadores.",
			NotificationType.SQUAD_SEARCH);
        squadSearchRepository.save(newSquad);
        return new SquadSearchResult(newSquad, false);
    }

    // Obtener escuadrones activos de un usuario
    public List<SquadSearch> getUserActiveSquads(String userId) {
        return squadSearchRepository.findByPlayerAndStatus(userId, SquadSearchStatus.SEARCHING);
    }

    // Obtener escuadrón por ID
    public Optional<SquadSearch> getSquadById(Integer squadId) {
        return squadSearchRepository.findById(squadId);
    }

    // Cancelar búsqueda de escuadrón
    @Transactional
    public boolean cancelSquadSearch(Integer squadId, String userId) {
        Optional<SquadSearch> squadOpt = squadSearchRepository.findById(squadId);
        if (squadOpt.isPresent() && squadOpt.get().getPlayers().contains(userId)) {
            squadSearchRepository.delete(squadOpt.get());
            return true;
        }
        return false;
    }


}