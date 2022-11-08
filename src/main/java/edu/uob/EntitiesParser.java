package edu.uob;
import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;
import edu.uob.GameEntities.Entities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class EntitiesParser {
    private com.alexmerz.graphviz.objects.Graph wholeDocument;
    public EntitiesParser(){
        Parser parser = new Parser();
        String path = "config" + File.separator + "entities.dot";
        try {
            FileReader reader = new FileReader(path);
            parser.parse(reader);
            this.wholeDocument = parser.getGraphs().get(0);
        } catch (FileNotFoundException | ParseException e) {
            throw new RuntimeException(e);
        }




    }

    public ArrayList<Graph> getParts(){
        return this.wholeDocument.getSubgraphs();
    }
    public ArrayList<Graph> getLocations() {
        return getParts().get(0).getSubgraphs();
    }
    public ArrayList<Edge> getPaths(){
        return getParts().get(1).getEdges();
    }

    public ArrayList<String> getAdjacent(String currentNode){
        ArrayList<String> adjacent = new ArrayList<>();
        ArrayList<Edge> paths = getPaths();
        for (int i =0; i < paths.size(); i++){
            if(paths.get(i).getSource().getNode().getId().getId().equals(currentNode)){
                adjacent.add(paths.get(i).getTarget().getNode().getId().getId());
            }
        }

        return adjacent;
    }

    public String nodeDescription(String nodeName){
        String description = "";
        ArrayList<Graph> locations =  getLocations();
        for(int i=0; i < locations.size(); i++){
            if(locations.get(i).getNodes(false).get(0).getId().getId().equals(nodeName)){
                description = locations.get(i).getNodes(false).get(0).getAttribute("description");
            }
        }
        return description;
    }

    public HashMap<String, ArrayList<Node>> initialiseArtefactHashMap(){
        HashMap<String, ArrayList<Node>> artifactHashMap = new HashMap<>();
        ArrayList<Graph> locations = getLocations();
        String key = "";
        int artefactLocation = 0;
        for(int i = 0; i < locations.size(); i++){
            key = locations.get(i).getNodes(false).get(0).getId().getId();
            artifactHashMap.put(locations.get(i).getNodes(false).get(0).getId().getId(), new ArrayList<>());
            artefactLocation = 0;
            for(int k = 0; k < locations.get(i).getSubgraphs().size(); k++){
                if(locations.get(i).getSubgraphs().get(k).getId().getId().equals("artefacts")){
                    artefactLocation = k + 1;
                }
            }
            if(artefactLocation>0){
                for(int j = 0; j < locations.get(i).getSubgraphs().get(artefactLocation - 1).getNodes(false).size(); j++){
                    artifactHashMap.get(key).add(locations.get(i).getSubgraphs().get(artefactLocation - 1).getNodes(false).get(j));

                }
            }

        }

        return artifactHashMap;
    }

    public HashMap<String, ArrayList<Node>> initialiseFurnitureHashMap(){
        HashMap<String, ArrayList<Node>> furnitureHashMap = new HashMap<>();
        ArrayList<Graph> locations = getLocations();
        String key = "";
        int furnitureLocation = 0;
        for(int i = 0; i < locations.size(); i++){
            key = locations.get(i).getNodes(false).get(0).getId().getId();
            furnitureHashMap.put(locations.get(i).getNodes(false).get(0).getId().getId(), new ArrayList<>());
            furnitureLocation = 0;
            for(int k = 0; k < locations.get(i).getSubgraphs().size(); k++){
                if(locations.get(i).getSubgraphs().get(k).getId().getId().equals("furniture")){
                    furnitureLocation = k + 1;
                }
            }
            if(furnitureLocation>0){
                for(int j = 0; j < locations.get(i).getSubgraphs().get(furnitureLocation - 1).getNodes(false).size(); j++){
                    furnitureHashMap.get(key).add(locations.get(i).getSubgraphs().get(furnitureLocation - 1).getNodes(false).get(j));

                }
            }

        }
        return furnitureHashMap;
    }

    public HashMap<String, ArrayList<Node>> initialiseCharactersHashMap(){
        HashMap<String, ArrayList<Node>> charactersHashMap = new HashMap<>();
        ArrayList<Graph> locations = getLocations();
        String key = "";
        int charactersLocation = 0;
        for(int i = 0; i < locations.size(); i++){
            key = locations.get(i).getNodes(false).get(0).getId().getId();
            charactersHashMap.put(locations.get(i).getNodes(false).get(0).getId().getId(), new ArrayList<>());
            charactersLocation = 0;
            for(int k = 0; k < locations.get(i).getSubgraphs().size(); k++){
                if(locations.get(i).getSubgraphs().get(k).getId().getId().equals("characters")){
                    charactersLocation = k + 1;
                }
            }
            if(charactersLocation>0){
                for(int j = 0; j < locations.get(i).getSubgraphs().get(charactersLocation - 1).getNodes(false).size(); j++){
                    charactersHashMap.get(key).add(locations.get(i).getSubgraphs().get(charactersLocation - 1).getNodes(false).get(j));

                }
            }

        }
        return charactersHashMap;
    }

    public String listItems(ArrayList<String> items){
        if(items.size() == 0){
            return "none";
        }
        if(items.size() == 1){
            return items.get(0);
        }
        if (items.size() == 2){
            return items.get(0) + " and " + items.get(1);
        }
        else {
            String result = "";
            for(int i = 0; i < items.size(); i ++){
                if(items.size() - i == 0){
                    result = result + " and " + items.get(i);
                }
            }
            return result;
        }
    }

    public String listNodes(ArrayList<Node> items){

        if(items.size() == 0){
            return "none";
        }
        if(items.size() == 1){
            return items.get(0).getId().getId();
        }
        if (items.size() == 2){
            return items.get(0).getId().getId() + " and " + items.get(1).getId().getId();
        }
        else {
            String result = "";
            for(int i = 0; i < items.size(); i ++){
                if(items.size() - i - 1== 0){
                    result = result + " and " + items.get(i).getId().getId();
                }
                else if(items.size() - i - 1 == 1){
                    result = result + items.get(i).getId().getId();
                }
                else{
                    result = result + items.get(i).getId().getId() + ", ";
                }
            }
            return result;
        }
    }

    public Entities initialiseEntities(){
        ArrayList<Entities> entities = new ArrayList<>();
        Entities entity;
        HashMap<String, ArrayList<Node>> charactersHashMap = new HashMap<>();
        ArrayList<Graph> locations = getLocations();
        String key = "";
        String locationName;
        String locationDescription;
        ArrayList<Edge> paths = getPaths();
        Entities world = new Entities("world", "world", "world");


        for(int i = 0; i < locations.size(); i++){
            locationName = locations.get(i).getNodes(false).get(0).getId().getId();
            locationDescription = locations.get(i).getNodes(false).get(0).getAttribute("description");
            entity = new Entities(locationName, "location", locationDescription);

            addEntitiesToLocation(locations, i, entity, entities, "artefacts");
            addEntitiesToLocation(locations, i, entity, entities, "characters");
            addEntitiesToLocation(locations, i, entity, entities, "furniture");

            world.addHeldItems(entity);
        }
        for (int i = 0; i < world.getHeldItems().size(); i ++){
            for (int j = 0; j < paths.size(); j ++){
                if(paths.get(j).getSource().getNode().getId().getId().equals(world.getHeldItems().get(i).getName())){
                    for(int k = 0; k < world.getHeldItems().size(); k ++){
                        if(world.getHeldItems().get(k).getName().equals(paths.get(j).getTarget().getNode().getId().getId())){
                            world.getHeldItems().get(i).addAdjacent(world.getHeldItems().get(k));
                        }
                    }
                }
            }
        }


        return world;
    }
    public void addEntitiesToLocation(ArrayList<Graph> locations, int i, Entities entity, ArrayList<Entities> entities, String entityType){
        int charactersLocation = 0;
        String entityName;
        String entityDescription;
        for(int k = 0; k < locations.get(i).getSubgraphs().size(); k++){
            if(locations.get(i).getSubgraphs().get(k).getId().getId().equals(entityType)){
                charactersLocation = k + 1;
            }
        }
        if(charactersLocation>0){
            for(int j = 0; j < locations.get(i).getSubgraphs().get(charactersLocation - 1).getNodes(false).size(); j++){
                entityName = locations.get(i).getSubgraphs().get(charactersLocation - 1).getNodes(false).get(j).getId().getId();
                entityDescription = locations.get(i).getSubgraphs().get(charactersLocation - 1).getNodes(false).get(j).getAttribute("description");
                entity.addHeldItems(new Entities(entityName, entityType, entityDescription));
            }
        }

    }
}
