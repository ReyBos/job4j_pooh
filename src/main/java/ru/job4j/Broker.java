package ru.job4j;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

public class Broker {
    private static final Logger LOG = LoggerFactory.getLogger(Broker.class.getName());
    private final ConcurrentHashMap<String, Queue<String>> queues;
    private final ConcurrentHashMap<String, Queue<String>> topics;
    private final ConcurrentHashMap<String, Function<JsonObject, JsonObject>> dispatch;

    public Broker() {
        queues = new ConcurrentHashMap<>();
        topics = new ConcurrentHashMap<>();
        dispatch = new ConcurrentHashMap<>();
        queues.put("weather", new LinkedBlockingQueue<>());
        topics.put("weather", new LinkedBlockingQueue<>());
        dispatch.put("queue", this.toQueue());
        dispatch.put("topic", this.toTopic());
    }

    private Function<JsonObject, JsonObject> toQueue() {
        return json -> {
            JsonObject resp = new JsonObject();
            String action = json.get("action").getAsString();
            String key = json.get("key").getAsString();
            Queue<String> queue = queues.get(key);
            if (action.equals("POST")) {
                String text = json.get("text").getAsString();
                queue.offer(text);
                resp.addProperty("text", "добавлено в очередь");
            } else if (action.equals("GET")) {
                String text = queue.poll();
                resp.addProperty("text", text);
            }
            return resp;
        };
    }

    private Function<JsonObject, JsonObject> toTopic() {
        return json -> {
            JsonObject resp = new JsonObject();
            String action = json.get("action").getAsString();
            String key = json.get("key").getAsString();
            Queue<String> topic = topics.get(key);
            if (action.equals("POST")) {
                String text = json.get("text").getAsString();
                topic.offer(text);
                resp.addProperty("text", "добавлено в топики");
            } else if (action.equals("GET")) {
                String text = topic.poll();
                resp.addProperty("text", text);
            }
            return resp;
        };
    }

    public JsonObject process(JsonObject req) {
        String mode = req.get("mode").getAsString();
        JsonObject resp = dispatch.get(mode).apply(req);
        return resp;
    }
}
