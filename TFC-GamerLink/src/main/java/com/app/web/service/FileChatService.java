package com.app.web.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.web.entities.ChatMessage;
import com.app.web.entities.User;
import com.app.web.repositories.FriendshipRepository;

@Service
public class FileChatService {
    private static final String CHATS_DIR = "chats/";
    
    @Autowired
    private FriendshipService friendshipService; 
    
    public FileChatService() {
        try {
            Files.createDirectories(Paths.get(CHATS_DIR));
        } catch (IOException e) {
            System.err.println("Error creando directorio de chats: " + e.getMessage());
        }
    }
    
    public synchronized void saveMessage(ChatMessage message) {
        String filename = getChatFilename(message.getSenderId(), message.getReceiverId());
        try {
            String entry = message.toString() + "\n";
            Files.write(Paths.get(filename), entry.getBytes(), 
                      StandardOpenOption.CREATE, 
                      StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Error guardando mensaje", e);
        }
    }
    
    public List<ChatMessage> getChatHistory(String user1Id, String user2Id) {
        String filename = getChatFilename(user1Id, user2Id);
        try {
            if (Files.exists(Paths.get(filename))) {
				// Leemos el archivo y convertimos cada línea en un objeto ChatMessage
				List<String> lines = Files.readAllLines(Paths.get(filename));
				List<ChatMessage> messages = new ArrayList<>();
				for (String line : lines) {
					String[] parts = line.split(" ", 3);
					if (parts.length >= 3) {
						String timestamp = parts[0];
						String senderId = parts[1].split("->")[0].trim();
						String receiverId = parts[1].split("->")[1].trim();
						String content = parts[2];
						ChatMessage message = new ChatMessage(senderId, receiverId, content);
						message.setTimestamp(LocalDateTime.parse(timestamp));
						messages.add(message);
					}
				}
                return messages;
            }
            return new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo historial", e);
        }
    }
    
    private String getChatFilename(String user1Id, String user2Id) {
        // Ordenamos los IDs alfabéticamente para que el chat sea único
        String[] ids = {user1Id, user2Id};
        Arrays.sort(ids);
        
        // Reemplazamos caracteres no válidos para nombres de archivo
        String safeId1 = ids[0].replaceAll("[^a-zA-Z0-9.-]", "_");
        String safeId2 = ids[1].replaceAll("[^a-zA-Z0-9.-]", "_");
        
        return CHATS_DIR + "chat_" + safeId1 + "_" + safeId2 + ".txt";
    }
    
    public List<String> getRecentChatsForUser(String userId) {
        try {
            String safeUserId = userId.replaceAll("[^a-zA-Z0-9.-]", "_");
            return Files.list(Paths.get(CHATS_DIR))
                    .filter(path -> path.getFileName().toString().contains(safeUserId))
                    .map(path -> {
                        try {
                            List<String> lines = Files.readAllLines(path);
                            return lines.isEmpty() ? "" : lines.get(lines.size() - 1);
                        } catch (IOException e) {
                            return "";
                        }
                    })
                    .filter(line -> !line.isEmpty())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
    
    public List<Path> getChatFilesForUser(String userId) {
        try {
            return Files.list(Paths.get(CHATS_DIR))
                    .filter(path -> path.getFileName().toString().contains(userId))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public List<User> getFriendsWithChats(User currentUser, List<Path> chatFiles) {
        List<User> friends = friendshipService.getUserFriends(currentUser);
        List<User> friendsWithChats = new ArrayList<>();
        
        for (User friend : friends) {
            String friendId = friend.getId();
            boolean hasChat = chatFiles.stream()
                    .anyMatch(path -> path.getFileName().toString().contains(friendId));
            
            if (hasChat) {
                friendsWithChats.add(friend);
            }
        }
        
        return friendsWithChats;
    }
}