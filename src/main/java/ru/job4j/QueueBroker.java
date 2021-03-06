package ru.job4j;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.UnaryOperator;

public class QueueBroker implements UnaryOperator<JsonObject> {
    private static final Logger LOG = LoggerFactory.getLogger(Broker.class.getName());
    private final ConcurrentHashMap<String, LinkedBlockingQueue<String>> queues =
            new ConcurrentHashMap<>();

    @Override
    public JsonObject apply(JsonObject request) {
        JsonObject response = new JsonObject();
        String requestAction = request.get("action").getAsString();
        String requestKey = request.get("key").getAsString();
        queues.putIfAbsent(requestKey, new LinkedBlockingQueue<>());
        LinkedBlockingQueue<String> queue = queues.get(requestKey);
        if (requestAction.equals("POST")) {
            String message = request.get("text").getAsString();
            queue.offer(message);
            response.addProperty("text", "добавлено в очередь");
        } else if (requestAction.equals("GET")) {
            String text = null;
            try {
                text = queue.take();
            } catch (InterruptedException e) {
                LOG.error("Ошибка ", e);
            }
            response.addProperty("text", text);
        }
        return response;
    }
}
