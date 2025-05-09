package com.app.web.enums;

public enum RoleList {
	ROLE_ADMIN("Administrador"),
    ROLE_USER("Usuario normal");
    
    private final String displayName;
    
    RoleList(String displayName) {
        this.displayName = displayName;
    }
    
    public String getName() {
        return displayName;
    }
}
