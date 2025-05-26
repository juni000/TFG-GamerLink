package com.app.web.entities;

// Clase para el resultado de búsqueda/creación
public class SquadSearchResult {
    private final SquadSearch squad;
    private final boolean joinedExisting;

    public SquadSearchResult(SquadSearch squad, boolean joinedExisting) {
        this.squad = squad;
        this.joinedExisting = joinedExisting;
    }

    // Getters
    public SquadSearch getSquad() { return squad; }
    public boolean isJoinedExisting() { return joinedExisting; }
}
