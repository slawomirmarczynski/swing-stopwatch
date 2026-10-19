package example.stopwatch;

import java.util.ArrayList;
import java.util.List;

/**
 * Model stopera. Przechowuje informację o tym, czy pomiar trwa, zapamiętuje
 * jego granice czasowe oraz okresowo powiadamia zarejestrowanych obserwatorów.
 *
 * <p>Pomiar bazuje na {@link System#nanoTime()}, który służy do mierzenia
 * upływu czasu monotonicznego. Wartość zwracana przez {@link #getValue()}
 * jest liczbą całkowitych sekund.</p>
 */
public class StopWatch implements Runnable {
    private boolean running;
    private final long granularity = 100; // w milisekundach
    private long begin; // w nanosekundach
    private long end; // nanosekundach

    // Lista obiektów, które mają być informowane o zmianie stanu stopera.
    //
    private final List<ObjectObserver> observers = new ArrayList<>();

    /**
     * Cyklicznie informuje obserwatorów o wartości stopera.
     *
     * <p>Pętla działa do momentu, gdy wątek zostanie oznaczony jako przerwany.
     * Między powiadomieniami usypia na czas określony przez {@link #granularity}.
     * W razie przerwania podczas uśpienia wyjątek jest obecnie ignorowany.</p>
     */
    @Override
    public void run() {
        // W pętli informuj obserwatorów o aktualnej wartości, po czym uśpij
        // wątek na ustalony czas, aby ograniczyć częstotliwość odświeżeń.
        // Pętla kończy się, gdy wątek zostanie oznaczony jako przerwany.
        //
        while (!Thread.currentThread().isInterrupted()) {
            notifyObservers();
            try {
                Thread.sleep(granularity);
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
        notifyObservers();
    }

    /**
     * Zatrzymuje trwający pomiar i zapamiętuje chwilę jego zakończenia.
     * Jeżeli stoper już był zatrzymany, metoda nie zmienia stanu ani nie
     * wysyła dodatkowego powiadomienia.
     */
    public void stop() {
        // Zatrzymaj pomiar tylko wtedy, gdy jest aktywny: zapamiętaj jego koniec
        // i od razu powiadom obserwatorów, by widok nie czekał na kolejny cykl.
        //
        if (running) {
            running = false;
            end = System.nanoTime();
            notifyObservers();
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
    }

    /** Wywołuje {@link ObjectObserver#update()} u każdego zarejestrowanego obserwatora. */
    private void notifyObservers() {
        // Przejdź po zarejestrowanych obserwatorach i wywołaj ich metodę
        // update(), pozostawiając każdemu z nich obsługę powiadomienia.
        //
        for (ObjectObserver observer : observers) {
            observer.update();
        }
    }

    /**
     * Dodaje obserwatora do listy odbiorców powiadomień.
     *
     * @param observer obiekt, który ma otrzymywać powiadomienia
     */
    public void addObserver(ObjectObserver observer) {
        // Dodaj obiekt do listy, z której korzysta metoda notifyObservers().
        observers.add(observer);
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
    public Object getValue() {
        // Ustal przelicznik nanosekund na sekundy i oblicz czas trwania:
        // dla aktywnego pomiaru względem bieżącej chwili, a dla zatrzymanego
        // względem zapamiętanego końca. Dzielenie całkowitoliczbowe odrzuca
        // niepełną część sekundy.

        final long nanosecondsPerSeconds = 1_000_000_000;
        long duration;
        if (running) {
            duration = System.nanoTime() - begin;
        } else {
            duration = end - begin;
        }
        return duration / nanosecondsPerSeconds;
    }
}
