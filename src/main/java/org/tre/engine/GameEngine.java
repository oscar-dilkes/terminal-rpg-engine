package org.tre.engine;

import com.alexmerz.graphviz.ParseException;
import org.tre.objects.GameLocation;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class GameEngine {

  GameWorld gameWorld;

  public static void main(String[] args) throws IOException {
    BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
    GameEngine engine = new GameEngine(consoleReader, false);

    while (true) {
      System.out.print("Enter command: ");
      String command = consoleReader.readLine();
      if (command.equalsIgnoreCase("exit")) {
        System.out.println("Thank you for playing!");
        return;
      }
      String response = engine.handleCommand(command);
      System.out.println(response);
    }
  }

  public static List<File> loadFiles(BufferedReader consoleReader, boolean isTest) throws IOException {
    boolean successfullyLoaded = false;
    File entitiesFile = null;
    File actionsFile = null;
    while (!successfullyLoaded) {
      String entitiesFilePath = "example-entities.dot";
      String actionsFilePath = "example-actions.xml";

      if (!isTest) {
        // Read commands from the terminal
        System.out.println("Enter 'Y' if you would like to use the example files: ");

        if (!consoleReader.readLine().equalsIgnoreCase("y")) {
          System.out.print("Enter the name of the desired entities file: ");
          entitiesFilePath = consoleReader.readLine();
          System.out.print("Enter the name of the desired actions file: ");
          actionsFilePath = consoleReader.readLine();
        }
      }

      try {
        Path entitiesPath = Paths.get("config" + File.separator + entitiesFilePath).toAbsolutePath();
        Path actionsPath = Paths.get("config" + File.separator + actionsFilePath).toAbsolutePath();

        if (entitiesPath.toFile().exists() && actionsPath.toFile().exists()) {
          entitiesFile = entitiesPath.toFile();
          actionsFile = actionsPath.toFile();
          successfullyLoaded = true;
        } else {
          System.err.println("File not found. Please enter valid file names.");
        }
      } catch (Exception e) {
        System.err.println("Error loading files: " + e.getMessage());
      }
    }
    return(List.of(entitiesFile, actionsFile));
  }

  public GameEngine(BufferedReader consoleReader, boolean isTest) throws IOException {
    List<File> fileList = loadFiles(consoleReader, isTest);

    HashMap<String, HashSet<GameAction>> possibleActions;
    Set<GameLocation> gameLocations;

    ActionParser actionParser = new ActionParser();
    possibleActions = actionParser.parseActionsFile(fileList.get(1));

    try {
      EntityParser entityParser = new EntityParser();
      gameLocations = entityParser.parseEntitiesFile(fileList.get(0));

      gameWorld = new GameWorld(possibleActions, gameLocations);
    }
    catch (ParseException | FileNotFoundException e) {
      System.out.println(e.getMessage());
    }
  }


  public String handleCommand(String command) {
    CommandParser commandParser = new CommandParser();
    return commandParser.parseCommandString(command, this.gameWorld);
  }
}
