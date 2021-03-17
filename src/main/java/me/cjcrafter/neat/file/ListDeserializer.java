package me.cjcrafter.neat.file;

import org.json.simple.JSONArray;

import java.util.Collection;

public class ListDeserializer {

    private final Collection<? extends Serializable> list;

    public ListDeserializer(Collection<? extends Serializable> list) {
        this.list = list;
    }

    @SuppressWarnings("unchecked")
    public JSONArray deserialize() {
        JSONArray arr = new JSONArray();
        for (Serializable serializable : list) {
            arr.add(serializable.deserialize());
        }
        return arr;
    }
}
