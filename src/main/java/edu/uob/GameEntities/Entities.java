package edu.uob.GameEntities;

import java.util.ArrayList;

public class Entities {
    ArrayList<Entities> heldItems;
    ArrayList<Entities> adjacent;
    String name;
    String entityType;
    String description;

    public Entities(String name, String entityType, String description){
        this.name = name;
        this.entityType = entityType;
        this.description = description;
        this.heldItems = new ArrayList<>();
        this.adjacent = new ArrayList<>();
    }

    public void addHeldItems(Entities item){
        this.heldItems.add(item);
    }

    public void addHeldItems(ArrayList<Entities> items){
        for (int i = 0; i < items.size(); i ++){
            this.heldItems.add(items.get(i));
        }

    }

    public void addAdjacent(Entities location){
        this.adjacent.add(location);
    }
    public ArrayList<Entities> getHeldItems(){
        return this.heldItems;
    }
    public ArrayList<Entities> getHeldItemsTyped(String entityType){
        ArrayList<Entities> heldItemsTyped = new ArrayList<>();
        for (int i = 0; i < this.heldItems.size(); i++){
            if (this.heldItems.get(i).getEntityType().equals(entityType)){
                heldItemsTyped.add(heldItems.get(i));
            }
        }
        return heldItemsTyped;
    }
    public ArrayList<Entities> getAdjacent(){
        return this.adjacent;
    }
    public String getName(){
        return this.name;
    }
    public String getEntityType(){
        return this.entityType;
    }
    public String getDescription(){
        return this.description;
    }
    public Entities findEntity(String entityName){
        Entities foundItem;
        for (int i = 0; i < heldItems.size(); i ++){
            if(heldItems.get(i).getName().equals(entityName)){
                return heldItems.get(i);
            }
            foundItem =  heldItems.get(i).findEntity(entityName);
            if(foundItem != null){
                return foundItem;
            }
        }
        return null;
    }

    public Entities findEntityHolder(String entityName){
        Entities foundItem;
        for (int i = 0; i < heldItems.size(); i ++){
            if(heldItems.get(i).getName().equals(entityName)){
                return this;
            }
            foundItem =  heldItems.get(i).findEntityHolder(entityName);
            if(foundItem != null){
                return foundItem;
            }
        }
        return null;
    }
    public void removeEntity(String entityName){
        Entities entityRemoved = findEntity(entityName);
        Entities entityHolder = findEntityHolder(entityName);
        entityHolder.getHeldItems().remove(entityRemoved);
    }

    public void emptyHeldItems(){
        this.heldItems = new ArrayList<>();
    }

    public boolean findIfEntity(String entityName){
        if(findEntity(entityName) != null){
            return true;
        }
        return false;
    }

    public boolean findIfOneIsEntity(ArrayList<String> text){
        for (int i = 0; i < text.size(); i++){
            if (findIfEntity(text.get(i))){

                return true;
            }
        }
        return false;
    }

    public Entities findOneEntity(ArrayList<String> text){
        for (int i = 0; i < text.size(); i++){
            if (findIfEntity(text.get(i))){
                return findEntity(text.get(i));
            }
        }
        return null;
    }
}
