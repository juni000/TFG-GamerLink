package com.app.web.enums;

public enum FriendshipStatus {
	PENDING("Pendiente"),
	ACCEPTED("Aceptada"),
	REJECTED("Rechazada");
	
	private final String displayName;
	FriendshipStatus(String displayName) {
		this.displayName = displayName;
	}
	public String getName() {
	        return displayName;
	}
}
