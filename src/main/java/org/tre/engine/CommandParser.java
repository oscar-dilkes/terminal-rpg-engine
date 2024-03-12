package org.tre.engine;

import org.tre.objects.GameArtefact;
import org.tre.objects.GameEntity;
import org.tre.objects.GameLocation;
import org.tre.objects.GamePlayer;
import org.tre.exceptions.*;

import java.util.*;
import java.util.function.Consumer;

public class CommandParser {
    public CommandParser() {
    }

    public String parseCommandString (String command, GameWorld gameWorld) {
        try {
            // Extract player information from command
            GamePlayer currentPlayer = parsePlayerName(command, gameWorld);

            // Remove player name from colon as could contain action triggers or subjects
            int colonIndex = command.indexOf(":");
            if (colonIndex != -1) {
                command = command.substring(colonIndex + 1).toLowerCase();
            }

            // Handle basic command parsing and execution
            // Allow basic commands to take precedence since they are easily reversible
            // If method returns null then no basic command was found in command so move on to finding an action
            String handleBasicCommandsResponse = handleBasicCommands(command, currentPlayer);
            if (handleBasicCommandsResponse != null) {
                return handleBasicCommandsResponse;
            }

            // Check that it is possible for the action to be performed
            GameAction verifiedAction = parseAction(command, gameWorld);

            // Execute command and obtain relevant narration
            String actionNarration = performAction(currentPlayer, verifiedAction, gameWorld);

            // Handle if action has reduced player health to 0
            if (currentPlayer.getHealth() == 0) {
                // Empty inventory into location
                // Copy of inventory made to avoid ConcurrentModificationException
                Set<GameArtefact> inventoryCopy = new HashSet<>(currentPlayer.getInventory());
                for (GameArtefact artefact : inventoryCopy) {
                    currentPlayer.removeFromInventory(artefact);
                    currentPlayer.getCurrentLocation().addArtefact(artefact);
                }
                currentPlayer.setCurrentLocation(gameWorld.getStartingLocation());
                currentPlayer.fullHealth();
                // Add additional narration regarding death on the line following the action narration
                return (actionNarration + System.lineSeparator() + "You died and lost all of your items, you have been returned to the start of the game.");
            }

            // Else return normal narration
            return actionNarration;
        }
        catch (PlayerNotFoundException | ActionTriggerNotFoundException | SubjectEntityNotFoundException |
               MultipleActionAvailableException | ExtraneousEntityException e) {
            return e.getMessage();
        }
    }

    private GamePlayer parsePlayerName(String command, GameWorld gameWorld) throws PlayerNotFoundException {
        GamePlayer currentPlayer;
        String playerName;

        if (!command.contains(":")) {
            throw new PlayerNotFoundException("Couldn't locate a player name in your command, please try again.");
        }

        // Split command by colon
        String[] commandArray = command.split(":");
        // Take player name as everything before the colon
        playerName = commandArray[0].trim();


        if (playerName.isBlank()) {
            throw new PlayerNotFoundException("Couldn't locate a player name in your command, please try again.");
        }

        // If the player doesn't already exist in the world, then create a new player with that name
        if (gameWorld.getPlayer(playerName) == null) {
            currentPlayer = new GamePlayer(playerName, "Player #" + (gameWorld.getPlayerSet().size() + 1));

            currentPlayer.setCurrentLocation(gameWorld.getStartingLocation());
            gameWorld.getStartingLocation().addPlayer(currentPlayer);

            gameWorld.addPlayer(currentPlayer);
        } else {
            currentPlayer = gameWorld.getPlayer(playerName);
        }
        return currentPlayer;
    }

    private String handleBasicCommands(String command, GamePlayer currentPlayer) {
        HashSet<GameArtefact> inventory = currentPlayer.getInventory();
        GameLocation currentLocation = currentPlayer.getCurrentLocation();
        if (command.contains("inventory") || command.contains("inv")) {
            return inventoryBasicCommand(inventory);
        }
        else if (command.contains("get")) {
            return getBasicCommand(command, currentPlayer, currentLocation);
        }
        else if (command.contains("drop")) {
            return dropBasicCommand(command, currentPlayer, currentLocation);
        }
        else if (command.contains("goto")) {
            return gotoBasicCommand(command, currentPlayer, currentLocation);
        }
        else if (command.contains("look")) {
            return lookBasicCommand(currentLocation);
        }
        else if (command.contains("health")) {
            return "Your current health is " + currentPlayer.getHealth() + ".";
        }
        return null;
    }

    private static String inventoryBasicCommand(HashSet<GameArtefact> inventory) {
        StringBuilder stb = new StringBuilder();

        if (inventory.isEmpty()) {
            return "Your inventory is currently empty, try picking an artefact up using 'get'.";
        }

        stb.append("Here is what's currently in your inventory: ");

        int i = 0;
        for (GameArtefact artefact : inventory) {
            stb.append(artefact.getName());
            if (i < inventory.size() - 1) {
                stb.append(", ");
            }
            i++;
        }
        return stb.toString();
    }

    // The methods to handle goto, drop, and get are very similar
    // Iterate through relevant set, check for a match within the command, perform relevant actions
    private static String gotoBasicCommand(String command, GamePlayer currentPlayer, GameLocation currentLocation) {
        for (GameLocation adjacentLocation : currentLocation.getAdjacentLocations()) {
            if (command.contains(adjacentLocation.getName())) {
                currentPlayer.setCurrentLocation(adjacentLocation);
                currentLocation.removePlayer(currentPlayer);
                adjacentLocation.addPlayer(currentPlayer);
                return "You have moved to the '" + adjacentLocation.getName() + "'.";
            }
        }
        return "You can't get there from here.";
    }

    private String dropBasicCommand(String command, GamePlayer currentPlayer, GameLocation currentLocation) {
        for (GameArtefact artefact : currentPlayer.getInventory()) {
            if (command.contains(artefact.getName())) {
                currentPlayer.removeFromInventory(artefact);
                currentLocation.addArtefact(artefact);
                return "You dropped '" + artefact.getName() + "'.";
            }
        }
        return "That isn't in your inventory.";
    }

    private String getBasicCommand(String command, GamePlayer currentPlayer, GameLocation currentLocation) {
        for (GameArtefact artefact : currentLocation.getPresentArtefacts()) {
            if (command.contains(artefact.getName())) {
                currentPlayer.addToInventory(artefact);
                currentLocation.removeArtefact(artefact);
                return "You picked up the '" + artefact.getName() + "' and placed it in your inventory.";
            }
        }
        return "That isn't at this location.";
    }

    private static String lookBasicCommand(GameLocation currentLocation) {
        StringBuilder stb = new StringBuilder();
        stb.append("You are currently in the ").append(currentLocation.getName()).append(", ").append(currentLocation.getDescription()).append(System.lineSeparator()).append(System.lineSeparator());
        stb.append("These are all the artefacts in your current location:");
        appendEntitiesToLookString(stb, currentLocation.getPresentArtefacts());

        stb.append("Here is all the furniture:");
        appendEntitiesToLookString(stb, currentLocation.getPresentFurniture());

        stb.append("These are all the characters:");
        appendEntitiesToLookString(stb, currentLocation.getPresentCharacters());

        stb.append("These are all the players:");
        appendEntitiesToLookString(stb, currentLocation.getPresentPlayers());

        stb.append("And here are all the locations you can reach from here:");
        appendEntitiesToLookString(stb, currentLocation.getAdjacentLocations());

        return stb.toString();
    }

    private static <T extends GameEntity> void appendEntitiesToLookString(StringBuilder stb, Set<T> entities) {
        for (T entity : entities) {
            stb.append(System.lineSeparator());
            stb.append(entity.getName()).append(": ").append(entity.getDescription());
        }
        stb.append(System.lineSeparator()).append(System.lineSeparator());
    }

    
    private GameAction parseAction (String command, GameWorld gameWorld) throws SubjectEntityNotFoundException, ActionTriggerNotFoundException, MultipleActionAvailableException, ExtraneousEntityException {
        HashMap<String, HashSet<GameAction>> actions = gameWorld.getPossibleActions();
        String trigger = identifyTriggerInCommand(command, actions);
        HashSet<GameAction> possibleActionsForThisTrigger = actions.get(trigger);
        return checkSubjects(command, gameWorld, trigger, possibleActionsForThisTrigger);
    }

    private String identifyTriggerInCommand (String command, HashMap<String, HashSet<GameAction>> triggerMap) throws ActionTriggerNotFoundException {
        for (String trigger : triggerMap.keySet()) {

            // Check for the trigger in the command string
            if (command.contains(trigger)) {
                return trigger;
            }
        }
        throw new ActionTriggerNotFoundException("Couldn't find any action words in your command, please try again.");
    }

    private GameAction checkSubjects(String command, GameWorld gameWorld, String trigger, HashSet<GameAction> actions) throws ExtraneousEntityException, SubjectEntityNotFoundException, MultipleActionAvailableException {
        GameAction currentAction = null;
        Set<String> presentSubjectEntities = identifySubjectEntitiesInCommand(command, gameWorld);
        // Integer for storing the number of possible actions with the trigger and subjects in command string
        int possibleActions = 0;
        for (GameAction action : actions) {
            Set<String> subjectSet = action.getSubjects();
            boolean validAction = false;
            for (String subject : subjectSet) {
                if (presentSubjectEntities.contains(subject)) {
                    validAction = true;
                    currentAction = action;
                    presentSubjectEntities.remove(subject);
                }
            }
            if (validAction) {
                possibleActions++;
            }
        }
        // Correct subject entities are removed from presentSubjectEntities if found
        // If any are left over, then it means the user has given an extra subject entity for a different action
        // Or that they've given the wrong subject entity
        if (presentSubjectEntities.size() > 0) {
            throw new ExtraneousEntityException("You tried to perform the action using the incorrect object, please try again.");
        }
        // possibleActions incremented if a matching action is found for the given trigger
        if (possibleActions == 0) {
            throw new SubjectEntityNotFoundException("Couldn't find any matching subject entities in your command, please try again.");
        } else if (possibleActions == 1) {
            return currentAction;
        } else {
            throw new MultipleActionAvailableException("There are multiple '" + trigger + "' actions available to you, which would you like to perform?");
        }
    }

    private HashSet<String> identifySubjectEntitiesInCommand (String command, GameWorld gameWorld) {
        HashSet<String> allSubjectEntities = new HashSet<>();
        HashMap<String, HashSet<GameAction>> actions = gameWorld.getPossibleActions();

        // Collect all the possible subject entities for every action, so they can be identified in the command String
        // This is to deal with extraneous entities from other actions in a command
        actions.values().forEach(possibleActionsForThisTrigger -> {
            for (GameAction action : possibleActionsForThisTrigger) {
                allSubjectEntities.addAll(action.getSubjects());
            }
        });

        HashSet<String> presentSubjectEntities = new HashSet<>();
        String[] commandArray = command.split(" ");
        for (String commandElement : commandArray) {
            if (allSubjectEntities.contains(commandElement)) {
                presentSubjectEntities.add(commandElement);
            }
        }
        return presentSubjectEntities;
    }

    private String performAction (GamePlayer currentPlayer, GameAction gameAction, GameWorld gameWorld) {
        GameLocation currentLocation = currentPlayer.getCurrentLocation();
        GameLocation storeRoom = gameWorld.getLocation("storeroom");
        int numberOfSubjectEntities = gameAction.getSubjects().size();
        int subjectsPresent = 0;

        // Count the number of each entity needed for the current action that are present in the current location
        for (String subject : gameAction.getSubjects()) {
            subjectsPresent += currentLocation.getPresentArtefacts()

                    // Create a stream of entities
                    .stream()

                    // Filter to keep only entities with name the same as the current subject
                    .filter(a -> a.getName().equals(subject))

                    // Count the number of entities that make it through the filter
                    .count();
                    // This is added to subjectsPresent

            subjectsPresent += currentLocation.getPresentCharacters()
                    .stream()
                    .filter(c -> c.getName().equals(subject))
                    .count();
            subjectsPresent += currentLocation.getPresentFurniture()
                    .stream()
                    .filter(f -> f.getName().equals(subject))
                    .count();
            subjectsPresent += currentPlayer.getInventory()
                    .stream()
                    .filter(a -> a.getName().equals(subject))
                    .count();
        }

        if (subjectsPresent != numberOfSubjectEntities) {
            return "You cannot perform this action as you do not have everything that you need available to you at this time.";
        }

        consumeEntities(currentPlayer, gameAction, currentLocation, storeRoom);

        produceEntities(gameAction, gameWorld, currentLocation, storeRoom, currentPlayer);

        return gameAction.getNarration();
    }

    private static void consumeEntities(GamePlayer gamePlayer, GameAction gameAction, GameLocation currentLocation, GameLocation storeRoom) {
        gameAction.getConsumed().forEach(consumed -> {
            handleEntity(gamePlayer.getInventory(), consumed, storeRoom::addArtefact, gamePlayer::removeFromInventory);
            handleEntity(currentLocation.getPresentCharacters(), consumed, storeRoom::addCharacter, currentLocation::removeCharacter);
            handleEntity(currentLocation.getPresentFurniture(), consumed, storeRoom::addFurniture, currentLocation::removeFurniture);
            handleEntity(currentLocation.getAdjacentLocations(), consumed, currentLocation::removeAdjacentLocation);
            if (consumed.equals("health")) {
                gamePlayer.removeHealth();
            }
        });
    }


    private static void produceEntities(GameAction gameAction, GameWorld gameWorld, GameLocation currentLocation, GameLocation storeRoom, GamePlayer currentPlayer) {
        gameAction.getProduced().forEach(produced -> {
            handleEntity(storeRoom.getPresentArtefacts(), produced, currentLocation::addArtefact, storeRoom::removeArtefact);
            handleEntity(storeRoom.getPresentCharacters(), produced, currentLocation::addCharacter, storeRoom::removeCharacter);
            handleEntity(storeRoom.getPresentFurniture(), produced, currentLocation::addFurniture, storeRoom::removeFurniture);
            handleEntity(gameWorld.getGameLocations(), produced, currentLocation::addAdjacentLocation);
            if (produced.equals("health")) {
                if (currentPlayer.getHealth() < 3) {
                    currentPlayer.addHealth();
                }
            }
        });
    }

    private static <T extends GameEntity> void handleEntity(Set<T> entities, String consumedOrProduced, Consumer<T> addEntity, Consumer<T> removeEntity) {
        entities.stream()

                // Filter inventory to get artefact for consumption
                .filter(entity -> consumedOrProduced.equals(entity.getName()))

                // Find the first matching artefact
                .findFirst()

                // If a match is found, act accordingly for whichever entity type
                .ifPresent(entity -> {
                    removeEntity.accept(entity);
                    addEntity.accept(entity);
                });
    }

    // Same but just for location as only one action needs to be taken
    private static <T extends GameEntity> void handleEntity(Set<T> entities, String consumed, Consumer<T> addOrRemoveLocation) {
        entities.stream()
                .filter(entity -> consumed.equals(entity.getName()))
                .findFirst()
                .ifPresent(addOrRemoveLocation);
    }

}
