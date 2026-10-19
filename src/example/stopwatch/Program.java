package example.stopwatch;

import com.formdev.flatlaf.FlatLightLaf;

public class Program implements Runnable {

    private UserInterface userInterface;
    private StopWatch stopWatch;

    @Override
    public void run() {
        stopWatch = new StopWatch();
        userInterface = new UserInterface();
        stopWatch.addObserver(userInterface);
        userInterface.setStopWatch(stopWatch);
        stopWatch.run();
        // Ponieważ stopWatch jest obiektem klasy implementującej Runnable
        // to można, zamiast po prostu wywołać run(), uruchomić działanie run()
        // jako osobny wątek.
        //
        //      Thread stopWatchThread = new Thread(stopWatch);
        //      stopWatchThread.setDaemon(true); // wskazane, ale nie niezbędne
        //      stopWatchThread.start();
    }

    static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "2");

        FlatLightLaf.setup();

        Program program = new Program();
        program.run();
    }
}
