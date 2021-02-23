package ru.job4j;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MonoThreadClientHandler implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(
            MonoThreadClientHandler.class.getName()
    );
    private final Socket socket;
    private Broker broker;

    public MonoThreadClientHandler(Socket socket, Broker broker) {
        this.socket = socket;
        this.broker = broker;
    }

    @Override
    public void run() {
        try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream());
             socket) {
            JsonObject req = new Gson().fromJson(in.readUTF(), JsonObject.class);
            JsonObject resp = broker.process(req);
            LOG.debug(resp.toString());
            out.writeUTF(resp.toString());
        } catch (IOException e) {
            LOG.error("Ошибка ", e);
        }
    }
}
