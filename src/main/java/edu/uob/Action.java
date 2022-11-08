package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;

////Key : keyword value : ArrayList<String>
//
public class Action { ;
    private HashMap<String, ArrayList<String>> subjects;
    private HashMap<String, ArrayList<String>> consumed;
    private HashMap<String, ArrayList<String>> produced;
    private HashMap<String, ArrayList<String>> narration;

    public Action(
            HashMap<String, ArrayList<String>> subjects,
            HashMap<String, ArrayList<String>> consumed,
            HashMap<String, ArrayList<String>> produced,
            HashMap<String, ArrayList<String>> narration
    ){
        this.subjects = subjects;
        this.consumed = consumed;
        this.produced = produced;
        this.narration = narration;
    }

    public HashMap<String, ArrayList<String>> getSubjects(){
        return subjects;
    }

    HashMap<String, ArrayList<String>> getConsumed(){
        return consumed;
    }
    HashMap<String, ArrayList<String>> getProduced(){
        return produced;
    }
    HashMap<String, ArrayList<String>> getNarration(){
        return narration;
    }


}

