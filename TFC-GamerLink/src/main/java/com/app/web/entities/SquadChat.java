package com.app.web.entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "squad_chats")
public class SquadChat {
    @Id
    private String id;
    
    @Column(nullable = false)
    private String name; // Nombre personalizable del chat
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;
    
    @ManyToMany
    private List<User> members = new ArrayList<>();
    
    private LocalDateTime createdAt;
    
    public static String generateDefaultName(Game game) {
        return "Escuadrón " + game.getName() + " - " + 
               LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM HH:mm"));
    }

}
