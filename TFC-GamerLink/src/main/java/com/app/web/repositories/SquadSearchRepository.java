package com.app.web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.web.entities.Game;
import com.app.web.entities.SquadSearch;
import com.app.web.enums.SquadSearchStatus;

@Repository
public interface SquadSearchRepository extends JpaRepository<SquadSearch, Integer> {
    @Query("SELECT s FROM SquadSearch s WHERE " +
            "s.game.id = :gameId AND " +
            "s.searchSize = :searchSize AND " +
            "s.status = 'SEARCHING' AND " +
            "s.actualSize < s.searchSize")
     List<SquadSearch> findCompatibleSquads(
         @Param("gameId") int gameId,
         @Param("searchSize") int searchSize);
     
     // Método derivado actualizado
     List<SquadSearch> findByGame_IdAndSearchSizeAndStatus(
         @Param("gameId") int gameId,
         @Param("searchSize") int searchSize,
         @Param("status") SquadSearchStatus status);

    @Query("SELECT s FROM SquadSearch s WHERE " +
            "s.status = 'SEARCHING' AND " +
            "(s.player1 = :userId OR s.player2 = :userId OR s.player3 = :userId OR " +
            "s.player4 = :userId OR s.player5 = :userId OR s.player6 = :userId)")
     List<SquadSearch> findByPlayerAndStatus(
             @Param("userId") String userId,
             @Param("status")SquadSearchStatus status);

}