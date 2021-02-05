package ru.job4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

public class ServerAction {
    private static final Logger LOG = LoggerFactory.getLogger(EchoServer.class.getName());
    private final LinkedHashMap<Function<String[], Boolean>, Function<String[], String>> dispatch;
    private final Queue<String> queue;
    private final Queue<String> topic;

    public ServerAction() {
        dispatch = new LinkedHashMap<>();
        queue = new ConcurrentLinkedQueue<>();
        topic = new ConcurrentLinkedQueue<>();
        dispatch.put(
                request -> request.length == 2 && request[0].equals("/queue"),
                request -> {
                    queue.offer(request[1]);
                    return successCode();
                });
        dispatch.put(
                request -> request.length > 0 && request[0].equals("/queue/weather"),
                request -> {
                    StringBuilder builder = new StringBuilder(successCode());
                    try {
                        builder.append(queue.remove());
                    } catch (NoSuchElementException e) {
                        builder.append("Queue is empty");
                    }
                    return builder.toString();
                });
        dispatch.put(
                request -> request.length == 2 && request[0].equals("/topic"),
                request -> {
                    topic.offer(request[1]);
                    return successCode();
                });
        dispatch.put(
                request -> request.length > 0 && request[0].equals("/topic/weather"),
                request -> {
                    StringBuilder builder = new StringBuilder(successCode());
                    try {
                        builder.append(topic.remove());
                    } catch (NoSuchElementException e) {
                        builder.append("Queue is empty");
                    }
                    return builder.toString();
                });
    }

    public String doAction(String firstLineRequest) {
        String[] request = getRequest(firstLineRequest);
        for (Function<String[], Boolean> predict : this.dispatch.keySet()) {
            if (predict.apply(request)) {
                return this.dispatch.get(predict).apply(request);
            }
        }
        return errorCode();
    }

    /**
     * На вход принимает строки вида:
     * GET / HTTP/1.1
     * GET /?data=json HTTP/1.1
     * GET /queue?data=json HTTP/1.1
     * @param line стартовая строка HTTP запроса
     * @return возвращает массив содержащий один или два элемента, в первом элементе запрос
     * (например "/" или "/queue"), а во втором (при наличии) json для сохранения в очередь
     */
    private String[] getRequest(String line) {
        String requestPart = line.split(" ")[1];
        String[] rsl = requestPart.split("\\?");
        if (rsl.length == 2) {
            rsl[1] = rsl[1].split("=")[1];
        }
        return rsl;
    }

    private String errorCode() {
        return "HTTP/1.1 500 Internal Server Error\r\n\r\n";
    }

    private String successCode() {
        return "HTTP/1.1 200 OK\r\n\r\n";
    }
}