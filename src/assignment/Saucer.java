package assignment;


import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Random;

public class Saucer {

    // Fields

    Random rand = new Random();
    double rotRate;
    double rot;

    ///////////////////////
    private int x;
    private int y;
    private int r;
    private Vector2D position;
    private Vector2D velocity;

    private int dx;
    private int dy;
    private double rad;
    private double speed;

    private int health;

    private Color color1;

    private boolean ready;
    private boolean dead;

    private boolean hit;
    private long hitTimer;

    private boolean slow;

    private boolean space;
    private long firingTimer;
    private long firingDelay = 400;

    private Ship ship;
    private Vector2D target;

    public Saucer() {
        speed = 2;
        r = 25;
        health = 1;

        position = new Vector2D(x, y);

        position.x = (Math.random() * Game.WIDTH / 2 + Game.WIDTH) * 25;
        position.y = -r + Math.random() * 22 + Math.random() * 43;

        double angle = Math.random() * 140 + 20;
        rad = Math.toRadians(angle);

        velocity = new Vector2D(dx, dy);

        velocity.add(Math.cos(rad) * speed,Math.sin(rad) * speed);

        ready = false;
        dead = false;

        hit = false;
        hitTimer = 0;
        color1 = Color.PINK;

        space = true;

        rotRate = (rand.nextDouble() - 0.5) * Math.PI / 20;
        rot = 0;

        ship = new Ship();
       target = ship.position;
    }

    public double getx() {return position.x; }
    public double gety() {return position.y; }
    public int getr() {return r; }

    public void hit() {
        health--;
        if (health <= 0) {
            dead = true;
        }
        hit = true;
        hitTimer = System.nanoTime();
    }

    public boolean isDead() { return dead; }

    public boolean update() {
        position.add(velocity).wrap(Game.WIDTH, Game.HEIGHT);
        rot += rotRate;


        if(space) {
            long elapsed = (System.nanoTime() - firingTimer) / 1000000;

            if(elapsed > firingDelay) {
                firingTimer = System.nanoTime();
                //SoundManager.fire();
                SoundManager.saucer();

                Game.enemyBullets.add(new Bullet(target.angle(), position.x, position.y));
                target.rotate(rot);
            }
        }

        if(hit) {
            long elapsed = (System.nanoTime() - hitTimer) / 1000000;
            if(elapsed > 50) {
                hit = false;
                hitTimer = 0;
            }
        }

        if(isDead()) {
            return true;
        }
        else
            return false;

    }

    public void draw(Graphics2D g) {
        g.setStroke(new BasicStroke(3));
        g.setColor(color1.darker());
        g.fill(new Ellipse2D.Double((int) (position.x - r), (int) (position.y -r), 2 * r,  r));
        g.setStroke(new BasicStroke(1));

        g.setColor(Color.YELLOW.darker());
        g.setStroke(new BasicStroke(4));
        g.drawLine((int)position.x - r, (int)position.y - (r / 2), (int)position.x + r, (int)position.y - (r / 2));
    }

}
