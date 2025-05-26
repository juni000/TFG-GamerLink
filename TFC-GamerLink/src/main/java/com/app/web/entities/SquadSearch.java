package com.app.web.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.app.web.enums.SquadSearchStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "squad_search")
public class SquadSearch {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "search_size", nullable = false)
    private Integer searchSize;

    @Column(name = "actual_size", nullable = false)
    private Integer actualSize = 1;

    @Column(name = "player1", nullable = false)
    private String player1;

    @Column(name = "player2")
    private String player2;

    @Column(name = "player3")
    private String player3;

    @Column(name = "player4")
    private String player4;

    @Column(name = "player5")
    private String player5;

    @Column(name = "player6")
    private String player6;
    
    public SquadSearch(Game game, Integer searchSize, String player1) {
        this.game = game;
        this.searchSize = searchSize;
        this.player1 = player1;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SquadSearchStatus status = SquadSearchStatus.SEARCHING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public boolean isComplete() {
        return actualSize >= searchSize;
    }

    public boolean hasAvailableSlot() {
        return actualSize < searchSize;
    }
    
    public void addPlayer(String playerId) {
        if (player2 == null) {
            player2 = playerId;
        } else if (player3 == null) {
            player3 = playerId;
        } else if (player4 == null) {
            player4 = playerId;
        } else if (player5 == null) {
            player5 = playerId;
        } else if (player6 == null) {
            player6 = playerId;
        } else {
            throw new IllegalStateException("No hay espacios disponibles en el escuadrón");
        }
        actualSize++;
        
        if (isComplete()) {
            status = SquadSearchStatus.COMPLETED;
        }
    }
    public List<String> getPlayers() {
        List<String> players = new ArrayList<>();
        if (player1 != null) players.add(player1);
        if (player2 != null) players.add(player2);
        if (player3 != null) players.add(player3);
        if (player4 != null) players.add(player4);
        if (player5 != null) players.add(player5);
        if (player6 != null) players.add(player6);
        return players;
    }
}
