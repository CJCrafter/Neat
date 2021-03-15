package me.cjcrafter.neat.file;

import org.json.simple.JSONObject;

public interface Serializable {

    JSONObject deserialize();

    void serialize(JSONObject data);
}
