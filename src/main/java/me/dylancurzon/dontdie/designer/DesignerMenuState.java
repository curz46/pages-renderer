package me.dylancurzon.dontdie.designer;

import me.dylancurzon.dontdie.GameState;
import me.dylancurzon.dontdie.tile.Level;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class DesignerMenuState implements GameState {

    private final LevelDesigner designer;
    private JFrame frame;

    public DesignerMenuState(LevelDesigner designer) {
        this.designer = designer;
    }

    @Override
    public void start() {
        frame = new JFrame("Level Designer");

        Dimension dim = new Dimension(250, 100);
        frame.setMinimumSize(dim);
        frame.setPreferredSize(dim);
        frame.setMaximumSize(dim);
        frame.setResizable(false);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(1, 2));

        JPanel panel1 = new JPanel(new GridBagLayout());
        JButton button1 = new JButton("Choose Level");
        button1.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int option = chooser.showOpenDialog(panel1);
            if (option != JFileChooser.APPROVE_OPTION) return;

            File file = chooser.getSelectedFile();
            if (file == null) throw new RuntimeException("file == null");
            Level level = Level.fromFile(file);
//            (new LevelDesigner(level)).start();
            designer.setGameState(new DesignerLevelState(designer, level));
        });

        panel1.add(button1);

        JPanel panel2 = new JPanel(new GridBagLayout());
        JButton button2 = new JButton("Create Level");
        button2.addActionListener(e -> {
            Level level = new Level();
//            (new LevelDesigner(level)).start();
            designer.setGameState(new DesignerLevelState(designer, Level.generateTestLevel()));
        });

        panel2.add(button2);

        panel.add(panel1);
        panel.add(panel2);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(panel);

        frame.setVisible(true);
    }

    @Override
    public void finish() {
        frame.dispose();
        frame = null;
        // TODO: This is required in order to stop the program crashing when using JFrame and LWJGL in the same process.
        System.gc();
    }

}
