
package edu.uob;
import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


public class ActionsReader {

    private HashMap<String, ArrayList<String>> subjectsMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> consumedMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> producedMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> narrationMap = new HashMap<>();


    public ActionsReader(){
        DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance ();
         try {
            DocumentBuilder dombuilder = domfac.newDocumentBuilder ();
            InputStream is = new FileInputStream ("config" + File.separator + "actions.xml");
            Document doc = dombuilder.parse (is);
            // create the document
            Element root = doc.getDocumentElement();
            NodeList actions = root.getChildNodes();
            // get all the actions and creating an action node-list
            for (int a = 1 ; a < actions.getLength(); a+=2) {
                // get every action quite werid
                Element actionEl = (Element) actions.item(a);

                NodeList trigList = actionEl.getElementsByTagName("triggers");
                Element triggers = (Element)trigList.item(0);
                NodeList keyList = triggers.getElementsByTagName("keyword");

                NodeList subList = actionEl.getElementsByTagName("subjects");
                Element subjects = (Element)subList.item(0);
                NodeList entity0List = subjects.getElementsByTagName("entity");

                NodeList comList = actionEl.getElementsByTagName("consumed");
                Element consumed = (Element)comList.item(0);
                NodeList entity1List = consumed.getElementsByTagName("entity");

                NodeList proList = actionEl.getElementsByTagName("produced");
                Element produced = (Element)proList.item(0);
                NodeList entity2List = produced.getElementsByTagName("entity");

                NodeList narList = actionEl.getElementsByTagName("narration");

                for (int b = 0; b <keyList.getLength();b++){

                    // get every trigger in sequence
                    String keyword = keyList.item(b).getTextContent();
//                    System.out.print("\n"+keyword+ "\n");

                    ArrayList<String> subjectList = entitiesToList(entity0List);
//                    for (int c = 0;c<subjectList.size();c++ ){
//                        System.out.print(subjectList.get(c)+" ");
//                    }
                    ArrayList<String> consumedList = entitiesToList(entity1List);
                    ArrayList<String> producedList = entitiesToList(entity2List);
                    ArrayList<String> narrationList = entitiesToList(narList);

//                  System.out.print(" keyword: "+ keyword+ " subject: "+subject+"\n");
                    //System.out.println(entity2List.getLength());

                    subjectsMap.put(keyword, subjectList);
                    consumedMap.put(keyword, consumedList);
                    producedMap.put(keyword, producedList);
                    narrationMap.put(keyword, narrationList);
                    //System.out.print(" keyword: "+ keyword+ " subjects: "+subjectList+ " consumed: "+ consumedList+" produce: "+producedList+" narration: "+narrationList+"\n");
                }
            }


           } catch (SAXException | IOException | ParserConfigurationException e) {
                      e.printStackTrace();
             }
    }

    public ArrayList<String> entitiesToList(NodeList entityList){
        ArrayList<String> entityArrayList = new ArrayList<>();
        for (int i = 0; i < entityList.getLength(); i++){
            entityArrayList.add(entityList.item(i).getTextContent());
        }
        return entityArrayList;
    }

    public HashMap<String, ArrayList<String>> getSubjectsMap() {
        return subjectsMap;
    }

    public HashMap<String, ArrayList<String>> getConsumedMap() {
        return consumedMap;
    }

    public HashMap<String, ArrayList<String>> getProducedMap() {
        return producedMap;
    }

    public HashMap<String, ArrayList<String>> getNarrationMap() {
        return narrationMap;
    }

    public Action createAction (){
        return new Action(subjectsMap, consumedMap, producedMap, narrationMap);
    }


}


;



