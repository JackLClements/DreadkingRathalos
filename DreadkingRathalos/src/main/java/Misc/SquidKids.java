/*
 */
package Misc;

import SplatNet2.Stage;
import exceptions.JSONNotFoundException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Package to pull stage info from SquidKids.azurewebsites.net No need for auth
 * in this case so can full straight from site JSON
 *
 * @author Jack L. Clements
 */
public class SquidKids {

    private final static String IMG_URL = "https://app.splatoon2.nintendo.net";
    private final static String JSON_URL = "http://squidkidsfeed.azurewebsites.net/Schedule.json";

    private static String pullJSON() {
        String schedule;
        try {
            URL url = new URL(JSON_URL);
            BufferedReader read = new BufferedReader(new InputStreamReader(url.openStream()));
            schedule = read.readLine();

        } catch (Exception e) {
            e.printStackTrace();
            schedule = "Error";
        }
        return schedule;
    }
    
    /**
     * Displays current Turf War. Will make other function for displaying others.
     * @return 
     */
    public static Stage[] turfWar() {
        Stage[] stages = new Stage[2];
        //fetch json
        String jString = pullJSON();
        JSONObject json = new JSONObject(jString);
        JSONArray regular = new JSONArray(json.get("regular").toString());
        
        ArrayList<JSONObject> turfwar = new ArrayList<>();

        for (int i = 0; i < regular.length(); i++) {
            turfwar.add(regular.getJSONObject(i));
        }
        sortObjects(turfwar); //sorts objects, passed by reference so should be fine

        try {
            JSONObject stage = new JSONObject(turfwar.get(0).get("stage_a").toString());
            Stage stageAbean = new Stage(stage.getString("name"), new URL(IMG_URL + stage.get("image")));
            stage = new JSONObject(turfwar.get(0).get("stage_b").toString());
            Stage stageBbean = new Stage(stage.getString("name"), new URL(IMG_URL + stage.get("image")));            
            stages[0] = stageAbean;
            stages[1] = stageBbean;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return stages;
    }

    public static String[] rankedBattle() {
        return new String[3];
    }

    public static String[] leagueBattle() {
        return new String[3];
    }

    public static void sortObjects(ArrayList<JSONObject> objects) {
        Collections.sort(objects, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject t1, JSONObject t2) {
                int timeA = 0;
                int timeB = 0;//working on unix time here
                try {
                    timeA = Integer.parseInt(t1.get("start_time").toString());
                    timeB = Integer.parseInt(t2.get("start_time").toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (timeA > timeB) {
                    return 1;
                } else if (timeA == timeB) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
    }

}
