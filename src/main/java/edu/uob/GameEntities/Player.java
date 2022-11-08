package edu.uob.GameEntities;

import java.util.ArrayList;
import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;

public class Player {


    private ArrayList<Entities> myInventory;
    private String name;
    private String currentLocation;

    private int playerHealth;

    public Player(String name, String start) {
        this.name = name;
        this.currentLocation = start;
        this.myInventory = new ArrayList<>();
        this.playerHealth = 3;
    }

    public boolean addItem(Entities item) {
        this.myInventory.add(item);
        return true;
    }

    public String getLocation() {
        return this.currentLocation;
    }

    public String getName(){
        return this.name;
    }
    public int getPlayerHealth(){
        return playerHealth;
    }
    public ArrayList<Entities> getMyInventory(){
        return myInventory;
    }
    public void setMyInventory(ArrayList<Entities> inventory){
        this.myInventory = inventory;
    }
    public void setHealth(int health){
        playerHealth = health;
    }
    public void increaseHealth(){
        playerHealth += 1;
    }
    public void reduceHealth(){
        playerHealth -= 1;
    }



    public void move(String newLocation) {
        this.currentLocation = newLocation;
    }
}
