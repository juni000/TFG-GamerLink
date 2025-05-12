package com.app.web.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.web.entities.Friendship;
import com.app.web.entities.User;
import com.app.web.enums.FriendshipStatus;
import com.app.web.repositories.FriendshipRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    public boolean existsFriendshipRequest(User user, User friend) {
        return friendshipRepository.existsByUserAndFriendAndStatus(user, friend, FriendshipStatus.PENDING) ||
               friendshipRepository.existsByUserAndFriendAndStatus(friend, user, FriendshipStatus.PENDING);
    }

    public Friendship sendFriendRequest(User user, User friend) {
        Friendship friendship = new Friendship();
        friendship.setUser(user);
        friendship.setFriend(friend);
        friendship.setStatus(FriendshipStatus.PENDING);
        return friendshipRepository.save(friendship);
    }

    public Friendship acceptFriendRequest(Friendship friendship) {
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        return friendshipRepository.save(friendship);
    }

    public void rejectFriendRequest(Friendship friendship) {
        friendship.setStatus(FriendshipStatus.REJECTED);
        friendshipRepository.save(friendship);
    }

    public List<User> getUserFriends(User user) {
    	List<Friendship> friendships = friendshipRepository.findByUserOrFriendAndStatus(user, user, FriendshipStatus.ACCEPTED);
        List<User> friends = new ArrayList<>();
        for (Friendship friendship : friendships) {
			if (friendship.getUser().equals(user) && friendship.getStatus() == FriendshipStatus.ACCEPTED) {
				friends.add(friendship.getFriend());
			} else if (friendship.getStatus() == FriendshipStatus.ACCEPTED) {
				friends.add(friendship.getUser());
			}
		}
    	return friends;
    }

    public List<Friendship> getPendingRequests(User user) {
    return  friendshipRepository.findByFriendAndStatus(user, FriendshipStatus.PENDING);
    }

	public Friendship findById(Long friendshipId) {
		return friendshipRepository.findById(friendshipId)
				.orElseThrow(() -> new EntityNotFoundException("Friendship not found"));
	}

	public List<Friendship> getUserFriendships(User currentUser) {
		return friendshipRepository.findByUserOrFriendAndStatus(currentUser, currentUser, FriendshipStatus.ACCEPTED);
	}
}