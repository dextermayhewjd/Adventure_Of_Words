package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;
import edu.uob.GameEntities.Player;


public final class GameServer {
    private final String [] stopWords = {
            "please", "ourselves", "hers", "between", "yourself",
            "but", "again", "there", "about", "once", "during",
            "out", "very", "having", "with", "they", "own", "an",
            "be", "some", "for", "do", "its", "yours", "such",
            "into", "of", "most", "itself", "other", "off", "is",
            "s", "am", "or", "who", "as", "from", "him", "each",
            "the", "themselves", "until", "below", "are", "we",
            "these", "your", "his", "through", "don", "nor", "me",
            "were", "her", "more", "himself", "this", "down",
            "should", "our", "their", "while", "above", "both",
            "up", "to", "ours", "had", "she", "all", "no", "when",
            "at", "any", "before", "them", "same", "and", "been",
            "have", "in", "will", "on", "does", "yourselves",
            "then", "that", "because", "what", "over", "why",
            "so", "can", "did", "not", "now", "under", "he", "you",
            "herself", "has", "just", "where", "too", "only",
            "myself", "which", "those", "i", "after", "few", "whom",
            "t", "being", "if", "theirs", "my", "against", "a", "by",
            "doing", "it", "how", "further", "was", "here", "than"
    };
    private GameState gameState;

    public static void main(String[] args) throws IOException {
        GameServer server = new GameServer();
        server.blockingListenOn(8888);
    }

    public GameServer() {
        EntitiesParser parser = new EntitiesParser();
        this.gameState = new GameState(parser);
    }

    public String[] processText(String text){

        text = " "+text.toLowerCase()+" ";
        text = text.replace(".", "");
        text = text.replace(",", "");
        while(text.contains("  ")){
            text = text.replace("  ", " ");
        }

        for (String stopWord : stopWords) {
            text = text.replace(" " + stopWord + " ", " ");
        }
        text = text.trim();
        String[] splitText = text.split(" ");
        return splitText;
    }

    public String[] FindTheSameWords( String[] s1, String[] s2 ){
HashSet<String> sameword = new HashSet<>();
for(int i = 0; i < s1.length; i++){
    for(int j = 0; j < s2.length; j++){
        if (s1[i].equals(s2[j])){
            sameword.add(s1[i]);
        }
    }
}

        return sameword.toArray(sameword.toArray(new String[]{}));
    }


    public String handleCommand(String incomming) {

        String username = incomming.split(":")[0].trim();
        if (!this.gameState.playerExists(username)) {
            this.gameState.addNewPlayer(username);
        }
        String command = incomming.split(":")[1].trim();
        String [] commands = {"look", "goto", "inv", "get", "drop", "reset", "state"};
        String [] processedCommand = processText(command);
        String [] commandslist = FindTheSameWords(commands,processedCommand);
        String response = "";


if(commandslist.length < 2){
        if (processedCommand[0].equals("look") || Arrays.asList(processedCommand).contains("look")) {
            response += this.gameState.look(username);
        }
        else if (processedCommand[0].equals("inv") || Arrays.asList(processedCommand).contains("inv")) {
            response += this.gameState.inv(username);
        }
        else if (processedCommand[0].equals("state") || Arrays.asList(processedCommand).contains("state")) {
            response += this.gameState.state(username);
        }
        else if (processedCommand[0].equals("goto") || Arrays.asList(processedCommand).contains("goto")) {
            try {
                response += this.gameState.goTo(username, processedCommand[1]);
            }
            catch (ArrayIndexOutOfBoundsException e) {
                response += "You need to specify a location!";
            }
        }
        else if ((processedCommand[0].equals("get") || Arrays.asList(processedCommand).contains("get"))){
            try {
                response += this.gameState.get(username, processedCommand);
            }
            catch (ArrayIndexOutOfBoundsException e) {
                response += "Please use the get command correctly";
            }
        }
        else if (processedCommand[0].equals("drop") || Arrays.asList(processedCommand).contains("drop")){
            try {
                response += this.gameState.drop(username, processedCommand);
            }
            catch (ArrayIndexOutOfBoundsException e){
                response += "Please specify the item you would like to drop";
            }
        }
        else if(processedCommand[0].equals("reset")){
            response += this.gameState.reset();
        }

        else {
            response += gameState.performAction(username, processedCommand);
         }

        }
        else{
            response += "There is more than one command, which one do you want? ";
        }
        return response;
    }

    // Networking method - you shouldn't need to chenge this method !
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }

        }
    }

    // Networking method - you shouldn't need to chenge this method !
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        final char END_OF_TRANSMISSION = 4;
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if(incomingCommand != null) {
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
