package org.tre;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tre.engine.GameEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.*;

class CommandParserTest {
    private GameEngine gameEngine;

    @BeforeEach
    void setup() throws IOException {
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        gameEngine = new GameEngine(consoleReader, true);
    }


    String sendCommandToEngine(String command) {
        System.out.println(command);
        return gameEngine.handleCommand(command);
    }

    @Test
    void testPlayerName() {
        String response = sendCommandToEngine("wizard: look");
        response = response.toLowerCase();
        assertTrue(response.contains("wizard"), "Player wizard not shown in current location");

        response = sendCommandToEngine("    dog      man     : look");
        response = response.toLowerCase();
        assertTrue(response.contains("dog      man"), "Player dog      man not shown in current location");

        // Handle adding multiple players
        response = sendCommandToEngine("    cat   elf     : look");
        response = response.toLowerCase();
        assertTrue(response.contains("wizard") && response.contains("dog      man") && response.contains("cat   elf"), "All three players not shown in current location");
    }

    @Test
    void testEmptyInventory() {
        String response;

        response = sendCommandToEngine("wizard: inv");
        response = response.toLowerCase();
        assertEquals("your inventory is currently empty, try picking an artefact up using 'get'.", response, "Didn't return empty inventory string");

        response = sendCommandToEngine("wizard: inventory");
        response = response.toLowerCase();
        assertEquals("your inventory is currently empty, try picking an artefact up using 'get'.", response, "Didn't return empty inventory string");
    }

    @Test
    void testGoto() {
        String response;

        sendCommandToEngine("wizard: goto forest");

        response = sendCommandToEngine("wizard: look");
        response = response.toLowerCase();
        assertTrue(response.contains("forest"), "Look details haven't updated to reflect a movement to forest location");
    }

    @Test
    void testGet() {
        String response;

        sendCommandToEngine("wizard: get coin");

        response = sendCommandToEngine("wizard: inv");
        response = response.toLowerCase();
        assertTrue(response.contains("coin"), "Couldn't see the coin in inventory after an attempt to pick it up");

        response = sendCommandToEngine("wizard: look");
        response = response.toLowerCase();
        assertFalse(response.contains("coin"), "Coin is still present in this location after an attempt to pick it up");
    }

    @Test
    void testDrop() {
        String response;

        sendCommandToEngine("wizard: get coin");

        response = sendCommandToEngine("wizard: inv");
        response = response.toLowerCase();
        assertTrue(response.contains("coin"), "Couldn't see the coin in inventory after an attempt to pick it up");

        sendCommandToEngine("wizard: drop coin");

        response = sendCommandToEngine("wizard: inv");
        response = response.toLowerCase();
        assertFalse(response.contains("coin"), "Coin is still present in the inventory after an attempt to drop it");

        response = sendCommandToEngine("wizard: look");
        response = response.toLowerCase();
        assertTrue(response.contains("coin"), "Couldn't see the coin at this location after an attempt to drop it");
    }

    @Test
    void testConsumeArtefactAndProduceLocation() {
        String response;

        sendCommandToEngine("wizard: goto forest");
        sendCommandToEngine("wizard: get key");

        response = sendCommandToEngine("wizard: inv");
        response = response.toLowerCase();
        assertTrue(response.contains("key"), "Couldn't see the key in inventory after an attempt to pick it up");

        sendCommandToEngine("wizard: goto cabin");

        response = sendCommandToEngine("wizard: look");
        response = response.toLowerCase();
        assertFalse(response.contains("cellar"), "The cellar location is available before it should be");

        sendCommandToEngine("wizard: open trapdoor");

        response = sendCommandToEngine("wizard: inv");
        response = response.toLowerCase();
        assertFalse(response.contains("key"), "Key hasn't been consumed and is still in inventory");

        response = sendCommandToEngine("wizard: look");
        response = response.toLowerCase();
        assertTrue(response.contains("cellar"), "The cellar location hasn't become available");
    }

    @Test
    void testConsumeFurniture() {
        String response;

        sendCommandToEngine("wizard: get axe");
        sendCommandToEngine("wizard: goto forest");

        response = sendCommandToEngine("wizard: look");
        response = response.toLowerCase();
        assertTrue(response.contains("tree"), "Couldn't see the tree in the current location");

        sendCommandToEngine("wizard: cut down tree");

        response = sendCommandToEngine("wizard: look");
        response = response.toLowerCase();
        assertFalse(response.contains("tree"), "The tree is still in the current location");
    }

    @Test
    void testConsumeHealth() {
        String response;

        sendCommandToEngine("wizard: goto forest");
        sendCommandToEngine("wizard: get key");
        sendCommandToEngine("wizard: goto cabin");
        sendCommandToEngine("wizard: open trapdoor");
        sendCommandToEngine("wizard: goto cellar");

        response = sendCommandToEngine("wizard: health");
        response = response.toLowerCase();
        assertTrue(response.contains("3"), "Health should be full at 3");

        sendCommandToEngine("wizard: attack elf");

        response = sendCommandToEngine("wizard: health");
        response = response.toLowerCase();
        assertTrue(response.contains("2"), "Health should now be at 2");
    }

    @Test
    void testNoHealth() {
        String response;

        sendCommandToEngine("wizard: get potion");
        sendCommandToEngine("wizard: get axe");
        sendCommandToEngine("wizard: get coin");
        sendCommandToEngine("wizard: goto forest");
        sendCommandToEngine("wizard: get key");
        sendCommandToEngine("wizard: goto cabin");
        sendCommandToEngine("wizard: open trapdoor");
        sendCommandToEngine("wizard: goto cellar");
        sendCommandToEngine("wizard: attack elf");
        sendCommandToEngine("wizard: attack elf");

        response = sendCommandToEngine("wizard: inv");
        response = response.toLowerCase();
        assertTrue(response.contains("potion"), "Couldn't see the potion in inventory");
        assertTrue(response.contains("axe"), "Couldn't see the axe in inventory");
        assertTrue(response.contains("coin"), "Couldn't see the coin in inventory");

        sendCommandToEngine("wizard: attack elf");

        response = sendCommandToEngine("wizard: look");
        response = response.toLowerCase();
        assertTrue(response.contains("cabin"), "Player should respawn at cabin");

        response = sendCommandToEngine("wizard: inv");
        response = response.toLowerCase();
        assertFalse(response.contains("potion"), "Potion should no longer be in inventory");
        assertFalse(response.contains("axe"), "Axe should no longer be in inventory");
        assertFalse(response.contains("coin"), "Coin should no longer be in inventory");

        sendCommandToEngine("wizard: goto cellar");

        response = sendCommandToEngine("wizard: look");
        response = response.toLowerCase();
        assertTrue(response.contains("potion"), "Potion should've dropped into cellar");
        assertTrue(response.contains("axe"), "Axe should've dropped into cellar");
        assertTrue(response.contains("coin"), "Coin should've dropped into cellar");
    }

    @Test
    void testProduceArtefact() {
        String response;

        sendCommandToEngine("wizard: get coin");
        sendCommandToEngine("wizard: goto forest");
        sendCommandToEngine("wizard: get key");
        sendCommandToEngine("wizard: goto cabin");
        sendCommandToEngine("wizard: open trapdoor");
        sendCommandToEngine("wizard: goto cellar");

        response = sendCommandToEngine("wizard: look");
        response = response.toLowerCase();
        assertFalse(response.contains("shovel"), "There shouldn't be a shovel yet");

        sendCommandToEngine("wizard: pay elf");

        response = sendCommandToEngine("wizard: look");
        response = response.toLowerCase();
        assertTrue(response.contains("shovel"), "The elf should've produced a shovel");
    }

    @Test
    void testProduceCharacter() {
        String response;

        sendCommandToEngine("wizard: goto forest");
        sendCommandToEngine("wizard: goto riverbank");
        sendCommandToEngine("wizard: get horn");

        response = sendCommandToEngine("wizard: look");
        response = response.toLowerCase();
        assertFalse(response.contains("lumberjack"), "The lumberjack shouldn't be here yet");

        sendCommandToEngine("wizard: blow horn");

        response = sendCommandToEngine("wizard: look");
        response = response.toLowerCase();
        assertTrue(response.contains("lumberjack"), "The lumberjack should've appeared");
    }

    @Test
    void testProduceFurniture() {
        String response;

        sendCommandToEngine("wizard: get axe");
        sendCommandToEngine("wizard: goto forest");

        response = sendCommandToEngine("wizard: look");
        response = response.toLowerCase();
        // Split using unique word 'reach' from available locations so that 'log' from 'log cabin' isn't being picked up
        String[] responseArray = response.split("reach");
        assertFalse(responseArray[0].contains("log"), "There shouldn't be a log here yet");

        sendCommandToEngine("wizard: cut down tree");

        response = sendCommandToEngine("wizard: look");
        response = response.toLowerCase();
        responseArray = response.split("reach");
        assertTrue(responseArray[0].contains("log"), "The log should've appeared");
    }

    @Test
    void testProduceHealthWhenMaxHealth() {
        String response;

        sendCommandToEngine("wizard: get potion");

        response = sendCommandToEngine("wizard: health");
        response = response.toLowerCase();
        assertTrue(response.contains("3"), "Health should be at three");

        sendCommandToEngine("wizard: drink potion");

        response = sendCommandToEngine("wizard: health");
        response = response.toLowerCase();
        assertTrue(response.contains("3"), "Health should still be at three");
    }

    @Test
    void testProduceHealthWhenNotMaxHealth() {
        String response;

        sendCommandToEngine("wizard: get potion");
        sendCommandToEngine("wizard: goto forest");
        sendCommandToEngine("wizard: get key");
        sendCommandToEngine("wizard: goto cabin");
        sendCommandToEngine("wizard: open trapdoor");
        sendCommandToEngine("wizard: goto cellar");
        sendCommandToEngine("wizard: attack elf");

        response = sendCommandToEngine("wizard: health");
        response = response.toLowerCase();
        assertTrue(response.contains("2"), "Health should be at two");

        sendCommandToEngine("wizard: drink potion");

        response = sendCommandToEngine("wizard: health");
        response = response.toLowerCase();
        assertTrue(response.contains("3"), "Health should now be at three");
    }

    @Test
    void testActionTriggerNotFoundException() {
        String response = sendCommandToEngine("wizard: hello from my bedroom");
        response = response.toLowerCase();
        assertEquals("couldn't find any action words in your command, please try again.", response, "Matching response string from ActionTriggerNotFoundException is missing");
    }

    @Test
    void testExtraneousEntityException() {
        String response;
        sendCommandToEngine("wizard: get potion");
        sendCommandToEngine("wizard: get coin");

        response = sendCommandToEngine("wizard: drink coin");
        response = response.toLowerCase();
        assertEquals("you tried to perform the action using the incorrect object, please try again.", response, "Matching response string from ExtraneousEntityException is missing");

        response = sendCommandToEngine("wizard: drink potion and coin");
        response = response.toLowerCase();
        assertEquals("you tried to perform the action using the incorrect object, please try again.", response, "Matching response string from ExtraneousEntityException is missing");

    }

    @Test
    void testPlayerNotFoundException() {
        String response = sendCommandToEngine("hello from my bedroom");
        response = response.toLowerCase();
        assertEquals("couldn't locate a player name in your command, please try again.", response, "Matching response string from PlayerNotFoundException is missing");

        response = sendCommandToEngine(":hello from my bedroom");
        response = response.toLowerCase();
        assertEquals("couldn't locate a player name in your command, please try again.", response, "Matching response string from PlayerNotFoundException is missing");

        response = sendCommandToEngine("       :hello from my bedroom");
        response = response.toLowerCase();
        assertEquals("couldn't locate a player name in your command, please try again.", response, "Matching response string from PlayerNotFoundException is missing");
    }

    @Test
    void testSubjectEntityNotFoundException() {
        String response;

        sendCommandToEngine("wizard: get potion");

        response = sendCommandToEngine("wizard: drink");
        response = response.toLowerCase();
        assertEquals("couldn't find any matching subject entities in your command, please try again.", response, "Matching response string from SubjectEntityNotFoundException is missing");

    }

}