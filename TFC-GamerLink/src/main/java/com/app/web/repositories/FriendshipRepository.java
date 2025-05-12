package com.app.web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.web.entities.Friendship;
import com.app.web.entities.User;
import com.app.web.enums.FriendshipStatus;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

	boolean existsByUserAndFriendAndStatus(User user, User friend, FriendshipStatus pending);

	List<Friendship> findByUserOrFriendAndStatus(User user, User friend, FriendshipStatus accepted);

	List<Friendship> findByUserAndStatus(User user, FriendshipStatus accepted);

	List<Friendship> findByFriendAndStatus(User friend, FriendshipStatus pending);


}
