package ru.job4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class.getName());
    private static final ExecutorService POOL = Executors.newCachedThreadPool();
    private static final Properties CNF = new Properties();

    public static void main(String[] args) throws IOException {
        Broker broker = new Broker();
        try (InputStream in = Server.class.getClassLoader().getResourceAsStream(
                "server.properties"
        )) {
            CNF.load(in);
        }
        try (ServerSocket server = new ServerSocket(
                Integer.parseInt(CNF.getProperty("port"))
        )) {
            while (!server.isClosed()) {
                Socket socket = server.accept();
                POOL.execute(new MonoThreadClientHandler(socket, broker));
            }
            POOL.shutdown();
        } catch (IOException e) {
            LOG.error("Ошибка ", e);
        }
    }
}