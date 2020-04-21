package RubiksCube;

import javax.swing.*;
import java.awt.*;

public class ShuffleBar extends JPanel {

    private Cube cube;
    private JButton shuffleButton;
    private JButton resetButton;

    public ShuffleBar(Cube cube) {
        this.cube = cube;
        setLayout(new FlowLayout());
        addResetButton();
        addShuffleButton();
    }

    private void addResetButton() {
        resetButton = new JButton(Move.RESET.getSymbol());
        resetButton.addActionListener(e -> cube.reset());
        add(resetButton);
    }

    private void addShuffleButton() {
        shuffleButton = new JButton(Move.SHUFFLE.getSymbol());
        shuffleButton.addActionListener(e -> cube.shuffle());
        add(shuffleButton);
    }
}
