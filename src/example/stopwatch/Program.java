package example.stopwatch;

import com.formdev.flatlaf.FlatIntelliJLaf;

public class Program implements Runnable {

    private UserInterface userInterface;
    private StopWatch stopWatch;

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "2");

        FlatIntelliJLaf.setup();

        Program program = new Program();
        program.run();
    }

    @Override
    public void run() {
            stopWatch = new StopWatch();
            userInterface = new UserInterface();
            stopWatch.addObserver(userInterface);
            userInterface.setStopWatch(stopWatch);
            Thread stopWatchThread = new Thread(stopWatch);
            stopWatchThread.setDaemon(true);
            stopWatchThread.start();
    }
}
