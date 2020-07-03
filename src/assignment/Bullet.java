package assignment;

import java.awt.*;
public class Bullet {

    // Fields
    private double x;
    private double y;
    private int r;

    private double dx;
    private double dy;
    private double rad;
    private double speed;

    private Color color1;


    // Constructor
    public Bullet(double angle, double x, double y) {

        this.x = x;
        this.y = y;
        r = 4;

        speed = 10;
        dx = Math.cos(angle) * speed;
        dy = Math.sin(angle) * speed;



        color1 = Color.YELLOW;
    }

    // Functions

    public double getx() {return x; }
    public double gety() {return y; }
    public double getr() {return r; }

    public boolean update() {

        x += dx;
        y += dy;

        // Destroy bullets when they cross the game screen
        if(x < -r || x > Game.WIDTH + r ||
                y < -r || y > Game.HEIGHT + r) {
            return true;
        }
        return false;
    }

    public void draw (Graphics2D g) {

        g.setColor(color1);
        g.fillOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
    }
}

