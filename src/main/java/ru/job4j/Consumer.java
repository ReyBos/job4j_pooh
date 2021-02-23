package ru.job4j;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Properties;

public class Consumer implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(Consumer.class.getName());
    private final Socket socket;
    private final JsonObject json = new JsonObject();

    public Consumer(String mode, String key) throws IOException {
        Properties cnf = new Properties();
        try (InputStream in = Consumer.class.getClassLoader().getResourceAsStream(
                "server.properties"
        )) {
            cnf.load(in);
        }
        socket = new Socket(
                cnf.getProperty("server_url"), Integer.parseInt(cnf.getProperty("port"))
        );
        json.addProperty("action", "GET");
        json.addProperty("mode", mode);
        json.addProperty("key", key);
    }

    @Override
    public void run() {
        try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream());
             socket) {
            out.writeUTF(json.toString());
            out.flush();
            JsonObject resp = new Gson().fromJson(in.readUTF(), JsonObject.class);
            LOG.debug(resp.get("text").getAsString());
        } catch (IOException e) {
            LOG.error("Ошибка ", e);
        }
    }
}
