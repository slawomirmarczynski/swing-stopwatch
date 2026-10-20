package example.stopwatch;

/**
 * Kontrakt dla obiektów, które mają otrzymywać powiadomienia o zmianach
 * obserwowanego obiektu.
 *
 * <p>Implementacja określa, co należy zrobić po otrzymaniu powiadomienia.
 * W przypadku interfejsu graficznego jest to odświeżenie wyświetlanej wartości.</p>
 */
public interface ObjectObserver {

    /**
     * Wywoływana przez obserwowany obiekt, aby zasygnalizować zmianę stanu
     * wymagającą reakcji obserwatora.
     *
     * @param sender obiekt, którego zmiana wywołała powiadomienie
     */
    void update(ObjectObservable sender);
}
