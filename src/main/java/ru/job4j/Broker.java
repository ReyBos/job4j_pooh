package ru.job4j;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Broker {
    private static final Logger LOG = LoggerFactory.getLogger(Broker.class.getName());
    private final ConcurrentHashMap<String, LinkedBlockingQueue<String>> queues =
            new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LinkedBlockingQueue<String>> topics =
            new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Function<JsonObject, JsonObject>> dispatch =
            new ConcurrentHashMap<>();

    public Broker() {
        queues.put("weather", new LinkedBlockingQueue<>());
        topics.put("weather.#", new LinkedBlockingQueue<>());
        topics.put("*.Moscow", new LinkedBlockingQueue<>());
        topics.put("weather.Moscow", new LinkedBlockingQueue<>());
        dispatch.put("queue", this.toQueue());
        dispatch.put("topic", this.toTopic());
    }

    private Function<JsonObject, JsonObject> toQueue() {
        return json -> {
            JsonObject resp = new JsonObject();
            String action = json.get("action").getAsString();
            String key = json.get("key").getAsString();
            LinkedBlockingQueue<String> queue = queues.get(key);
            if (action.equals("POST")) {
                String text = json.get("text").getAsString();
                queue.offer(text);
                resp.addProperty("text", "добавлено в очередь");
            } else if (action.equals("GET")) {
                String text = null;
                try {
                    text = queue.take();
                } catch (InterruptedException e) {
                    LOG.error("Ошибка ", e);
                }
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
            if (action.equals("POST")) {
                String text = json.get("text").getAsString();
                for (String topicKey : topics.keySet()) {
                    String topicKeyPattern = topicKey.replaceAll("#", "\\.+");
                    topicKeyPattern = topicKeyPattern.replaceAll("\\*", "\\.+");
                    Pattern pattern = Pattern.compile(topicKeyPattern);
                    Matcher matcher = pattern.matcher(key);
                    if (matcher.find()) {
                        topics.get(topicKey).offer(text);
                    }
                }
                resp.addProperty("text", "добавлено в топики");
            } else if (action.equals("GET")) {
                LinkedBlockingQueue<String> topic = topics.get(key);
                String text = null;
                try {
                    text = topic.take();
                } catch (InterruptedException e) {
                    LOG.error("Ошибка ", e);
                }
                resp.addProperty("text", text);
            }
            return resp;
        };
    }

    public JsonObject process(JsonObject req) {
        String mode = req.get("mode").getAsString();
        return dispatch.get(mode).apply(req);
    }
}
