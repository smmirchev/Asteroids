package assignment;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Ship {

    // Fields

    public Vector2D direction;
    public Vector2D position;
    public Vector2D velocity;
    double r = 10;

    private boolean left;
    private boolean right;
    private boolean up;
    private boolean pressShield = false;

    private boolean space;
    private long firingTimer;
    private long firingDelay;

    private boolean recovering = false;
    private long recoveryTimer = 0;

    private int lives = 3;
    private Color color1 =  Color.cyan;
    private Color color2 = Color.magenta;

    private int score;

    private int powerLevel;
    private int power;
    private int[] requiredPower = {
            1, 2, 3, 4, 5
    };

    // rotation velocity in radians per second
    public static final double STEER_RATE = 2* Math.PI;

    // acceleration when thrust is applied
    public static final double MAG_ACC = 200;

    // constant speed loss factor
    public static final double DRAG = 0.01;


    public static final int DELAY = 20;  // in milliseconds
    public static final double DT = DELAY / 1000.0;  // in seconds

    public static final double DRAWING_SCALE = 8;


    final int[] XP = {0, 2,  0, -2};
    final int[] YP = {-2, 2, 0, 2};


    final int[] XPTHRUST = {0, 1, -1};
    final int[] YPTHRUST = {0, 1, 1};

    public int thrust; // 0 = off, 1 = on
    public int turn; // -1 = left turn, 0 = no turn, 1 = right turn

    public int shieldTime = 0;
    private boolean shielded = false;
    private long shieldTimer = 0;

    //Constructor
    public Ship () {
        this.position = new Vector2D().set(Game.HEIGHT / 2, Game.WIDTH / 2);
        this.velocity = new Vector2D().mult(10);
        this.direction = new Vector2D(1, 0);

        space = false;
        firingTimer = System.nanoTime();
        firingDelay = 200;
    }

    //Functions

    public void setLeft(boolean b)  {left = b;}
    public void setRight(boolean b)  {right = b;}
    public void setUp (boolean b)  {up = b;}
    public void setFiring(boolean b) {space = b;}
    public void setShield(boolean b) {pressShield = b;
    shieldTimer = System.nanoTime();}

    public double getx() {return position.x; }
    public double gety() {return position.y; }
    public double getr() {return r; }

    public int getScore() {return score; }
    public void addScore(int i) {score += i; }

    public int getLives() {return lives; }

    public boolean isDead() {return lives <= 0;}
    public boolean isRecovering() {return recovering;}
    public boolean isShielded() {return shielded;}
    public boolean getShield() {return pressShield;}
    public void gainLife() {
        lives++;
    }

    public void loseLife() {
        lives--;
        recovering = true;
        recoveryTimer = System.nanoTime();
    }



    public void increasePower(int i) {
        power += i;
        if(powerLevel == 4) {
            if(power > requiredPower[powerLevel]) {
                power = requiredPower[powerLevel];
            }
            return;
        }
        if(power >= requiredPower[powerLevel]) {
            power -= requiredPower[powerLevel];
            powerLevel++;
        }
    }

    public int getPowerLevel() {return powerLevel; }
    public int getPower() {return  power; }
    public int getRequiredPower() {return requiredPower[powerLevel]; }



    public void update() {


        turn = 0;
        thrust = 0;

        if (left)
            turn = -1;


        if (right)
            turn = +1;

        if (up) {
            thrust = 1;
            SoundManager.startThrust();
        } else
            SoundManager.stopThrust();



        direction.rotate(turn * STEER_RATE * DT);
        velocity.addScaled(direction, MAG_ACC * thrust * DT)
                .mult(1 - DRAG);
        position.addScaled(velocity, DT)
                .wrap(Game.WIDTH, Game.HEIGHT);

        if (space) {
            long elapsed = (System.nanoTime() - firingTimer) / 1000000;

            if (elapsed > firingDelay) {
                firingTimer = System.nanoTime();
                SoundManager.fire();

                if (powerLevel < 2) {
                    Game.bullets.add(new Bullet(direction.angle(), position.x, position.y));
                } else if (powerLevel < 3) {
                    Game.bullets.add(new Bullet(direction.angle() + 0.05, position.x + 5, position.y + 5));
                    Game.bullets.add(new Bullet(direction.angle() - 0.05, position.x - 5, position.y - 5));
                } else if (powerLevel < 4) {
                    Game.bullets.add(new Bullet(direction.angle(), position.x, position.y));
                    Game.bullets.add(new Bullet(direction.angle() - 0.03, position.x - 9, position.y - 9));
                    Game.bullets.add(new Bullet(direction.angle() - 0.2, position.x + 9, position.y + 9));
                    Game.bullets.add(new Bullet(direction.angle() + 0.2, position.x - 9, position.y - 9));
                } else {
                    Game.bullets.add(new Bullet(direction.angle(), position.x, position.y));
                    Game.bullets.add(new Bullet(direction.angle() + 0.3, position.x - 9, position.y - 9));
                    Game.bullets.add(new Bullet(direction.angle() - 0.3, position.x - 9, position.y - 9));
                    Game.bullets.add(new Bullet(direction.angle() - 0.5, position.x + 9, position.y + 9));
                    Game.bullets.add(new Bullet(direction.angle() + 0.5, position.x - 9, position.y - 9));
                }
            }
        }

        if (recovering) {
            long elapsed = (System.nanoTime() - recoveryTimer) / 1000000; // how much time has past since getting hit
            if (elapsed > 2000) { // be invincible after getting hit for 2 seconds
                recovering = false;
                recoveryTimer = 0;
            }
        }

        if(shieldTime > 0 && getShield()) {
            shielded = true;
            long elapsed = (System.nanoTime() - shieldTimer) / 1000000;
            if (elapsed > shieldTime) {
                shieldTime = 0;
                shielded = false;
                pressShield = false;
            }
        }
    }



    public void draw(Graphics2D g) {

        if (recovering || shielded) {
            AffineTransform at = g.getTransform();
            g.translate(position.x, position.y);
            double rot = direction.angle() + Math.PI / 2;
            g.rotate(rot);
            g.scale(r, r);
            g.setColor(color2);
            g.fillPolygon(XP, YP, XP.length);
            if (thrust == 1) {
                g.setColor(Color.red);
                g.fillPolygon(XPTHRUST, YPTHRUST, XPTHRUST.length);
            }

            g.setTransform(at);

            g.setColor(Color.RED);
            Vector2D end = new Vector2D(position).addScaled(direction, 20);
            g.setStroke(new BasicStroke(2));
            g.drawLine((int)position.x, (int)position.y, (int)end.x, (int)end.y);
        }
        else {
            AffineTransform at = g.getTransform();
            g.translate(position.x, position.y);
            double rot = direction.angle() + Math.PI / 2;
            g.rotate(rot);
            g.scale(r, r);
            g.setColor(color1);
            g.fillPolygon(XP, YP, XP.length);
            if (thrust == 1) {
                g.setColor(Color.red);
                g.fillPolygon(XPTHRUST, YPTHRUST, XPTHRUST.length);
            }

            g.setTransform(at);

            g.setColor(Color.RED);
            Vector2D end = new Vector2D(position).addScaled(direction, 20);
            g.setStroke(new BasicStroke(2));
            g.drawLine((int)position.x, (int)position.y, (int)end.x, (int)end.y);
        }
    }
}
