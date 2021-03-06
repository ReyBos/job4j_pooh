package ru.job4j;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

public class Broker {
    private final Map<String, UnaryOperator<JsonObject>> dispatch = new HashMap<>();

    public Broker() {
        dispatch.put("queue", new QueueBroker());
        dispatch.put("topic", new TopicBroker());
    }

    public JsonObject process(JsonObject request) {
        String mode = request.get("mode").getAsString();
        return dispatch.get(mode).apply(request);
    }
}
