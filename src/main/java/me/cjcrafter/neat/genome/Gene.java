package me.cjcrafter.neat.genome;

import me.cjcrafter.neat.file.Serializable;
import org.json.simple.JSONObject;

public class Gene implements Serializable {

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

    }

    @Override
    public String toString() {
        return "Gene{" +
                "id=" + id +
                '}';
    }
}
