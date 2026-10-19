package example.stopwatch;

import com.formdev.flatlaf.FlatLightLaf;

/**
 * Klasa uruchomieniowa aplikacji.
 *
 * <p>Łączy model stopera do pomiaru czasu z interfejsem graficznym, rejestruje
 * interfejs jako obserwatora obserwującego stoper i uruchamia stoper.
 * Klasa program, choć ma statyczną metodę main będącą punktem wejścia dla
 * programu, większość rzeczy realizuje w swojej metodzie run(), która nie jest
 * statyczna.</p>
 */
public class Program implements Runnable {
    private UserInterface userInterface;
    private StopWatch stopWatch;

    /**
     * Inicjalizuje elementy aplikacji i rozpoczyna działanie stopera.
     *
     * <p>Najpierw tworzony jest model, następnie interfejs, który zostaje
     * zarejestrowany jako obserwator modelu. Po przekazaniu interfejsowi
     * referencji do stopera model uruchamia się, co w istocie rozpoczyna
     * cykliczne powiadamianie obserwatorów.</p>
     */
    @Override
    public void run() {
        // Tworzony jest model stopera, a następnie okno z kontrolkami
        // do sterowania pomiarem. Rejestrowane jest okno jako obserwator modelu
        // mający dostęp do stopera. Dzięki temu przyciski mogą sterować
        // pomiarem, a wyświetlacz pokazywać wynik.
        //
        stopWatch = new StopWatch();
        userInterface = new UserInterface();
        stopWatch.addObserver(userInterface);
        userInterface.setStopWatch(stopWatch);

        // Rozpocznij pętlę cyklicznego powiadamiania obserwatorów.
        // Bezpośrednie wywołanie run() blokuje bieżący wątek.
        //
        stopWatch.run();
        //
        // Ponieważ StopWatch implementuje Runnable, można uruchomić go osobno,
        // jak pokazano poniżej. Powstaje w ten sposób nowy wątek do tego celu.
        //
        //      Thread stopWatchThread = new Thread(stopWatch);
        //      stopWatchThread.setDaemon(true);
        //      stopWatchThread.start();
    }

    /**
     * Punkt wejścia aplikacji: ustawia skalę interfejsu, wybiera jasny wygląd
     * FlatLaf, a następnie tworzy i uruchamia obiekt konfigurujący aplikację.
     *
     * @param args argumenty wiersza poleceń; w tej aplikacji nie są używane,
     *             ale chcieliśmy zachować pełną zgodność ze standardami,
     *             także tymi starszymi
     */
    public static void main(String[] args) {
        // Ustaw skalę renderowania elementów Swing, wybierz jasny motyw,
        // a następnie utwórz i uruchom obiekt koordynujący aplikację.
        //
        System.setProperty("sun.java2d.uiScale", "2");
        FlatLightLaf.setup();
        Program program = new Program();
        program.run();
    }
}
