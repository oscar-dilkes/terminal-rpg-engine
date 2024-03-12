package org.tre.objects;

import java.util.HashSet;

public class GamePlayer extends GameCharacter {
    private GameLocation currentLocation;
    private final HashSet<GameArtefact> inventory;
    private int health;
    public GamePlayer(String name, String description) {
        super(name, description);
        inventory = new HashSet<>();
        health = 3;
    }

    public GameLocation getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(GameLocation currentLocation) {
        this.currentLocation = currentLocation;
    }

    public HashSet<GameArtefact> getInventory() {
        return inventory;
    }

    public void addToInventory(GameArtefact artefact) {
        inventory.add(artefact);
    }

    public void removeFromInventory(GameArtefact artefact) {
        inventory.remove(artefact);
    }

    public int getHealth() {
        return health;
    }

    public void addHealth() {
        health++;
    }

    public void removeHealth() { health--; }

    public void fullHealth() { health = 3; }
}
