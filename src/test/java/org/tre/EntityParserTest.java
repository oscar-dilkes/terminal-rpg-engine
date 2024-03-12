package org.tre;

import com.alexmerz.graphviz.ParseException;
import org.junit.jupiter.api.Test;
import org.tre.engine.EntityParser;
import org.tre.objects.GameLocation;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EntityParserTest {

    @Test
    void testParseEntitiesFile() {
        File extendedEntitiesFile = Paths.get("config" + File.separator + "example-entities.dot").toAbsolutePath().toFile();
        EntityParser parser = new EntityParser();
        try {
            Set<GameLocation> locationList = parser.parseEntitiesFile(extendedEntitiesFile);
            assertEquals(6, locationList.size(), "Expected six locations to be parsed");
            assertTrue(locationList.stream().anyMatch(location -> location.getName().equals("cabin")
                    && location.getDescription().equals("A log cabin in the woods")));
            assertTrue(locationList.stream().anyMatch(location -> location.getName().equals("forest")));
            assertTrue(locationList.stream().anyMatch(location -> location.getName().equals("cellar")));
            assertTrue(locationList.stream().anyMatch(location -> location.getName().equals("riverbank")));
            assertTrue(locationList.stream().anyMatch(location -> location.getName().equals("clearing")
                    && location.getDescription().equals("A clearing in the woods")));
            assertTrue(locationList.stream().anyMatch(location -> location.getName().equals("storeroom")));

            for (GameLocation location : locationList) {
                switch (location.getName()) {
                    case "cabin" -> {
                        assertTrue(location.getPresentArtefacts().stream().anyMatch(artefact -> artefact.getName().equals("potion")
                        && artefact.getDescription().equals("A bottle of magic potion")));
                        assertTrue(location.getPresentArtefacts().stream().anyMatch(artefact -> artefact.getName().equals("axe")));
                        assertTrue(location.getPresentArtefacts().stream().anyMatch(artefact -> artefact.getName().equals("coin")));
                        assertTrue(location.getPresentFurniture().stream().anyMatch(furniture -> furniture.getName().equals("trapdoor")));
                    }
                    case "forest" -> {
                        assertTrue(location.getPresentArtefacts().stream().anyMatch(artefact -> artefact.getName().equals("key")
                                && artefact.getDescription().equals("A rusty old key")));
                        assertTrue(location.getPresentFurniture().stream().anyMatch(furniture -> furniture.getName().equals("tree")
                                && furniture.getDescription().equals("A tall pine tree")));
                    }
                    case "cellar" ->
                            assertTrue(location.getPresentCharacters().stream().anyMatch(character -> character.getName().equals("elf")
                                    && character.getDescription().equals("An angry looking Elf")));
                    case "riverbank" -> {
                        assertTrue(location.getPresentArtefacts().stream().anyMatch(artefact -> artefact.getName().equals("horn")));
                        assertTrue(location.getPresentFurniture().stream().anyMatch(furniture -> furniture.getName().equals("river")));
                    }
                    case "clearing" ->
                            assertTrue(location.getPresentFurniture().stream().anyMatch(furniture -> furniture.getName().equals("ground")));
                    case "storeroom" -> {
                        assertTrue(location.getPresentCharacters().stream().anyMatch(character -> character.getName().equals("lumberjack")));
                        assertTrue(location.getPresentArtefacts().stream().anyMatch(artefact -> artefact.getName().equals("log")));
                        assertTrue(location.getPresentArtefacts().stream().anyMatch(artefact -> artefact.getName().equals("shovel")));
                        assertTrue(location.getPresentArtefacts().stream().anyMatch(artefact -> artefact.getName().equals("gold")));
                        assertTrue(location.getPresentFurniture().stream().anyMatch(furniture -> furniture.getName().equals("hole")));
                    }
                    default -> fail("Parsed an incorrect location name");
                }
            }
        } catch (FileNotFoundException | ParseException e) {
            System.out.println(e.getMessage());
        }
    }

    }