package com.app.web.enums;

public enum NotificationType {
	FRIEND_REQUEST("Solicitud de amistad"),
	FRIEND_REQUEST_ACCEPTED("Solicitud de amistad aceptada"),
	FRIEND_REQUEST_DECLINED("Solicitud de amistad rechazada"),
	MESSAGE("Mensaje"),
	COMMENT("Comentario"),
	GROUP_INVITATION("Invitación a grupo"),
	OTHER("Otro");

	private final String displayName;

	NotificationType(String displayName) {
		this.displayName = displayName;
	}

	public String getName() {
		return displayName;
	}
}
