package org.tre.objects;

import java.util.*;

public class GameLocation extends GameEntity{

    public GameLocation(String name, String description, Set<GameCharacter> presentCharacters, Set<GameArtefact> presentArtefacts, Set<GameFurniture> presentFurniture) {
        super(name, description);
        this.presentCharacters = presentCharacters;
        this.presentArtefacts = presentArtefacts;
        this.presentFurniture = presentFurniture;
    }

    private final Set<GameLocation> adjacentLocations = new HashSet<>();
    private final Set<GameCharacter> presentCharacters;
    private final Set<GamePlayer> presentPlayers = new HashSet<>();
    private final Set<GameArtefact> presentArtefacts;
    private final Set<GameFurniture> presentFurniture;
    private boolean isStartingLocation = false;


    public boolean isStartingLocation() {
        return isStartingLocation;
    }

    public void setStartingLocation(boolean startingLocation) {
        isStartingLocation = startingLocation;
    }

    public Set<GameLocation> getAdjacentLocations() {
        return adjacentLocations;
    }

    public void addAdjacentLocation(GameLocation location) {
        adjacentLocations.add(location);
    }

    public void removeAdjacentLocation(GameLocation location) {
        adjacentLocations.remove(location);
    }

    public Set<GameCharacter> getPresentCharacters() {
        return presentCharacters;
    }

    public void addCharacter (GameCharacter character) {
        presentCharacters.add(character);
    }

    public void removeCharacter (GameCharacter character) { presentCharacters.remove(character); }

    public Set<GamePlayer> getPresentPlayers() {
        return presentPlayers;
    }

    public void addPlayer (GamePlayer player) {
        presentPlayers.add(player);
    }

    public void removePlayer (GamePlayer player) { presentPlayers.remove(player); }

    public Set<GameArtefact> getPresentArtefacts() {
        return presentArtefacts;
    }

    public void addArtefact (GameArtefact artefact) {
        presentArtefacts.add(artefact);
    }

    public void removeArtefact (GameArtefact artefact) { presentArtefacts.remove(artefact); }

    public Set<GameFurniture> getPresentFurniture() {
        return presentFurniture;
    }

    public void addFurniture (GameFurniture furniture) {
        presentFurniture.add(furniture);
    }

    public void removeFurniture (GameFurniture furniture) { presentFurniture.remove(furniture); }
}
