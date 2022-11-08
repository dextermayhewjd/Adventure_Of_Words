package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

final class CommandTests {

  private GameServer server;

  // This method is automatically run _before_ each of the @Test methods
  @BeforeEach
  void setup() {
      server = new GameServer();
  }

  @Test
  void testLookCommand() {
    server.handleCommand("Daniel: reset");
    String response = server.handleCommand("Daniel: look");
    String[] splitResponse = response.split("\n");
    assertTrue(splitResponse[0].contains("The location you are currently in is") & splitResponse[0].contains("cabin"), "No location returned by `look`");
    assertFalse(splitResponse[0].contains("forest") | splitResponse[0].contains("cellar")
            | splitResponse[0].contains("riverbank") | splitResponse[0].contains("clearing")
            | splitResponse[0].contains("storeroom"),"Incorrect location returned by `look`");

    assertTrue(response.contains("There are the following artefacts in this location") & splitResponse[2].contains("potion")
            & splitResponse[2].contains("axe") & splitResponse[2].contains("coin"), "No artefacts returned by `look`");
    assertFalse(splitResponse[2].contains("log") | splitResponse[2].contains("shovel")
            | splitResponse[2].contains("gold") | splitResponse[2].contains("key")
            | splitResponse[2].contains("horn"), "Incorrect artefacts returned by `look`");

    assertTrue(splitResponse[3].contains("There are paths to the following locations") & splitResponse[3].contains("forest"), "No paths returned by `look`");

    assertFalse(splitResponse[3].contains("cabin") | splitResponse[3].contains("cellar")
            | splitResponse[3].contains("riverbank") | splitResponse[3].contains("clearing")
            | splitResponse[3].contains("storeroom"),"Incorrect paths returned by `look`");

    assertTrue(splitResponse[4].contains("There are the following items of furniture") & splitResponse[4].contains("trapdoor"), "No paths returned by `look`");

    assertTrue(splitResponse[1].equals("A log cabin in the woods"), "Incorrect location description returned by `look`");

  }
  @Test
  void testInventoryCommand() {
    server.handleCommand("Daniel: reset");
    String response = server.handleCommand("Daniel: inv");
    assertTrue(response.contains("You have the following items in your inventory"), "Inventory not listed");
    server.handleCommand("Daniel: get potion");
    response = server.handleCommand("Daniel: inv");
    assertTrue(response.contains("You have the following items in your inventory") && response.contains("potion"), "Inventory not listed");
  }
  @Test
  void testGoToCommand(){
    server.handleCommand("Daniel: reset");
    String response = server.handleCommand("Daniel: goto forest");
    String[] splitResponse = response.split("\n");

    assertTrue(splitResponse[0].equals("You go to the forest"));

    assertTrue(splitResponse[1].contains("The location you are currently in is") & splitResponse[1].contains("forest"), "No location returned by `look`");
    assertFalse(splitResponse[1].contains("cabin") | splitResponse[1].contains("cellar")
            | splitResponse[1].contains("riverbank") | splitResponse[1].contains("clearing")
            | splitResponse[1].contains("storeroom"),"Incorrect location returned by `look`");
  }
  @Test
  void testTokenization() {
    server.handleCommand("Daniel: reset");
    String response = server.handleCommand("Daniel: ... I  You INv at");
    assertTrue(response.contains("You have the following items in your inventory"), "Inventory not listed");

  }
  @Test
  void testGetCommand() {
    String response;
    String[] responses;

    server.handleCommand("Daniel: reset");
    response = server.handleCommand("Daniel: get potion");
    assertTrue(response.equals("You pick up the potion"), "Item is not in inventory");

    response = server.handleCommand("Daniel: get trapdoor");
    assertTrue(response.equals("You can only pick up artefacts"), "Allowed to pick up item that isn't an artefact");

    response = server.handleCommand("Daniel: inv");
    assertTrue(response.contains("potion"), "Item is not in inventory");

    responses = server.handleCommand("Daniel: look").split("\n");
    assertTrue(!responses[2].contains("potion"), "Picked up item is still in the game location");

    server.handleCommand("Daniel: goto forest");

    response = server.handleCommand("Daniel: get key");
    assertTrue(response.equals("You pick up the key"), "Item is not in inventory");

    response = server.handleCommand("Daniel: inv");
    assertTrue(response.contains("potion") && response.contains("key"), "Item is not in inventory");

    responses = server.handleCommand("Daniel: look").split("\n");
    assertTrue(!responses[2].contains("key"), "Picked up item is still in the game location");

    server.handleCommand("Daniel: reset");

    response = server.handleCommand("Daniel: get potion axe");
    assertTrue(response.equals("Please specify one singular item you would like to pick up"), "You should not picked up something");

    response = server.handleCommand("Daniel: inv");
    assertTrue(response.contains("none"), "Item should not in inventory");

    responses = server.handleCommand("Daniel: look").split("\n");
    assertTrue(responses[2].contains("potion") && responses[2].contains("axe"), "Picked up item should still in the game location");

    server.handleCommand("Daniel: reset");

    response = server.handleCommand("Daniel: get potion axe coin");
    assertTrue(response.equals("Please specify one singular item you would like to pick up"), "You should not picked up something");

    response = server.handleCommand("Daniel: inv");
    assertTrue(response.contains("none"), "Item should not in inventory");

    responses = server.handleCommand("Daniel: look").split("\n");
    assertTrue(responses[2].contains("potion") && responses[2].contains("axe") && responses[2].contains("coin"), "Picked up item should still in the game location");

    server.handleCommand("Daniel: reset");

    response = server.handleCommand("Daniel: get trapdoor");
    assertTrue(response.equals("You can only pick up artefacts"), "You should not picked up furniture");
  }
  @Test
  void testDropCommand() {
    String response;
    String[] responses;

    server.handleCommand("Daniel: reset");
    server.handleCommand("Daniel: get potion");
    server.handleCommand("Daniel: goto forest");
    server.handleCommand("Daniel: get key");



    response = server.handleCommand("Daniel: inv");
    assertTrue(response.contains("potion") & response.contains("key"), "Item is not in inventory");

    responses = server.handleCommand("Daniel: look").split("\n");
    assertTrue(!responses[2].contains("potion") & !responses[2].contains("key"), "Item is in location it shouldn't be");

    response = server.handleCommand("Daniel: drop key");
    assertTrue(response.equals("You drop the key"), "Incorrect response to command");

    responses = server.handleCommand("Daniel: look").split("\n");
    assertTrue(!responses[2].contains("potion") & responses[2].contains("key"), "Dropped item is not in location");

    response = server.handleCommand("Daniel: inv");
    assertTrue(response.contains("potion") & !response.contains("key"), "Dropped item still in inventory");

    response = server.handleCommand("Daniel: drop potion");
    assertTrue(response.equals("You drop the potion"), "Item is not in inventory");

    responses = server.handleCommand("Daniel: look").split("\n");
    assertTrue(responses[2].contains("potion") & responses[2].contains("key"), "Dropped item is not in location");

    response = server.handleCommand("Daniel: inv");
    assertTrue(!response.contains("potion") & !response.contains("key"), "Dropped item still in inventory");

    server.handleCommand("Daniel: goto cabin");

    response = server.handleCommand("Daniel: drop axe");
    assertTrue(response.equals("No such item to be found"), "You don't have a axe, you cant drop it");

    server.handleCommand("Daniel: reset");
    server.handleCommand("Daniel: get potion");
    server.handleCommand("Daniel: get coin");
    server.handleCommand("Daniel: get axe");

    response = server.handleCommand("Daniel: drop potion coin");
    assertTrue(response.equals("Please specify one singular item you would like to drop"), "You should not drop down something");

    response = server.handleCommand("Daniel: inv");
    assertTrue(response.contains("potion") && response.contains("coin") && response.contains("axe"), "Item should not be drop down from inventory");

    responses = server.handleCommand("Daniel: look").split("\n");
    assertTrue(responses[2].contains("none"), "nothing should be dropped in the game location");

    response = server.handleCommand("Daniel: drop potion coin axe");
    assertTrue(response.equals("Please specify one singular item you would like to drop"), "You should not drop down something");

    response = server.handleCommand("Daniel: inv");
    assertTrue(response.contains("potion") && response.contains("coin") && response.contains("axe"), "Item should not be drop down from inventory");

    responses = server.handleCommand("Daniel: look").split("\n");
    assertTrue(responses[2].contains("none"), "nothing should be dropped in the game location");

  }
  @Test
  void testResetCommand(){
    String response;
    String[] responses;
    server.handleCommand("Daniel: reset");
    server.handleCommand("Daniel: get potion");
    server.handleCommand("Daniel: goto forest");

    server.handleCommand("Daniel: reset");

    responses = server.handleCommand("Daniel: look").split("\n");
    assertTrue(responses[0].contains("cabin"));

    response = server.handleCommand("Daniel: inv");
    assertTrue(response.equals("You have the following items in your inventory : none"));

  }
  @Test
  void testFirstActionMap(){
    ActionsReader ar = new ActionsReader();
    assertTrue(ar.getSubjectsMap().get("open").get(0).equals("trapdoor"));
    assertTrue(ar.getConsumedMap().get("open").get(0).equals("key"));
    assertTrue(ar.getProducedMap().get("open").get(0).equals("cellar"));
    assertTrue(ar.getNarrationMap().get("open").get(0).equals("You unlock the door and see steps leading down into a cellar"));
  }
  @Test
  void testSecondActionMap(){
    ActionsReader ar = new ActionsReader();
    assertTrue(ar.getSubjectsMap().get("cutdown").get(0).equals("tree"));
    assertTrue(ar.getConsumedMap().get("cutdown").get(0).equals("tree"));
    assertTrue(ar.getProducedMap().get("cutdown").get(0).equals("log"));
    assertTrue(ar.getNarrationMap().get("cutdown").get(0).equals("You cut down the tree with the axe"));
  }
  @Test
  void testWhetherFightProduce(){
    ActionsReader ar = new ActionsReader();
    assertTrue(ar.getSubjectsMap().get("fight").get(0).equals("elf"));
    assertTrue(ar.getConsumedMap().get("fight").get(0).equals("health"));
    assertTrue(ar.getProducedMap().get("fight").isEmpty());
    assertTrue(ar.getNarrationMap().get("fight").get(0).equals("You attack the elf, but he fights back and you lose some health"));
  }
  @Test
  void testWhetherBlowConsume(){
    ActionsReader ar = new ActionsReader();
    assertTrue(ar.getSubjectsMap().get("blow").get(0).equals("horn"));
    assertTrue(ar.getConsumedMap().get("blow").isEmpty());
    assertTrue(ar.getProducedMap().get("blow").get(0).equals("lumberjack"));
    assertTrue(ar.getNarrationMap().get("blow").get(0).equals("You blow the horn and as if by magic, a lumberjack appears !"));
  }
  @Test
  void testActions(){
    String response;
    String[] responses;
    response = server.handleCommand("Daniel: chop tree with axe");
    assertTrue(response.equals("You don't have access to the resources necessary to perform this action"), "Performing action while not having access to needed items");
    response = server.handleCommand("Daniel: get potion");
    response = server.handleCommand("Daniel: inv");


    assertTrue(response.contains("You have the following items in your inventory : potion"), "Potion is still in inventory");
    server.handleCommand("Daniel: goto forest");


    response = server.handleCommand("Daniel: chop tree with axe");
    assertTrue(response.equals("You don't have access to the resources necessary to perform this action"), "Performing action while not having access to needed items");



    response = server.handleCommand("Daniel: drink potion");
    assertTrue(response.equals("You drink the potion and your health improves"), "Not performing action or performing incorrect action when necessary items are present");

    response = server.handleCommand("Daniel: inv");

    assertTrue(!response.contains("potion"), "Potion is still in inventory");

    server.handleCommand("Daniel: goto cabin");
    server.handleCommand("Daniel: get axe");
    server.handleCommand("Daniel: goto forest");

    response = server.handleCommand("Daniel: i chop down the tree with my axe");
    assertTrue(response.equals("You cut down the tree with the axe"), "Not performing action or performing incorrect action when necessary items are present");

    responses = server.handleCommand("Daniel: look").split("\n");
    assertTrue(responses[2].contains("log"), "Item that should be produced isn't");
  }
  @Test
  void testPartialCommands(){
    String response;
    String[] responses;
    server.handleCommand("Daniel: goto forest");
    server.handleCommand("Daniel: get key");
    server.handleCommand("Daniel: goto cabin");
    response=server.handleCommand("Daniel: unlock trapdoor");
    assertTrue(response.equals("You unlock the door and see steps leading down into a cellar"));
  }

//  @Test
//  void testDecoratedCommands(){
//    String response;
//    String[] responses;
//    server.handleCommand("Daniel: goto forest");
//    server.handleCommand("Daniel: get key");
//    server.handleCommand("Daniel: goto cabin");
//    response=server.handleCommand("Please unlock the trapdoor using the key");
//    assertTrue(response.equals("You unlock the door and see steps leading down into a cellar"));
//  }

//  @Test
//  void testInvertedCommands(){
//    String response;
//    String[] responses;
//    server.handleCommand("Daniel: goto forest");
//    server.handleCommand("Daniel: get key");
//    server.handleCommand("Daniel: goto cabin");
//    response=server.handleCommand("Daniel: use the key to unlock the trapdoor");
//    assertTrue(response.equals("You unlock the door and see steps leading down into a cellar"));
//  }

  @Test
  void testMultiplayer(){
    String response;
    String[] responses;
    server.handleCommand("Daniel: goto forest");

    responses = server.handleCommand("May: goto forest").split("\n");
    assertTrue(responses[6].contains("Daniel"), "Other player not registered as being in the same location");


    server.handleCommand("Daniel: get key");

    response = server.handleCommand("Charlie: open");
    assertTrue(response.equals("You don't have access to the resources necessary to perform this action"), "Performing actions without the needed resources");

    responses = server.handleCommand("Daniel: goto cabin").split("\n");
    assertTrue(responses[6].contains("Charlie"), "Other player not registered as being in teh same location");

    response = server.handleCommand("Charlie: open");
    assertTrue(response.equals("You don't have access to the resources necessary to perform this action"), "Performing actions without the needed resources");

    server.handleCommand("Daniel: open");

    server.handleCommand("Charlie: get potion");


    response = server.handleCommand("Daniel: get potion");
    assertTrue(response.equals("No such item to be found"), "Picking item that should be in another player's inventory");

    server.handleCommand("Charlie: goto cellar");

    response = server.handleCommand("Daniel: hit elf");
    assertTrue(response.equals("You don't have access to the resources necessary to perform this action"), "Performing actions without the needed resources");

    server.handleCommand("Charlie: hit elf");


    server.handleCommand("Charlie: hit elf");


    response = server.handleCommand("Charlie: hit elf");
    assertTrue(response.contains("You died"), "Player not ding after being dropped to 0 health");

    responses = server.handleCommand("Daniel: look").split("\n");
    assertTrue(responses[5].contains("Charlie"), "Other player not registered as being in the same location");

    responses = server.handleCommand("Daniel: goto cellar").split("\n");
    assertTrue(responses[3].contains("potion"), "Dead player did not drop item they had in their inventory");

    server.handleCommand("Daniel: drink potion");
    server.handleCommand("Daniel: hit elf");
    server.handleCommand("Daniel: hit elf");

    response = server.handleCommand("Daniel: hit elf");
    assertTrue(!response.contains("You died"), "Dies after 3 hits even though drank potion");

    response = server.handleCommand("Daniel: hit elf");
    assertTrue(response.contains("You died"));


  }
}
