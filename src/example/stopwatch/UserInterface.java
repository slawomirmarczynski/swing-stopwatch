package example.stopwatch;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class UserInterface implements Runnable {

    private static final int horizontalGrids = 3;
    private static final int insetSize = 10;
    private static final int uiScale = 2;

    private final Object updateLock = new Object();
    private final StopWatch stopWatch;
    private final Thread applicationThread;

    private Display swtDisplay;
    private Shell shell;
    private Font scaledFont;
    private Text display;
    private boolean closed;

    UserInterface(StopWatch stopWatch) {
        this.stopWatch = stopWatch;
        applicationThread = Thread.currentThread();
        stopWatch.userInterface = this;

        Thread uiThread = new Thread(this, "stopwatch-swt-ui");
        uiThread.start();
    }

    /**
     * Główna pętla obsługi zdarzeń SWT.
     */
    public void run() {
        try {
            synchronized (updateLock) {
                swtDisplay = new Display();
                FontData[] fontData = swtDisplay.getSystemFont().getFontData();
                for (FontData data : fontData) {
                    data.setHeight(data.getHeight() * uiScale);
                }
                scaledFont = new Font(swtDisplay, fontData);

                shell = new Shell(swtDisplay, SWT.SHELL_TRIM | SWT.RESIZE);
                shell.setFont(scaledFont);
                createMainWindow();
                addDisplay();

                addButton("Start", stopWatch::start);
                addButton("Stop", stopWatch::stop);
                addButton("Reset", stopWatch::reset);

                display.setText(stopWatch.getValue() + " seconds");
                showMainWindow();
            }

            while (!shell.isDisposed()) {
                if (!swtDisplay.readAndDispatch()) {
                    swtDisplay.sleep();
                }
            }
        } finally {
            synchronized (updateLock) {
                closed = true;
            }
            if (shell != null && !shell.isDisposed()) {
                shell.dispose();
            }
            if (scaledFont != null && !scaledFont.isDisposed()) {
                scaledFont.dispose();
            }
            if (swtDisplay != null && !swtDisplay.isDisposed()) {
                swtDisplay.dispose();
            }
            applicationThread.interrupt();
        }
    }

    /**
     * Aktualizacja wyświetlacza z bezpiecznym przejściem do wątku GUI.
     */
    public void update() {
        synchronized (updateLock) {
            if (closed || swtDisplay == null || display == null) {
                return;
            }

            swtDisplay.asyncExec(() -> {
                if (!display.isDisposed()) {
                    display.setText(stopWatch.getValue() + " seconds");
                }
            });
        }
    }

    private void createMainWindow() {
        shell.setText("Stopwatch");

        GridLayout layout = new GridLayout(horizontalGrids, true);
        layout.marginWidth = insetSize;
        layout.marginHeight = insetSize;
        layout.horizontalSpacing = insetSize;
        layout.verticalSpacing = insetSize;

        shell.setLayout(layout);
    }

    private void showMainWindow() {
        shell.pack();
        shell.setMinimumSize(shell.getSize());
        shell.open();
    }

    private void addDisplay() {
        final int fieldSizeInCharacters = 10;

        display = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);

        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        data.horizontalSpan = horizontalGrids;
        data.widthHint = fieldSizeInCharacters * 20;

        display.setLayoutData(data);
        display.setFont(scaledFont);
    }

    private void addButton(String name, Runnable action) {
        Button button = new Button(shell, SWT.PUSH);
        button.setText(name);
        button.setFont(scaledFont);

        button.setLayoutData(
                new GridData(SWT.FILL, SWT.CENTER, true, false));

        button.addListener(SWT.Selection, event -> action.run());
    }
}
