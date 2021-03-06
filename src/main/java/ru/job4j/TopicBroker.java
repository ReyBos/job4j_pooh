package ru.job4j;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TopicBroker implements UnaryOperator<JsonObject> {
    private static final Logger LOG = LoggerFactory.getLogger(Broker.class.getName());
    private final ConcurrentHashMap<String, LinkedBlockingQueue<String>> topics =
            new ConcurrentHashMap<>();

    public TopicBroker() {
        topics.put("*.Moscow", new LinkedBlockingQueue<>());
        topics.put("weather.Moscow", new LinkedBlockingQueue<>());
    }

    @Override
    public JsonObject apply(JsonObject request) {
        JsonObject response = new JsonObject();
        String requestAction = request.get("action").getAsString();
        String requestKey = request.get("key").getAsString();
        if (requestAction.equals("POST")) {
            String message = request.get("text").getAsString();
            response = addMessage(requestKey, message);
        } else if (requestAction.equals("GET")) {
            response = getMessage(requestKey);
        }
        return response;
    }

    private JsonObject addMessage(String requestKey, String message) {
        JsonObject response = new JsonObject();
        for (String topicKey : topics.keySet()) {
            String topicKeyPattern = topicKey.replaceAll("#", "\\.+");
            topicKeyPattern = topicKeyPattern.replaceAll("\\*", "\\.+");
            Pattern pattern = Pattern.compile(topicKeyPattern);
            Matcher matcher = pattern.matcher(requestKey);
            if (matcher.find()) {
                topics.get(topicKey).offer(message);
            }
        }
        response.addProperty("text", "добавлено в топики");
        return response;
    }

    private JsonObject getMessage(String requestKey) {
        JsonObject response = new JsonObject();
        LinkedBlockingQueue<String> topic = topics.get(requestKey);
        String text = null;
        try {
            text = topic.take();
        } catch (InterruptedException e) {
            LOG.error("Ошибка ", e);
        }
        response.addProperty("text", text);
        return response;
    }
}
