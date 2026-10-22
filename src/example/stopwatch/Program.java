package example.stopwatch;

import javax.swing.*;

/**
 * Klasa uruchomieniowa aplikacji.
 *
 * <p>Łączy model stopera z interfejsem graficznym i uruchamia cykliczne
 * odświeżanie widoku. Metoda {@link #main(String[])} jest punktem wejścia
 * aplikacji, a pozostała inicjalizacja odbywa się w niestatycznej metodzie
 * {@link #run()}.</p>
 */
public class Program implements Runnable {
    private UserInterface userInterface;
    private StopWatch stopWatch;

    /**
     * Inicjalizuje elementy aplikacji i rozpoczyna działanie stopera.
     *
     * <p>Tworzy model i interfejs, który przechowuje jego referencję. Model
     * otrzymuje z kolei referencję do interfejsu, aby zlecać mu odświeżanie
     * widoku. Następnie uruchamia pętlę cyklicznych aktualizacji.</p>
     */
    @Override
    public void run() {
        // Utwórz model stopera i interfejs z kontrolkami. Interfejs przechowuje
        // referencję do modelu, a model do interfejsu, by móc zlecać odświeżanie.
        //
        stopWatch = new StopWatch();
        userInterface = new UserInterface(stopWatch);

        // Rozpocznij pętlę cyklicznego odświeżania interfejsu.
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
     * @param args argumenty wiersza poleceń; nie są używane przez aplikację
     */
    public static void main(String[] args) {
        // Ustaw skalę renderowania elementów Swing, wybierz jasny motyw,
        // a następnie utwórz i uruchom obiekt koordynujący aplikację.
        //
        System.setProperty("sun.java2d.uiScale", "2");
        setupLookAndFeel();
        Program program = new Program();
        program.run();
    }

    /**
     * Ustalanie look-and-feel (tzw. laf), czyli jaki mają wygladać kontrolki -
     * czy mają przypominać te znane z MS Windows, czy raczej takie jakie są na
     * komputerach Apple, czy może jeszcze inne?!
     */
    private static void setupLookAndFeel() {
        // Tablica zawierająca preferowane i tablica zawierające dostępne LaF.
        // Są one final, bo nie będą modyfikowane po utworzeniu.
        //
        final String[] preferredNames = {"Windows", "Nimbus"};
        final UIManager.LookAndFeelInfo[] installed = UIManager.getInstalledLookAndFeels();

        // Negocjowanie jaki LaF ma być użyty - ponieważ dostępnych LaF jest
        // niewiele (kilka, może kilkanaście) i niewiele jest preferowanych LaF
        // - to użycie dwóch pętli for (jak poniżej) nie jest aż tak złe... jak
        // mogłoby się to wydawać. Przechwytywanie wyjątków (try-catch) jest
        // konieczne, ale - poza odnotowaniem że coś się dzieje - nie wymaga
        // szczególnych kroków - w najgorszym razie nic się nie uda i pozostanie
        // standardowy wygląd kontrolek - co jest dobrym rozwiązaniem.
        //
        try {
            for (String bestName : preferredNames) {
                for (UIManager.LookAndFeelInfo available : installed) {
                    if (available.getName().equals(bestName)) {
                        UIManager.setLookAndFeel(available.getClassName());
                        return;
                    }
                }
            }
        } catch (Exception exception) {
            // ignorujemy wyjątek
        }
    }
}