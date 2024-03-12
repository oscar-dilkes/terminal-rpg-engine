package org.tre;

import org.junit.jupiter.api.Test;
import org.tre.engine.ActionParser;
import org.tre.engine.GameAction;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class ActionParserTest {

    @Test
    void testParseActionsFile() {
        File extendedActionsFile = Paths.get("config" + File.separator + "example-actions.xml").toAbsolutePath().toFile();
        ActionParser parser = new ActionParser();
        HashMap<String, HashSet<GameAction>> actions = parser.parseActionsFile(extendedActionsFile);

        assertTrue(actions.containsKey("open"), "Trigger open not present in actions map");
        assertTrue(actions.containsKey("unlock"), "Trigger unlock not present in actions map");
        assertTrue(actions.containsKey("chop"), "Trigger chop not present in actions map");
        assertTrue(actions.containsKey("attack"), "Trigger attack not present in actions map");

        // Check the details of the parsed actions for the "open" trigger
        HashSet<GameAction> openActions = actions.get("open");
        assertEquals(1, openActions.size(), "Expected one action for trigger open, but got " + openActions.size());
        GameAction openAction = openActions.iterator().next();
        assertTrue(openAction.getSubjects().contains("trapdoor"), "Trapdoor not found in subjects");
        assertTrue(openAction.getSubjects().contains("key"), "Key not found in subjects");
        assertTrue(openAction.getConsumed().contains("key"), "Key not found in consumed");
        assertTrue(openAction.getProduced().contains("cellar"), "Entity not found in produced");
        assertEquals("You unlock the door and see steps leading down into a cellar", openAction.getNarration(), "Narration incorrect for open action");

    }
}