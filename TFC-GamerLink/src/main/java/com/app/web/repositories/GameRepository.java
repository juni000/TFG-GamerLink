package com.app.web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.web.entities.Game;

public interface GameRepository  extends JpaRepository<Game, Integer> {
    List<Game> findByNameContainingIgnoreCase(String name);
    List<Game> findAllByOrderByNameAsc();
	Object findByName(String name);
}
