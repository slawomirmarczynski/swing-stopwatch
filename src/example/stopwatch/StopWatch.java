package example.stopwatch;

import java.util.ArrayList;
import java.util.List;

/**
 * Model stopera. Przechowuje informację o tym, czy pomiar trwa, sumuje
 * zmierzone odcinki czasu oraz okresowo zleca interfejsowi odświeżenie widoku.
 *
 * <p>Pomiar bazuje na {@link System#nanoTime()}, który służy do mierzenia
 * upływu czasu monotonicznego. Wartość zwracana przez {@link #getValue()}
 * jest liczbą sekund z dokładnością do setnych części sekundy.</p>
 */
public class StopWatch implements Runnable, Publisher {
    private boolean running;
    private static final long GRANULARITY = 10; // w milisekundach
    private long begin; // w nanosekundach
    private long elapsedNanos; // suma zakończonych odcinków pomiaru
    private final List<Subscriber> subscriberList = new ArrayList<>();


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
            notifyAllSubscribers();
            try {
                //noinspection BusyWait
                Thread.sleep(GRANULARITY);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    /**
     * Rozpoczyna lub wznawia pomiar. Zakończone wcześniej odcinki czasu
     * pozostają uwzględnione w wyniku.
     */
    public void start() {
        if (!running) {
            begin = System.nanoTime();
            running = true;
        }
        notifyAllSubscribers();
    }

    /**
     * Zatrzymuje trwający pomiar i dodaje jego czas do sumy zmierzonych odcinków.
     * Jeżeli stoper już był zatrzymany, metoda nie zmienia stanu ani nie
     * wysyła dodatkowego powiadomienia.
     */
    public void stop() {
        if (running) {
            elapsedNanos += System.nanoTime() - begin;
            running = false;
            notifyAllSubscribers();
        }
    }

    /**
     * Zeruje pomiar. Stoper po tej operacji pozostaje zatrzymany.
     */
    public void reset() {
        running = false;
        elapsedNanos = 0;
        begin = System.nanoTime();
        notifyAllSubscribers();
     }

    @Override
    public void addSubscriber(Subscriber s) {
        if (!subscriberList.contains(s)) {
            subscriberList.add(s);
        }
    }

    @Override
    public void removeAllSubscribers() {
        subscriberList.clear();
    }

    private void notifyAllSubscribers(){
        for (Subscriber s : subscriberList) {
            s.update(this);
        }
    }

    /**
     * Oblicza aktualny lub ostatnio zmierzony czas w sekundach, z dokładnością
     * do setnych części sekundy.
     *
     * <p>Wynik obejmuje sumę zakończonych odcinków oraz, podczas działania,
     * czas od ostatniego wznowienia. Dzielenie przez liczbę nanosekund
     * w sekundzie obcina część mniejszą niż setna sekundy.</p>
     *
     * @return liczba sekund od rozpoczęcia pomiaru, zaokrąglona w dół do setnych
     */
    public Object getValue() {
        final long NANOSECONDS_PER_CENTISECOND = 10_000_000;
        long duration = elapsedNanos;
        if (running) {
            duration += System.nanoTime() - begin;
        }
        long centiseconds = duration / NANOSECONDS_PER_CENTISECOND;
        return (double) centiseconds / 100;
    }
}
