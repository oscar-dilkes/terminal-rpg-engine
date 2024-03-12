package org.tre.engine;

import java.util.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;
import org.tre.objects.GameArtefact;
import org.tre.objects.GameCharacter;
import org.tre.objects.GameFurniture;
import org.tre.objects.GameLocation;

public class EntityParser {

    public EntityParser() {}

    public Set<GameLocation> parseEntitiesFile(File entitiesFile) throws FileNotFoundException, ParseException {
        // Create GraphViz parser
        Parser parser = new Parser();

        // Parse file
        FileReader reader = new FileReader(entitiesFile);
        parser.parse(reader);

        // Begin parsing
        Graph wholeDocument = parser.getGraphs().get(0);
        List<Graph> sections = wholeDocument.getSubgraphs();

        // Get locations
        Map<String, GameLocation> locationMap = parseLocations(sections.get(0).getSubgraphs());

        // Iterate through all paths in this graph
        ArrayList<Edge> paths = sections.get(1).getEdges();

        for (Edge pathEdge : paths) {
            // Extract nodes
            Node fromLocationNode = pathEdge.getSource().getNode();
            Node toLocationNode = pathEdge.getTarget().getNode();

            String fromName = fromLocationNode.getId().getId();
            String toName = toLocationNode.getId().getId();

            // Extract locations
            if (!locationMap.containsKey(fromName)) {
                throw new ParseException("Invalid location supplied in path");
            }
            GameLocation fromLocation = locationMap.get(fromName);

            if (!locationMap.containsKey(toName)) {
                throw new ParseException("Invalid location supplied in path");
            }
            GameLocation toLocation = locationMap.get(toName);

            fromLocation.addAdjacentLocation(toLocation);

        }

        return new HashSet<>(locationMap.values());
    }

    private Map<String, GameLocation> parseLocations(List<Graph> locations) throws ParseException {
        Map<String, GameLocation> locationMap = new HashMap<>();

        // Begin the location parsing
        for (Graph location : locations) {
            Node locationDetails = location.getNodes(false).get(0);
            String locationName = locationDetails.getId().getId();
            String locationDescription = locationDetails.getAttribute("description");

            List<Graph> entityTypes = location.getSubgraphs();
            Set<GameCharacter> gameCharacterList = new HashSet<>();
            Set<GameArtefact> gameArtefactList = new HashSet<>();
            Set<GameFurniture> gameFurnitureList = new HashSet<>();

            for (Graph entityType : entityTypes) {
                ArrayList<Node> entities = entityType.getNodes(false);

                switch (entityType.getId().getId()) {
                    case "characters" -> {
                        for (Node entity : entities) {
                            GameCharacter gameCharacter = new GameCharacter(entity.getId().getId(), entity.getAttribute("description"));
                            gameCharacterList.add(gameCharacter);
                        }
                    }
                    case "artefacts" -> {
                        for (Node entity : entities) {
                            GameArtefact gameArtefact = new GameArtefact(entity.getId().getId(), entity.getAttribute("description"));
                            gameArtefactList.add(gameArtefact);
                        }
                    }
                    case "furniture" -> {
                        for (Node entity : entities) {
                            GameFurniture gameFurniture = new GameFurniture(entity.getId().getId(), entity.getAttribute("description"));
                            gameFurnitureList.add(gameFurniture);
                        }
                    }
                    default -> throw new ParseException("Invalid entity type encountered during parsing.");
                }
            }
            GameLocation thisLocation = new GameLocation(locationName, locationDescription, gameCharacterList, gameArtefactList, gameFurnitureList);
            if (location.equals(locations.get(0))) {
                thisLocation.setStartingLocation(true);
            }
            locationMap.put(locationName, thisLocation);
        }

        return locationMap;
    }

}