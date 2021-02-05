package ru.job4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServer {
    private static final Logger LOG = LoggerFactory.getLogger(EchoServer.class.getName());

    public static void main(String[] args) {
        ServerAction serverAction = new ServerAction();
        ExecutorService pool = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()
        );
        try (ServerSocket server = new ServerSocket(9000)) {
            while (true) {
                Socket socket = server.accept();
                pool.execute(() -> {
                    try (OutputStream out = socket.getOutputStream();
                         BufferedReader in = new BufferedReader(
                                 new InputStreamReader(socket.getInputStream()))) {
                        String firstLineRequest = in.readLine();
                        String answer = serverAction.doAction(firstLineRequest);
                        out.write(answer.getBytes());
                    } catch (IOException e) {
                        LOG.error("Error", e);
                    }
                });
            }
        } catch (IOException e) {
            LOG.error("Error", e);
        }
        pool.shutdown();
    }
}