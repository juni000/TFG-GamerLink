package com.app.web.service;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.web.entities.ChatMessage;
import com.app.web.entities.SquadChat;
import com.app.web.entities.SquadSearch;
import com.app.web.entities.User;
import com.app.web.repositories.SquadChatRepository;

@Service
public class SquadChatService {
    private static final String SQUAD_CHATS_DIR = "squad_chats/";
    
    
    @Autowired
    private SquadChatRepository squadChatRepository;
    @Autowired
    private UserService userService;
    
    public SquadChatService() {
        try {
            Files.createDirectories(Paths.get(SQUAD_CHATS_DIR));
        } catch (IOException e) {
            System.err.println("Error creando directorio de chats de escuadrón: " + e.getMessage());
        }
    }
    
    /**
     * Crea un nuevo chat para un escuadrón completo
     */
    public SquadChat createSquadChat(SquadSearch completedSquad) {
        SquadChat chat = new SquadChat();
        chat.setId(generateSquadChatId(completedSquad));
        chat.setName(SquadChat.generateDefaultName(completedSquad.getGame()));
        chat.setGame(completedSquad.getGame());
        chat.setCreatedAt(LocalDateTime.now());
        
        // Añadir todos los miembros del escuadrón
        List<String> players = completedSquad.getPlayers();
        List<User> members = new ArrayList<>();
        if( players!=null) {
        	for (String player : players) {
				if (player != null && !player.isEmpty()) {
					members.add(userService.getUserById(player));
				}
			}
        }
        chat.setMembers(members);
        
        squadChatRepository.save(chat);
        createChatFile(chat.getId());
        
        return chat;
    }
    
    /**
     * Guarda un mensaje en el chat del escuadrón
     */
    public synchronized void saveSquadMessage(String squadChatId, ChatMessage message) {
        String filename = getSquadChatFilename(squadChatId);
        try {
            String entry = message.toString() + "\n";
            Files.write(Paths.get(filename), entry.getBytes(), 
                      StandardOpenOption.CREATE, 
                      StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Error guardando mensaje del escuadrón", e);
        }
    }
    
    /**
     * Obtiene el historial de mensajes del chat del escuadrón
     */
    public List<ChatMessage> getSquadChatHistory(String squadChatId) {
        String filename = getSquadChatFilename(squadChatId);
        try {
            if (Files.exists(Paths.get(filename))) {
                List<String> lines = Files.readAllLines(Paths.get(filename));
                List<ChatMessage> messages = new ArrayList<>();
                for (String line : lines) {
                    String[] parts = line.split(" ", 3);
                    if (parts.length >= 3) {
                        String timestamp = parts[0];
                        String senderId = parts[1].split("->")[0].trim();
                        String content = parts[2];
                        ChatMessage message = new ChatMessage(senderId, "SQUAD", content);
                        message.setTimestamp(LocalDateTime.parse(timestamp));
                        messages.add(message);
                    }
                }
                return messages;
            }
            return new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo historial del escuadrón", e);
        }
    }
    /**
	 * Cambia el nombre del chat del escuadrón
	 */
    public void updateChatName(String squadChatId, String newName, User requester) {
        SquadChat chat = squadChatRepository.findById(squadChatId)
                .orElseThrow(() -> new RuntimeException("Chat no encontrado"));
        
        // Verificar que el usuario que solicita el cambio es miembro
        if (!chat.getMembers().contains(requester)) {
            throw new SecurityException("No tienes permiso para modificar este chat");
        }
        
        chat.setName(newName);
        squadChatRepository.save(chat);
        
        // Opcional: guardar mensaje de sistema sobre el cambio
        ChatMessage systemMsg = new ChatMessage(
            "system",
            "SQUAD",
            "El chat ha sido renombrado a: " + newName
        );
        saveSquadMessage(squadChatId, systemMsg);
    }
    
    private String generateSquadChatId(SquadSearch squad) {
        return "squad_" + squad.getId() + "_" + 
               squad.getGame().getId() + "_" + 
               Instant.now().getEpochSecond();
    }

    public Optional<SquadChat> getSquadChatById(String id) {
        return squadChatRepository.findById(id);
    }
    
    public List<SquadChat> getUserSquadChats(String userId) {
        User user = new User();
        user.setId(userId);
        return squadChatRepository.findByMembersContaining(user);
    }
    /**
     * Crea el archivo físico para el chat
     */
    private void createChatFile(String squadChatId) {
        String filename = getSquadChatFilename(squadChatId);
        try {
            Files.createFile(Paths.get(filename));
        } catch (IOException e) {
            throw new RuntimeException("Error creando archivo de chat", e);
        }
    }

    private String getSquadChatFilename(String squadChatId) {
        return SQUAD_CHATS_DIR + squadChatId + ".txt";
    }
    

    public boolean isMember(String squadChatId, User user) {
        return squadChatRepository.findById(squadChatId)
                .map(chat -> chat.getMembers().contains(user))
                .orElse(false);
    }
    
}
