package example.stopwatch;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class UserInterface implements ObjectObserver {

    final static int horizontalGrids = 3;
    final static int verticalGrids = 2;
    final static int insetSize = 5;

    private JFrame frame;
    private JTextField display;
    private StopWatch stopWatch;

    UserInterface() {
        try {
            EventQueue.invokeAndWait(() -> {
                createMainWindow();
                addDisplay();
                addButton("Start", 1, 0, ()->stopWatch.start());
                addButton("Stop", 1, 1, ()->stopWatch.stop());
                addButton("Reset", 1, 2, ()->stopWatch.reset());
                showMainWindow();
            });
        } catch (InterruptedException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void update() {
        EventQueue.invokeLater(() -> {
            display.setText(stopWatch.toString());
        });
    }

    public void setStopWatch(StopWatch stopWatch) {
        this.stopWatch = stopWatch;
    }

    private void showMainWindow() {
        frame.pack();
        frame.setVisible(true);
    }

    private void createMainWindow() {
        frame = new JFrame();
        frame.setTitle("Stopwatch");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new GridBagLayout());
    }

    private void addDisplay() {
        display = new JTextField(10);
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
