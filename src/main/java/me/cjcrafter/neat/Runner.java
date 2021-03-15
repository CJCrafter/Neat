package me.cjcrafter.neat;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Runner {

    public static void main(String[] args) {
        String directory = System.getProperty("user.dir");
        File file = new File(directory, "neat.json");

        JSONObject json = new JSONObject();
        json.put("So",  true);

        JSONObject test = new JSONObject();
        test.put("Lol", "Yes");
        json.put("Node", test);

        try (FileWriter writer = new FileWriter(directory + File.separator + "neat.json")) {
            writer.write(json.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }


        //Neat neat = new Neat(5, 1, 100);
        //neat.temp();

        //frame.setGenome(neat.getC);
    }
}
