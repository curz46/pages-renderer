package me.dylancurzon.dontdie.designer;

import me.dylancurzon.dontdie.GameState;
import me.dylancurzon.dontdie.tile.Level;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class DesignerMenuState implements GameState {

    private final LevelDesigner designer;
    private JFrame frame;

    public DesignerMenuState(final LevelDesigner designer) {
        this.designer = designer;
    }

    @Override
    public void start() {
        this.frame = new JFrame("Level Designer");

        final Dimension dim = new Dimension(250, 100);
        this.frame.setMinimumSize(dim);
        this.frame.setPreferredSize(dim);
        this.frame.setMaximumSize(dim);
        this.frame.setResizable(false);

        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JPanel panel = new JPanel(new GridLayout(1, 2));

        final JPanel panel1 = new JPanel(new GridBagLayout());
        final JButton button1 = new JButton("Choose Level");
        button1.addActionListener(e -> {
            final JFileChooser chooser = new JFileChooser();
            final int option = chooser.showOpenDialog(panel1);
            if (option != JFileChooser.APPROVE_OPTION) return;

            final File file = chooser.getSelectedFile();
            if (file == null) throw new RuntimeException("file == null");
            final Level level = Level.fromFile(file);
//            (new LevelDesigner(level)).start();
            this.designer.setGameState(new DesignerLevelState(this.designer, level));
        });

        panel1.add(button1);

        final JPanel panel2 = new JPanel(new GridBagLayout());
        final JButton button2 = new JButton("Create Level");
        button2.addActionListener(e -> {
            final Level level = new Level();
//            (new LevelDesigner(level)).start();
            this.designer.setGameState(new DesignerLevelState(this.designer, Level.generateTestLevel()));
        });

        panel2.add(button2);

        panel.add(panel1);
        panel.add(panel2);

        this.frame.pack();
        this.frame.setLocationRelativeTo(null);
        this.frame.getContentPane().add(panel);

        this.frame.setVisible(true);
    }

    @Override
    public void finish() {
        this.frame.dispose();
        this.frame = null;
        // TODO: This is required in order to stop the program crashing when using JFrame and LWJGL in the same process.
        System.gc();
    }

}
