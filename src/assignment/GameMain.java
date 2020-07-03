package assignment;

import javax.swing.JFrame;

public class GameMain {

    public static void main(String[] args) {
        JFrame window = new JFrame("Space Video Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setContentPane(new Game());
        window.pack();
        window.setVisible(true);
    }
}

