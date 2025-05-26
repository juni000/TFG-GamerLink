package com.app.web.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.web.entities.SquadChat;
import com.app.web.entities.User;

@Repository
public interface SquadChatRepository extends JpaRepository<SquadChat, String> {
    Optional<SquadChat> findById(String id);
    List<SquadChat> findByMembersContaining(User user);

}