package assignment;

import java.util.Random;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class Asteroid {

    // Fields

    int[] px, py;
    static int nPoints = 16;
    Random rand = new Random();
    static double radialRange = 0.6;
    double rotRate;
    double rot;

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
    private int type;
    private int rank;

    private Color color1;

    private boolean ready;
    private boolean dead;

    private boolean hit;
    private long hitTimer;

    private boolean slow;


    public Asteroid(int type, int rank) {
        this.type = type;
        this.rank = rank;

        rotRate = (rand.nextDouble() - 0.5) * Math.PI / 20;
        rot = 0;

        // Default Asteroid
        if (type == 1) {
            color1 = new Color(0, 0, 255, 128);
            if (rank == 1) {
                speed = 2;
                r = 15;
                health = 1;
            }
            if (rank == 2) {
                speed = 2;
                r = 20;
                health = 2;
            }
            if (rank == 3) {
                speed = 1.5;
                r = 30;
                health = 3;
            }
        }


            // Stronger and faster
            if(type == 2) {
                color1 = new Color(0, 255, 0, 128);
                if(rank == 1) {
                    speed = 2.5;
                    r = 15;
                    health = 2;
                }
                if(rank == 2) {
                    speed = 2.5;
                    r = 20;
                    health = 3;
                }
                if(rank == 3) {
                    speed = 2.5;
                    r = 30;
                    health = 4;
                }
            }

            // Slow, but hard to kill
            if(type == 3) {
                color1 = new Color(255, 0, 0, 128);
                if(rank == 1) {
                    speed = 1.5;
                    r = 15;
                    health = 4;
                }
                if(rank == 2) {
                    speed = 1.5;
                    r = 27;
                    health = 5;
                }
                if(rank == 3) {
                    speed = 1.5;
                    r = 36;
                    health = 7;
                }
            }


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

        setPolygon();

    }

    // Functions
    public double getx() {return position.x; }
    public double gety() {return position.y; }
    public int getr() {return r; }

    public boolean isDead() { return dead; }
    public void setSlow(boolean b) {slow = b; }

    public int getType() {return type; }
    public int getRank() {return rank; }

    public void hit() {
        health--;
        if (health <= 0) {
            dead = true;
        }
        hit = true;
        hitTimer = System.nanoTime();
    }

    public void explode() {
        if(rank == 1) {
            SoundManager.asteroids1();
        }
        if(rank >  1) {
            SoundManager.asteroids();
            int amount = 0;
            if(type == 1) {
                amount = 2;
            }
            if (type == 2) {
                amount = 3;
            }
            if (type == 3) {
                amount = 4;
            }
            for (int i = 0; i < amount; i++) {
                Asteroid e = new Asteroid(getType(), getRank() - 1);
                e.setSlow(slow);
                e.position.x = this.position.x;  // so that they appear at the dead enemy position, not on top screen
                e.position.y = this.position.y;
                double angle = 0;
                if (!ready) {
                    angle = Math.random() * 140 + 20;
                }
                else {
                    angle = Math.random() * 360;
                }
                e.rad = Math.toRadians(angle);
                Game.asteroids.add(e);
            }
        }
    }

    public void update() {
        if(slow) {
            position.addScaled(velocity, 0.3).wrap(Game.WIDTH, Game.HEIGHT);
            rot += rotRate * 0.4;
        }
        else {
            position.add(velocity).wrap(Game.WIDTH, Game.HEIGHT);
            rot += rotRate;
        }





        asteroidInteract();

        if(hit) {
            long elapsed = (System.nanoTime() - hitTimer) / 1000000;
            if(elapsed > 50) {
                hit = false;
                hitTimer = 0;
            }
        }

    }

    public void asteroidInteract() {

        for(int i = 0; i < Game.asteroids.size(); i++) {
            Asteroid b = Game.asteroids.get(i);
            double bx = b.getx();
            double by = b.gety();
            double br = b.getr();

            for(int j = i+1; j < Game.asteroids.size(); j++) {

                Asteroid e = Game.asteroids.get(j);
                double ex = e.getx();
                double ey = e.gety();
                double er = e.getr();

                // pythagorean theorem - to find the distance between two points
                double rx = bx - ex;
                double ry = by - ey;
                double dist = Math.sqrt(rx * rx + ry * ry); // distance formula

                // compare the distance of two asteroids
                // if they collide change their velocity to the opposite way
                if(dist < br + er) {
                    b.velocity.rotate(4.71239);
                    e.velocity.rotate(4.71239);
                    b.position.add(0.2, 0.2); // used to avoid glitched asteroids

                }


            }
        }



    }

    public void setPolygon() {
        px = new int[nPoints];
        py = new int[nPoints];
        for (int i = 0; i < nPoints; i++) {
            double theta = (Math.PI * 2 / nPoints)
                    * (i + rand.nextDouble());
            double rad = r * (1 - radialRange / 2
                    + rand.nextDouble() * radialRange);
            px[i] = (int) (rad * Math.cos(theta));
            py[i] = (int) (rad * Math.sin(theta));
        }
    }

    public void draw(Graphics2D g) {


        if(hit) {
            g.setColor(Color.WHITE);
            AffineTransform at = g.getTransform();
            g.translate(position.x, position.y);
            g.rotate(rot);
            if(r > 30) {
                g.scale((r / 18) -0.5, (r / 18) - 0.5);
            }
            else {
                g.scale(r / 14, r / 14);
            }
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(3));
            g.drawPolygon(px, py, px.length);

            g.setTransform(at);
        }
        else {
            g.setColor(color1);
            AffineTransform at = g.getTransform();
            g.translate(position.x, position.y);
            g.rotate(rot);
            if(r > 30) {
                g.scale((r / 18) -0.5, (r / 18) - 0.5);
            }
            else {
                g.scale(r / 14, r / 14);
            }
            g.setColor(color1);
            g.setStroke(new BasicStroke(3));
            g.drawPolygon(px, py, px.length);

            g.setTransform(at);
        }

    }
}

