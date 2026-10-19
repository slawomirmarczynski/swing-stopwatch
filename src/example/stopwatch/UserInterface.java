package example.stopwatch;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Okno graficzne stopera. Klasa tworzy interfejs Swing, przekazuje polecenia
 * użytkownika do obiektu {@link StopWatch} i wyświetla aktualną wartość stopera.
 *
 * <p>Komponenty Swing nie są bezpieczne przy równoczesnym dostępie z wielu
 * wątków. Dlatego tworzenie okna odbywa się w wątku obsługi zdarzeń Swing
 * (EDT), a aktualizacje wyświetlacza są do tego wątku zlecane.</p>
 */
public class UserInterface implements ObjectObserver {

    private final static int horizontalGrids = 3;
    //    private final static int verticalGrids = 2;
    private final static int insetSize = 5;

    private JFrame frame;
    private JTextField display;
    private StopWatch stopWatch;

    /**
     * Tworzy okno, wyświetlacz i przyciski sterujące.
     *
     * <p>Cała konfiguracja jest wykonywana w EDT. {@code invokeAndWait}
     * sprawia, że konstruktor wraca dopiero po utworzeniu interfejsu,
     * dzięki czemu dalsza inicjalizacja może korzystać z gotowych komponentów.</p>
     */
    UserInterface() {
        try {
            // W EDT utwórz okno, dodaj wyświetlacz i przyciski, a następnie
            // dopasuj rozmiar okna i pokaż gotowy interfejs.
            //
            EventQueue.invokeAndWait(() -> {
                createMainWindow();
                addDisplay();
                addButton("Start", 1, 0, () -> stopWatch.start());
                addButton("Stop", 1, 1, () -> stopWatch.stop());
                addButton("Reset", 1, 2, () -> stopWatch.reset());
                showMainWindow();
            });
        } catch (InterruptedException | InvocationTargetException exception) {
            // Zgłoś błąd inicjalizacji jako wyjątek czasu wykonania, zamiast
            // kontynuować działanie aplikacji bez poprawnie utworzonego okna.
            throw new RuntimeException(exception);
        }
    }

    /**
     * Zapamiętuje obiekt stopera obsługiwany przez interfejs.
     *
     * @param stopWatch stoper, którego metody wywołują przyciski i którego
     *                  aktualna wartość jest prezentowana w oknie
     */
    public void setStopWatch(StopWatch stopWatch) {
        // Zapamiętaj model wykorzystywany przez akcje przycisków
        // oraz przez kod odświeżający wyświetlaną wartość.
        this.stopWatch = stopWatch;
    }

    /**
     * Powiadamia interfejs o zmianie stanu obserwowanego obiektu i odświeża
     * wyświetlaną wartość.
     *
     * <p>Odświeżenie jest dodawane do kolejki EDT, zamiast wykonywać operację
     * bezpośrednio w wątku, z którego przyszło powiadomienie. Dzięki temu
     * modyfikacja komponentu Swing odbywa się w bezpiecznym dla niego wątku.</p>
     */
    public void update() {
        // Zleć aktualizację wątkowi EDT, a w nim pobierz bieżącą wartość modelu
        // i ustaw ją jako tekst wyświetlacza.
        EventQueue.invokeLater(() -> display.setText(stopWatch.getValue().toString()));
    }

    private void createMainWindow() {
        // Utwórz główne okno, ustaw jego tytuł i zachowanie przy zamykaniu,
        // zablokuj zmianę rozmiaru oraz wybierz GridBagLayout jako układ.
        frame = new JFrame();
        frame.setTitle("Stopwatch");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new GridBagLayout());
    }

    private void showMainWindow() {
        // Dopasuj rozmiar okna do dodanych komponentów, a następnie je wyświetl.
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Tworzy pole tekstowe pokazujące czas i umieszcza je w pierwszym wierszu.
     * Pole jest tylko do odczytu, a wyrównanie do prawej ułatwia obserwowanie
     * zmieniających się cyfr.
     */
    private void addDisplay() {
        final int fieldSizeInCharacters = 10;
        display = new JTextField(fieldSizeInCharacters);
        display.setEditable(false);
        display.setHorizontalAlignment(JTextField.RIGHT);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(insetSize, insetSize, insetSize, insetSize);
        constraints.gridwidth = horizontalGrids;
        constraints.gridx = 0;
        constraints.gridy = 0;
        frame.add(display, constraints);
    }

    /**
     * Dodaje przycisk do wskazanego wiersza i kolumny okna.
     *
     * @param name   tekst widoczny na przycisku
     * @param row    indeks wiersza w układzie siatki
     * @param column indeks kolumny w układzie siatki
     * @param action operacja wykonywana po kliknięciu przycisku
     */
    private void addButton(String name, int row, int column, Runnable action) {
        JButton button = new JButton(name);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(insetSize, insetSize, insetSize, insetSize);
        constraints.gridy = row;
        constraints.gridx = column;
        frame.add(button, constraints);
        button.addActionListener(event -> action.run());
    }
}
