package ru.job4j;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new Thread(new Consumer("topic", "weather.Moscow")).start();
        new Thread(new Consumer("topic", "*.Moscow")).start();
        new Thread(new Producer("topic", "weather.Moscow")).start();
        new Thread(new Producer("queue", "weather")).start();
        new Thread(new Consumer("queue", "weather")).start();
    }
}
