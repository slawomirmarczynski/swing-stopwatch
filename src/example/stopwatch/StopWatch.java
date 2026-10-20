package example.stopwatch;

/**
 * Model stopera. Przechowuje informację o tym, czy pomiar trwa, zapamiętuje
 * jego granice czasowe oraz okresowo zleca interfejsowi odświeżenie widoku.
 *
 * <p>Pomiar bazuje na {@link System#nanoTime()}, który służy do mierzenia
 * upływu czasu monotonicznego. Wartość zwracana przez {@link #getValue()}
 * jest liczbą całkowitych sekund.</p>
 */
public class StopWatch implements Runnable {
    private boolean running;
    private static final long GRANULARITY = 100; // w milisekundach
    private long begin; // w nanosekundach
    private long end; // w nanosekundach

    public UserInterface userInterface;

    /**
     * Cyklicznie zleca interfejsowi odświeżenie wyświetlanej wartości stopera.
     *
     * <p>Pętla działa do przerwania bieżącego wątku. Między aktualizacjami
     * usypia na czas określony przez {@link #GRANULARITY}. Przerwanie podczas
     * uśpienia kończy pętlę.</p>
     */
    @Override
    public void run() {
        // Zlecaj interfejsowi odświeżanie, a między aktualizacjami usypiaj
        // wątek. Przerwanie bieżącego wątku kończy pętlę.
        //
        while (!Thread.currentThread().isInterrupted()) {
            userInterface.update();
            try {
                Thread.sleep(GRANULARITY);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    /**
     * Rozpoczyna nowy pomiar od bieżącej chwili. Poprzednia wartość nie jest
     * zachowywana — początek i koniec pomiaru ustawiane są na ten sam moment.
     */
    public void start() {
        // Oznacz pomiar jako aktywny i zapamiętaj aktualny czas jako jego początek.
        // Ustawienie końca na tę samą chwilę oznacza, że początkowy wynik wynosi zero.
        //
        running = true;
        begin = System.nanoTime();
        end = begin;
        userInterface.update();
    }

    /**
     * Zatrzymuje trwający pomiar i zapamiętuje chwilę jego zakończenia.
     * Jeżeli stoper już był zatrzymany, metoda nie zmienia stanu ani nie
     * wysyła dodatkowego powiadomienia.
     */
    public void stop() {
        // Zatrzymaj pomiar tylko wtedy, gdy jest aktywny: zapamiętaj jego koniec
        // i od razu zleć odświeżenie widoku, bez czekania na kolejny cykl.
        //
        if (running) {
            running = false;
            end = System.nanoTime();
            userInterface.update();
        }
    }

    /**
     * Zeruje pomiar, ustawiając jego początek i koniec na bieżącą chwilę.
     * Stoper po tej operacji pozostaje zatrzymany.
     */
    public void reset() {
        // Zakończ ewentualny pomiar i ustaw jego początek oraz koniec
        // na tę samą bieżącą chwilę, co daje wyzerowany wynik.
        //
        running = false;
        begin = System.nanoTime();
        end = begin;
        userInterface.update();
    }

    /**
     * Oblicza aktualny lub ostatnio zmierzony czas w pełnych sekundach.
     *
     * <p>Podczas działania różnica liczona jest względem bieżącego czasu,
     * a po zatrzymaniu względem zapamiętanej chwili zakończenia. Dzielenie
     * przez liczbę nanosekund w sekundzie obcina część ułamkową.</p>
     *
     * @return liczba całkowitych sekund od rozpoczęcia pomiaru
     */
    public long getValue() {
        // Ustal przelicznik nanosekund na sekundy i oblicz czas trwania:
        // dla aktywnego pomiaru względem bieżącej chwili, a dla zatrzymanego
        // względem zapamiętanego końca. Dzielenie całkowitoliczbowe odrzuca
        // niepełną część sekundy.

        final long NANOSECONDS_PER_SECONDS = 1_000_000_000;
        long duration;
        if (running) {
            duration = System.nanoTime() - begin;
        } else {
            duration = end - begin;
        }
        return duration / NANOSECONDS_PER_SECONDS;
    }
}
