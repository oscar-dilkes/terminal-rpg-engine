package org.tre.engine;

import org.tre.objects.GameLocation;
import org.tre.objects.GamePlayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GameWorld {
    private final HashSet<GamePlayer> playerSet;
    private final HashMap<String, HashSet<GameAction>> possibleActions;
    private final Set<GameLocation> gameLocations;
    private GameLocation startingLocation;

    public GameWorld(HashMap<String, HashSet<GameAction>> possibleActions, Set<GameLocation> gameLocations) {
        this.possibleActions = possibleActions;
        this.gameLocations = gameLocations;
        playerSet = new HashSet<>();
        for (GameLocation location : gameLocations) {
            if (location.isStartingLocation()) {
                startingLocation = location;
            }
        }
    }

    public HashSet<GamePlayer> getPlayerSet() {
        return playerSet;
    }

    public GamePlayer getPlayer(String playerName) {
        for (GamePlayer player : playerSet) {
            if (player.getName().equals((playerName))) {
                return player;
            }
        }
        return null;
    }

    public void addPlayer(GamePlayer player) {
        playerSet.add(player);
    }

    public HashMap<String, HashSet<GameAction>> getPossibleActions() {
        return possibleActions;
    }

    public Set<GameLocation> getGameLocations() {
        return gameLocations;
    }

    public GameLocation getLocation(String locationName) {
        for (GameLocation location : gameLocations) {
            if (location.getName().equals(locationName)) {
                return location;
            }
        }
        return null;
    }

    public GameLocation getStartingLocation() {
        return startingLocation;
    }

}
