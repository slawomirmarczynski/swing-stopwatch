package example.stopwatch;

import java.util.ArrayList;
import java.util.List;

public class StopWatch implements Runnable {

    private boolean running;
    private final long granularity = 100;
    private long begin;
    private long end;
    private final List<ObjectObserver> observers = new ArrayList<>();

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            notifyObservers();
            try {
                Thread.sleep(granularity);
            } catch (InterruptedException e) {
            }
        }
    }

    public void start() {
        running = true;
        begin = System.nanoTime();
        end = begin;
    }

    public void stop() {
        if (running) {
            running = false;
            end = System.nanoTime();
            notifyObservers();
        }
    }

    public void reset() {
        running = false;
        begin = System.nanoTime();
        end = begin;
    }

    private void notifyObservers() {
        for (ObjectObserver observer : observers) {
            observer.update();
        }
    }

    public synchronized void addObserver(ObjectObserver observer) {
        observers.add(observer);
    }

    @Override
    public String toString() {
        long duration;
        if (running) {
            duration = System.nanoTime() - begin;
        } else {
            duration = end - begin;
        }
        return Long.toString(duration / 1_000_000_000);
    }
}
