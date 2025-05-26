package com.app.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.web.entities.Game;
import com.app.web.repositories.GameRepository;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    // Obtener todos los juegos
    public List<Game> getAllGames() {
        return gameRepository.findAllByOrderByNameAsc();
    }
    // Obtener juego por ID
    public Game getGameById(Integer id) {
        return gameRepository.findById(id).orElse(null);
    }

    // Crear o actualizar juego
    public Game saveGame(Game game) {
        // Validación básica
        if (game.getName() == null || game.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del juego es requerido");
        }
        
        // Si es nuevo, asegurarse que no existe otro con el mismo nombre
        if (game.getId() == null && gameRepository.findByName(game.getName()) != null) {
            throw new IllegalArgumentException("Ya existe un juego con ese nombre");
        }
        
        return gameRepository.save(game);
    }

    // Eliminar juego
    public void deleteGame(Integer id) {
        gameRepository.deleteById(id);
    }

    // Buscar juegos por nombre (búsqueda parcial)
    public List<Game> searchGamesByName(String name) {
        return gameRepository.findByNameContainingIgnoreCase(name);
    }
}
