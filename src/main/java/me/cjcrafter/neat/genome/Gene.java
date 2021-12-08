package me.cjcrafter.neat.genome;

import me.cjcrafter.neat.file.Serializable;
import org.json.simple.JSONObject;

public abstract class Gene implements Serializable {

    protected int id;

    public Gene(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public JSONObject deserialize() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        return json;
    }

    @Override
    public void serialize(JSONObject data) {
        id = ((Long) data.get("id")).intValue();
    }

    @Override
    public abstract String toString();
}
