package org.tre.engine;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ActionParser {
    public ActionParser() {
    }

    public HashMap<String, HashSet<GameAction>> parseActionsFile(File actionsFile){
        try {
            // Create new DocumentBuilder to parse XML file
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            // Parse XML file and create new Document object
            Document document = builder.parse(actionsFile);

            // Get NodeList of action elements in XML file
            NodeList actionNodes = document.getElementsByTagName("action");

            // Create new HashMap and HashSet to store Action objects
            HashMap<String, HashSet<GameAction>> actions = new HashMap<>();


            // Loop through NodeList of action elements
            for (int i = 0; i < actionNodes.getLength(); i++) {

                // Get current action element
                Element actionElement = (Element) actionNodes.item(i);

                // Extract triggers, subjects, consumed, produced, and narration elements from action element
                List<String> triggers = new ArrayList<>(getElementsFromTag(actionElement, "triggers", "keyphrase"));
                Set<String> subjects = getElementsFromTag(actionElement, "subjects", "entity");
                Set<String> consumed = getElementsFromTag(actionElement, "consumed", "entity");
                Set<String> produced = getElementsFromTag(actionElement, "produced", "entity");
                String narration = actionElement.getElementsByTagName("narration").item(0).getTextContent();

                GameAction action = new GameAction(subjects, consumed, produced, narration);
                for (String trigger : triggers) {
                    if (actions.containsKey(trigger)) {
                        actions.get(trigger).add(action);
                    } else {
                        HashSet<GameAction> possibleActionsFromTrigger = new HashSet<>();
                        possibleActionsFromTrigger.add(action);
                        actions.put(trigger, possibleActionsFromTrigger);
                    }
                }
            }

            return actions;

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            System.out.println ("ParserConfigurationException was thrown when attempting to read actions file.");
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            System.out.println ("SAXException was thrown when attempting to read actions file.");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException was thrown when attempting to read actions file.");
            return null;
        }
    }

    // Method to extract elements from a given tag in a given parent element
    private Set<String> getElementsFromTag(Element parentElement, String tagName, String childTagName) {

        // Get NodeList of elements with given tag name
        NodeList nodes = parentElement.getElementsByTagName(tagName);

        // Create new HashSet to store extracted elements
        Set<String> elements = new HashSet<>();

        // Loop through NodeList
        for (int j = 0; j < nodes.getLength(); j++) {

            // Get current element and child elements with given tag name
            Element element = (Element) nodes.item(j);
            NodeList childNodes = element.getElementsByTagName(childTagName);

            for (int k = 0; k < childNodes.getLength(); k++) {

                // Get text content of current child element
                String item = childNodes.item(k).getTextContent();
                elements.add(item);
            }
        }
        return elements;
    }

}
