package example.stopwatch;

import com.formdev.flatlaf.FlatLightLaf;

/**
 * Klasa uruchomieniowa aplikacji. Łączy model stopera z interfejsem graficznym,
 * rejestruje interfejs jako obserwatora i rozpoczyna cykliczne odświeżanie.
 */
public class Program implements Runnable {

    /** Interfejs pozwalający użytkownikowi sterować stoperem i obserwować jego wartość. */
    private UserInterface userInterface;

    /** Obiekt przechowujący stan stopera i udostępniający operacje sterujące. */
    private StopWatch stopWatch;

    /**
     * Inicjalizuje elementy aplikacji i rozpoczyna działanie stopera.
     *
     * <p>Najpierw tworzony jest model, następnie interfejs, który zostaje
     * zarejestrowany jako obserwator modelu. Po przekazaniu interfejsowi
     * referencji do stopera model rozpoczyna cykliczne powiadamianie
     * obserwatorów.</p>
     */
    @Override
    public void run() {
        // Utwórz model stopera, a następnie okno z kontrolkami do sterowania pomiarem.
        stopWatch = new StopWatch();
        userInterface = new UserInterface();

        // Zarejestruj okno jako obserwatora modelu i przekaż mu dostęp do stopera.
        // Dzięki temu przyciski mogą sterować pomiarem, a wyświetlacz odczytywać wynik.
        stopWatch.addObserver(userInterface);
        userInterface.setStopWatch(stopWatch);

        // Rozpocznij pętlę cyklicznego powiadamiania obserwatorów.
        // Bezpośrednie wywołanie run() blokuje bieżący wątek. Ponieważ StopWatch
        // implementuje Runnable, można uruchomić go osobno, jak pokazano poniżej.
        stopWatch.run();
        //
        //      Thread stopWatchThread = new Thread(stopWatch);
        //      stopWatchThread.setDaemon(true);
        //      stopWatchThread.start();
    }

    /**
     * Punkt wejścia aplikacji: ustawia skalę interfejsu, wybiera jasny wygląd
     * FlatLaf, a następnie tworzy i uruchamia obiekt konfigurujący aplikację.
     *
     * @param args argumenty wiersza poleceń; w tej aplikacji nie są używane
     */
    static void main(String[] args) {
        // Ustaw skalę renderowania elementów Swing, wybierz jasny motyw,
        // a następnie utwórz i uruchom obiekt koordynujący aplikację.
        System.setProperty("sun.java2d.uiScale", "2");
        FlatLightLaf.setup();
        Program program = new Program();
        program.run();
    }
}
