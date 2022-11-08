package edu.uob;

import edu.uob.GameEntities.Entities;
import edu.uob.GameEntities.Player;

import java.lang.reflect.Array;
import java.util.*;

import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;

public class GameState {
    //parser that reads and stores entities.dot file and has some accompanying useful functions
    private EntitiesParser parser;

    private Action actions;
    //a hashmap that links the player name to the player object (which also holds player location)
    private HashMap<String, Player> players = new HashMap<>();
    //starting location for any new game configurations
    private String startingLocation;

    private Entities world;

    //HashMap that uses the string name of locations as keys and responds with a list of artefacts at that location
    //it also should allow you to use a player name as a "location" that can host artefacts,furnitures,and characters
    //initialises game state
    public GameState(EntitiesParser parser) {
        this.parser = parser;
        this.actions = new ActionsReader().createAction();
        this.world = parser.initialiseEntities();
        this.startingLocation =  parser.getLocations().get(0).getNodes(false).get(0).getId().getId();
    }

    //adds new player to the player HashMap this should be done everytime a new player runs a command on the running server
    //should have the location be the default starting location
    public void addNewPlayer(String name) {
        players.put(name, new Player(name, startingLocation));
        world.getHeldItems().get(0).addHeldItems(new Entities(name, "players", "Player with the name, " + name));
        players.get(name).setMyInventory(world.findEntity(name).getHeldItems());
    }

    //checks if player exists
    public boolean playerExists(String playerName) {
        return this.players.containsKey(playerName);
    }

    //processes the goto command and returns the response
    public String goTo(String playerName, String location){
        String response = "";
        Entities targetLocation = world.findEntity(location);;
        Player plr = players.get(playerName);
        Entities plrEntity = world.findEntity(playerName);
        String playerLocation = plr.getLocation();
        Entities locationEntity = world.findEntity(playerLocation);

        if(locationEntity.getAdjacent().contains(targetLocation)){
            players.get(playerName).move(targetLocation.getName());
            locationEntity.getHeldItems().remove(plrEntity);
            targetLocation.getHeldItems().add(plrEntity);
            response += "You go to the " + targetLocation.getName() + "\n" + this.look(playerName);
        }
        else if(locationEntity.equals(targetLocation)){
             response += "Are you lost? You are in the " + targetLocation.getName()+" right now."+"\n";
        }
        else {
            response += "The specified location is not valid!";
        }
        return response;
    }

    //processes the look command and return the response
    public String look(String playerName) {
        Player plr = players.get(playerName);
        String location = plr.getLocation();
        Entities locationEntity = world.findEntity(location);

        String response = "";
        response += "The location you are currently in is the "+location+"\n";
        response += world.findEntity(location).getDescription() +"\n";
        response += "There are the following artefacts in this location : " + entitiesListToString(locationEntity.getHeldItemsTyped("artefacts")) + "\n";
        response += "There are paths to the following locations : " + entitiesListToString(locationEntity.getAdjacent()) + "\n";
        response += "There are the following items of furniture : " + entitiesListToString(locationEntity.getHeldItemsTyped("furniture")) + "\n";
        if(!locationEntity.getHeldItemsTyped("characters").isEmpty()){
            response += "There's a " + entitiesListToString(locationEntity.getHeldItemsTyped("characters")) + " in front of you\n";
        }
        if (areOtherPlayersInLocation(playerName, players.get(playerName).getLocation())){
            response += parser.listItems(otherPlayersInLocation(playerName, players.get(playerName).getLocation())) + " are/is also in this location";
        }
        return response;
    }

    //process the inv command and return the response
    public String inv(String playerName){
        String response = "";
        Player plr = players.get(playerName);
        response += "You have the following items in your inventory : " + entitiesListToString(plr.getMyInventory());

        return response;
    }

    public String state(String playerName){
        String response = "";
        Player plr = players.get(playerName);
        response += "Health : " + plr.getPlayerHealth();

        return response;
    }
    //process the get command and return
    public String get(String playerName, String[] item){
        List<String> itemList = Arrays.asList(item);
        String response = "";
        Player plr = players.get(playerName);
        Entities plrEntity = world.findEntity(playerName);
        String location = plr.getLocation();
        Entities locationEntity = world.findEntity(location);

        if (locationEntity.findIfEntity(itemList.get(1)) && itemList.size() == 2){
            if (!locationEntity.findEntityHolder(itemList.get(1)).getEntityType().equals("players")){
                if (!locationEntity.findEntity(itemList.get(1)).getEntityType().equals("artefacts")){
                    response = "You can only pick up artefacts";
                }
                else{
                    Entities holderEntity = locationEntity.findEntity(itemList.get(1));
                    locationEntity.removeEntity(itemList.get(1));
                    plrEntity.addHeldItems(holderEntity);
                    response = "You pick up the " + itemList.get(1);
                }

            }
            else{
                response = "No such item to be found";
            }

        }
        else if (itemList.size() > 2){
            response = "Please specify one singular item you would like to pick up";
        }
        else{
            response = "No such item to be found";
        }

        return response;
    }
    public String drop(String playerName, String[] item){
        List<String> itemList = Arrays.asList(item);
        String response = "";
        Player plr = players.get(playerName);
        Entities plrEntity = world.findEntity(playerName);
        String location = plr.getLocation();
        Entities locationEntity = world.findEntity(location);

        if (plrEntity.findIfEntity(itemList.get(1)) && itemList.size() == 2){
            Entities holderEntity = plrEntity.findEntity(itemList.get(1));
            plrEntity.removeEntity(itemList.get(1));
            locationEntity.addHeldItems(holderEntity);
            response = "You drop the " + itemList.get(1);
        }
        else if (itemList.size() > 2){
            response = "Please specify one singular item you would like to drop";
        }
        else{
            response = "No such item to be found";
        }

        return response;
    }
    //reset the game state and return it to original
    public String reset(){
        this.players =  new HashMap<>();
        this.world = parser.initialiseEntities();
        return "GAME HAS BEEN RESET!!!";
    }

    public String performAction(String playerName, String[] command){
        List<String> commandList = Arrays.asList(command);
        String response = "";
        String playerLocation = players.get(playerName).getLocation();
        Entities playerLocationEntity = world.findEntity(playerLocation);
        Player plr = players.get(playerName);
        Entities plrEntity = world.findEntity(playerName);
        if (listContainsKey(commandList, actions.getSubjects())){
            String key = usedKey(commandList, actions.getSubjects());
            if (listIsSubset(actions.getSubjects().get(key), commandList)){
                if(canAct(playerLocation, playerName, actions.getSubjects().get(key))){
                    produceList(actions.getProduced().get(key), playerName);
                    consumedList(actions.getConsumed().get(key), playerName);
                    if(actions.getProduced().get(key).contains("health")){
                        players.get(playerName).increaseHealth();
                    }
                    if(actions.getConsumed().get(key).contains("health")){
                        players.get(playerName).reduceHealth();
                    }
                    if (world.findIfOneIsEntity(actions.getProduced().get(key))){

                        Entities foundEntity = world.findOneEntity((actions.getProduced().get(key)));
                        if (foundEntity.getEntityType().equals("location")){
                            playerLocationEntity.addAdjacent(foundEntity);

                        }
                    }

                    response += actions.getNarration().get(key).get(0);
                    if(plr.getPlayerHealth()<= 0){
                        plr.move(startingLocation);

                        playerLocationEntity.addHeldItems(plr.getMyInventory());
                        world.findEntity(startingLocation).addHeldItems(plrEntity);
                        plr.setHealth(3);
                        plrEntity.emptyHeldItems();
                        plr.setMyInventory(plrEntity.getHeldItems());
                        response += "\nYou died. You drop all the items in your inventory and return back to your starting location.";
                    }
                }
                else{
                    response += "You don't have access to the resources necessary to perform this action";
                }
            }
            else{
                response += "Please use the command " + key +" correctly";
            }
        }
        else{
            response += "Please input a correct command";
        }

        return response;
    }

    //with a Node list you can find if a node with the specefied node name exists in that list
    public boolean listContainsKey(List<String> list, HashMap<String, ArrayList<String>> map){
        for (int i = 0; i < list.size(); i ++){
            if (map.containsKey(list.get(i))){
                return true;
            }
        }
        return false;
    }

    public String usedKey(List<String> list, HashMap<String, ArrayList<String>> map){
        String key = "";
        for (int i = 0; i < list.size(); i ++){
            if (map.containsKey(list.get(i))){
                return list.get(i);
            }
        }
        return key;
    }

    public boolean listIsSubset(ArrayList<String> mainList, List subsetList){
        boolean checker = false;
        for (int i = 1; i < subsetList.size(); i ++){
            if(mainList.contains(subsetList.get(i))){
                checker = true;
            }
            if(!checker){
                return false;
            }
            else{
                checker = false;
            }
        }
        return true;
    }

    public boolean canAct(String location, String playerName, ArrayList<String> subjects){
        boolean checker = false;
        Entities locationEntity = world.findEntity(location);


        for (int i = 0; i < subjects.size(); i ++){
            if(locationEntity.findIfEntity(subjects.get(i))){
                Entities itemHolder = locationEntity.findEntityHolder(subjects.get(i));

                if ((itemHolder.getEntityType().equals("players") && itemHolder.getName().equals(playerName)) || !itemHolder.getEntityType().equals("players")){
                    checker = true;
                }
            }
            if(!checker){
                return false;
            }
            else{
                checker = false;
            }


        }
        return true;
    }

    public void produce(String entityName, String playerName){

        if(world.findIfEntity(entityName) ){

            String playerLocation = players.get(playerName).getLocation();
            Entities plrEntity = world.findEntity(playerName);
            Player plr = players.get(playerName);
            Entities playerLocationEntity = world.findEntity(playerLocation);
            Entities entity = world.findEntity(entityName);
            if (entity.getEntityType().equals("artefacts") || entity.getEntityType().equals("characters") || entity.getEntityType().equals("furniture")){
                world.removeEntity(entityName);
                plr.setMyInventory(plrEntity.getHeldItems());
                playerLocationEntity.getHeldItems().add(entity);
            }

        }
    }

    public void produceList(ArrayList<String> entityNames, String playerName){

        for (int i = 0; i < entityNames.size(); i++){
            produce(entityNames.get(i), playerName);
        }

    }
    public void consume(String entityName, String playerName){

        if(world.findIfEntity(entityName)){
            Player plr = players.get(playerName);
            Entities plrEntity = world.findEntity(playerName);
            Entities storeroomEntity = world.findEntity("storeroom");
            Entities entity = world.findEntity(entityName);
            if (entity.getEntityType().equals("artefacts") || entity.getEntityType().equals("characters") || entity.getEntityType().equals("furniture")){
                world.removeEntity(entityName);
                plr.setMyInventory(plrEntity.getHeldItems());
                storeroomEntity.getHeldItems().add(entity);
            }
        }
    }

    public void consumedList(ArrayList<String> entityNames, String playerName){

        for (int i = 0; i < entityNames.size(); i++){
            consume(entityNames.get(i), playerName);
        }

    }

    public boolean areOtherPlayersInLocation(String player, String location){

        for (Map.Entry<String, Player> plr : players.entrySet()){

            if(plr.getValue().getLocation().equals(location) && !plr.getValue().getName().equals(player)){
                return true;
            }
        }
        return false;
    }
    public ArrayList<String> otherPlayersInLocation(String player, String location){
        ArrayList<String> playersInLocation = new ArrayList<>();
        for (Map.Entry<String, Player> plr : players.entrySet()){

            if(plr.getValue().getLocation().equals(location) && !plr.getValue().getName().equals(player)){
                playersInLocation.add(plr.getValue().getName());
            }
        }
        return playersInLocation;
    }

    public String entitiesListToString(ArrayList<Entities> items){

        if(items.size() == 0){
            return "none";
        }
        if(items.size() == 1){
            return items.get(0).getName();
        }
        if (items.size() == 2){
            return items.get(0).getName() + " and " + items.get(1).getName();
        }
        else {
            String result = "";
            for(int i = 0; i < items.size(); i ++){
                if(items.size() - i - 1== 0){
                    result = result + " and " + items.get(i).getName();
                }
                else if(items.size() - i - 1 == 1){
                    result = result + items.get(i).getName();
                }
                else{
                    result = result + items.get(i).getName() + ", ";
                }
            }

            return result;
        }
    }
}
