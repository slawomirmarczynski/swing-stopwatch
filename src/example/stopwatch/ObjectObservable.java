package example.stopwatch;

/**
 * Kontrakt dla obiektów, których wartość może być odczytywana przez obserwatorów.
 */
public interface ObjectObservable {
    /**
     * Zwraca bieżącą wartość obserwowanego obiektu.
     *
     * @return wartość obiektu
     */
    Object getValue();
}
