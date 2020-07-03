package assignment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Game extends JPanel implements Runnable, KeyListener {

    // FIELDS
    public static int WIDTH = 800;
    public static int HEIGHT = 800;

    private Thread thread;
    private boolean running;

    private BufferedImage image;
    private  Graphics2D g;

    private int FPS = 58;
    private double averageFPS;

    public static Ship ship;
    public static ArrayList<Bullet> bullets;
    public static ArrayList<Asteroid> asteroids;
    public static ArrayList<PowerUp> powerUps;
    public static ArrayList<Explosion> explosions;
    public static ArrayList<Text> texts;
    public static ArrayList<Saucer> saucers;
    public static ArrayList<Bullet> enemyBullets;

    private long levelStartTimer;
    private long levelStartTimerDiff;
    private int levelNumber;
    private boolean levelStart;
    private int levelDelay = 3000;  // Next level begins after 3 seconds

    private long slowDownTimer;
    private long slowDownTimerDiff;
    private int slowDownLength = 6000;



    // Constructor
    public Game() {
        super();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
    }

    //Functions
    public void addNotify() {
        super.addNotify();
        if(thread == null) {
            thread = new Thread(this);
            thread.start();
        }

        addKeyListener(this);
    }

    public void run() {
        long startTime;
        long timeMIllis;
        long waitTime;
        long totalTime = 0;

        int frameCount = 0;
        int maxFrameCount = 60;

        long targetTime = 1000 / FPS; // the amount of time it takes for 1 loop in order to run 60 FPS // approximately 17 milliseconds

        running = true;

        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();

        ship = new Ship();
        bullets = new ArrayList<Bullet>();
        asteroids = new ArrayList<Asteroid>();
        powerUps = new ArrayList<PowerUp>();
        explosions = new ArrayList<Explosion>();
        texts = new ArrayList<Text>();
        saucers = new ArrayList<Saucer>();
        enemyBullets = new ArrayList<Bullet>();

        levelStartTimer = 0;
        levelStartTimerDiff = 0;
        levelStart = true;
        levelNumber = 0;


        //Game Loop
        while (running) {
            startTime = System.nanoTime();  // gets current time in nano seconds

            gameUpdate();
            gameRender();
            gameDraw();

            timeMIllis = (System.nanoTime() - startTime) / 1000000; // divided by 1mil to get it in milliseconds

            //the amount of extra time it has to wait
            waitTime = targetTime - timeMIllis;

            try {
                Thread.sleep(waitTime);
            }
            catch(Exception e) {
            }

            totalTime += System.nanoTime() - startTime; // total loop time
            frameCount++;
            if(frameCount == maxFrameCount) {
                averageFPS = 1000.0 / ((totalTime /  frameCount)/ 1000000);
                frameCount = 0;
                totalTime = 0;
            }
        }

        // Drawing the end game screen
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Calibri", Font.PLAIN, 24));
        String s = "WELL DONE!";
        int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
        g.drawString(s, (WIDTH - length) / 2, HEIGHT / 2);
        s = "Your Score is  " + ship.getScore();
        length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
        g.drawString(s, (WIDTH - length) / 2, HEIGHT / 2 + 30);
        gameDraw(); // so it actually draws the game over screen, otherwise the game freezes
    }

    private void gameUpdate() {

        // New Level
        if(levelStartTimer == 0 && asteroids.size() == 0) {
            levelNumber++;
            levelStart = false;
            levelStartTimer = System.nanoTime();
        }
        else {
            levelStartTimerDiff = (System.nanoTime() - levelStartTimer) / 1000000;
            if(levelStartTimerDiff > levelDelay) {
                levelStart = true;
                levelStartTimer = 0;
                levelStartTimerDiff = 0;
            }
        }

        //Create asteroids
        if(levelStart && asteroids.size() == 0) {
            createNewEnemies();
        }


        // Player update
        ship.update();


        // Saucer update
        for(int i = 0; i < saucers.size(); i++) {
            boolean remove = saucers.get(i).update();
            if(remove) {
                bullets.remove(i);
                i--;
            }
        }


        // Bullet update
        for(int i = 0; i < bullets.size(); i++) {
            boolean remove = bullets.get(i).update();
            if(remove) {
                bullets.remove(i);
                i--;
            }
        }

        // Saucer Bullets update
        for(int i = 0; i < enemyBullets.size(); i++) {
            boolean remove = enemyBullets.get(i).update();
            if(remove) {
                enemyBullets.remove(i);
                i--;
            }
        }

        // Asteroid update
        for(int i = 0; i < asteroids.size(); i++) {
            asteroids.get(i).update();
        }

        //PowerUp update
        for(int i = 0; i < powerUps.size(); i++) {
            boolean remove = powerUps.get(i).update();
            if(remove) {
                powerUps.remove(i);
                i--;
            }
        }

        // Explosion update
        for(int i = 0; i < explosions.size(); i++) {
            boolean remove = explosions.get(i).update();
            if(remove) {
                explosions.remove(i);
                i--;
            }
        }

        //Text update
        for( int i = 0; i < texts.size(); i++) {
            boolean remove = texts.get(i).update();
            if(remove) {
                texts.remove(i);
                i--;
            }
        }


        // Bullet-asteroid collision
        for (int i = 0; i < bullets.size(); i++) {

            Bullet b = bullets.get(i);
            double bx = b.getx();
            double by = b.gety();
            double br = b.getr();

            for(int j = 0; j < asteroids.size(); j++) {

                Asteroid e = asteroids.get(j);
                double ex = e.getx();
                double ey = e.gety();
                double er = e.getr();

                // pythagorean theorem - to find the distance between two points
                double dx = bx - ex;
                double dy = by - ey;
                double dist = Math.sqrt(dx * dx + dy * dy); // distance formula

                // compare the distance of the radii of the asteroid and the bullet
                // if it's less than the sum of them, they have collided
                if(dist < br + er) {
                    e.hit();
                    bullets.remove(i);
                    i--;
                    break;
                }
            }
        }

        // Saucer Bullet - Player  collision
        for (int i = 0; i < enemyBullets.size(); i++) {

            Bullet b = enemyBullets.get(i);
            double bx = b.getx();
            double by = b.gety();
            double br = b.getr();

            double ex = ship.getx();
            double ey = ship.gety();
            double er = ship.getr();

            double dx = bx - ex;
            double dy = by - ey;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if(dist < br + er) {
                ship.loseLife();
                enemyBullets.remove(i);
                i--;
                break;
            }
        }

        // Bullet-saucer collision
        if(!ship.isRecovering() && !ship.isShielded()) {
            for (int i = 0; i < bullets.size(); i++) {

                Bullet b = bullets.get(i);
                double bx = b.getx();
                double by = b.gety();
                double br = b.getr();

                for(int j = 0; j <saucers.size(); j++) {

                    Saucer e = saucers.get(j);
                    double ex = e.getx();
                    double ey = e.gety();
                    double er = e.getr();

                    double dx = bx - ex;
                    double dy = by - ey;
                    double dist = Math.sqrt(dx * dx + dy * dy);

                    if(dist < br + er) {
                        e.hit();
                        bullets.remove(i);
                        i--;
                        break;
                    }
                }
            }
        }

        // check dead asteroids
        for(int i = 0; i < asteroids.size(); i++) {
            if (asteroids.get(i).isDead()) {
                Asteroid e = asteroids.get(i);

                // chance for powerUp
                double rand = Math.random();
                if(rand < 0.001) powerUps.add(new PowerUp(1, e.getx(), e.gety())); // 1 in a 1000 chance
                else if(rand < 0.020) powerUps.add(new PowerUp(3, e.getx(), e.gety())); // 2% chance
                else if(rand < 0.120) powerUps.add(new PowerUp(2, e.getx(), e.gety()));
                else if(rand < 0.130) powerUps.add(new PowerUp(4, e.getx(), e.gety()));

                ship.addScore(e.getType() + e.getRank());
                asteroids.remove(i);
                i--;

                e.explode();
                explosions.add(new Explosion(e.getx(), e.gety(), e.getr(), e.getr() + 50));
            }
        }
        // Check dead saucer
        for(int i =0; i < saucers.size(); i++) {
            if(saucers.get(i).isDead()) {
                Saucer e = saucers.get(i);
                ship.addScore(10);
                saucers.remove(i);
                i--;
            }
        }


        // Check for dead player
        if(ship.isDead()) {
            running = false;
        }

        // Player-asteroid collision
        if(!ship.isRecovering() && !ship.isShielded()) {
            double px = ship.getx();
            double py = ship.gety();
            double pr = ship.getr();
            for(int i = 0; i < asteroids.size(); i++) {
                Asteroid e = asteroids.get(i);
                double ex = e.getx();
                double ey = e.gety();
                double er = e.getr();

                double dx = px - ex;
                double dy = py - ey;
                double dist = Math.sqrt(dx * dx + dy * dy);

                if(dist < pr + er) {
                    ship.loseLife();
                }
            }
        }

        // Player-powerUp collision
        double px = ship.getx();
        double py = ship.gety();
        double pr = ship.getr();
        for(int i = 0; i < powerUps.size(); i++) {
            PowerUp p = powerUps.get(i);
            double x = p.getx();
            double y = p.gety();
            double r = p.getr();

            double dx = px - x;
            double dy = py -y;
            double dist = Math.sqrt(dx * dx + dy * dy);

            // Collected powerUp
            if(dist < pr + r) {

                int type = p.getType();

                if(type == 1) {
                    ship.gainLife();
                    texts.add(new Text(ship.getx(), ship.gety(), 2000, "Extra Life"));

                }
                if(type == 2) {
                    ship.increasePower(1);
                    texts.add(new Text(ship.getx(), ship.gety(), 2000, "Power"));

                }
                if(type == 3) {
                    ship.increasePower(2);
                   texts.add(new Text(ship.getx(), ship.gety(), 2000, "Double Power"));

                }
                if(type == 4) {
                    slowDownTimer = System.nanoTime();
                    for(int j = 0; j < asteroids.size(); j++) {
                        asteroids.get(j).setSlow(true);
                    }
                    texts.add(new Text(ship.getx(), ship.gety(), 2000, "Slow Down"));
                }

                powerUps.remove(i);
                i--;
            }
        }

        // Slowdown update
        if(slowDownTimer != 0) {
            slowDownTimerDiff = (System.nanoTime() - slowDownTimer) / 1000000;
            if(slowDownTimerDiff > slowDownLength) {
                slowDownTimer = 0;
                for(int j = 0; j < asteroids.size(); j++) {
                    asteroids.get(j).setSlow(false);
                }
            }
        }

    }

    // Draws graphics off screen
    private void gameRender() {
        //Draw background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT); // fills a rect across the entire screen

        // Draw slowdown screen
        if(slowDownTimer != 0) {
            g.setColor(new Color(60, 179, 255, 25));
            g.fillRect(0, 0, WIDTH, HEIGHT);
        }

        // Draw FPS and number of bullets
        g.setColor(Color.WHITE);
        g.drawString("FPS: " + Math.round(averageFPS), 6, 20);
        g.drawString("Asteroid: " + asteroids.size(), 6, 40);

        // Draw player
        ship.draw(g);

        // Draw Saucers
        for(int i = 0; i < saucers.size(); i++) {
            saucers.get(i).draw(g);
        }

        // Draw Bullets
        for(int i = 0; i < bullets.size(); i++) {
            bullets.get(i).draw(g);
        }

        // Draw Asteroid Bullets
        for(int i = 0; i < enemyBullets.size(); i++) {
            enemyBullets.get(i).draw(g);
        }

        //Draw Enemies
        for(int i = 0; i < asteroids.size(); i++) {
            asteroids.get(i).draw(g);
        }

        //Draw powerUps
        for(int i = 0; i < powerUps.size(); i++) {
            powerUps.get(i).draw(g);
        }
        //Draw explosions
        for(int i = 0; i < explosions.size(); i++) {
            explosions.get(i).draw(g);
        }

        //Draw text
        for(int i = 0; i < texts.size(); i++) {
            texts.get(i).draw(g);
        }

        // Draw level number
        if(levelStartTimer != 0) {
            g.setFont(new Font("Calibri", Font.PLAIN, 24));
            String s = " L E V E L  " + levelNumber + "      ";
            //Pulse in and out
            int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth(); // Gives us the total length of the string in pixels
            int alpha = (int) (255 * Math.sin(3.14 * levelStartTimerDiff / levelDelay)); // Gives it transparency
            if(alpha > 255) alpha = 255;
            g.setColor(new Color(255, 255, 255, alpha)); // White color with transparency
            g.drawString(s, WIDTH / 2 - length / 2, HEIGHT / 2 - 100);
        }

        // Draw player lives
        for(int i = 0; i < ship.getLives(); i++) {

            AffineTransform at = g.getTransform();
            g.translate(14 + (20 * i), 60);
            g.scale(4, 4);
            g.setColor(Color.cyan);
            //g.fillPolygon(XP, YP, XP.length);
            final int[] XP = {0, 2,  0, -2};
            final int[] YP = {-2, 2, 0, 2};
            g.fillPolygon(XP, YP, XP.length);
            g.setTransform(at);
        }

        // Draw player power
        g.setColor(Color.YELLOW);
        g.fillRect(20, 80, ship.getPower() * 8, 8);
        g.setColor(Color.YELLOW.darker());
        g.setStroke(new BasicStroke(2));
        for(int i = 0; i < ship.getRequiredPower(); i++) {
            g.drawRect(20 +  8 * i, 80, 8, 8);
        }
        g.setStroke(new BasicStroke(2));

        // Draw player score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Calibri", Font.PLAIN, 14));
        g.drawString("Score: " + ship.getScore(), WIDTH - 100, 30);

        // Draw slowdown meter
        if(slowDownTimer != 0) {
            g.setColor(Color.WHITE);
            g.drawRect(20, 120, 100, 8);
            g.fillRect(20, 120,
                    (int) (100 - 100.0 * slowDownTimerDiff / slowDownLength), 8);
        }

        // Draw shield timer
        g.setColor(Color.WHITE);
        g.setFont(new Font("Calibri", Font.PLAIN, 14));
        g.drawString("Shield Time: " + ship.shieldTime / 1000 + "s", 6, 110);



    }

    // Draws on game screen
    private void gameDraw() {
        Graphics g2 = this.getGraphics();
        g2.drawImage(image,0, 0, null);
        g2.dispose();
    }

    // New levels added from here
    private void createNewEnemies() {
        asteroids.clear();
        Asteroid e;

        if(levelNumber == 1) {
            for(int i = 0; i < 5; i++) {
                asteroids.add(new Asteroid(1, 1));
            }

        }
        if(levelNumber == 2) {
            for(int i = 0; i < 4; i++) {
                asteroids.add(new Asteroid(1, 1));
            }
            for(int i = 0; i < 4; i++) {
                asteroids.add(new Asteroid(2, 1));
            }
            ship.shieldTime += 4000;
        }
        if(levelNumber == 3) {
            for(int i = 0; i < 4; i++) {
                asteroids.add(new Asteroid(3, 1));
            }
            asteroids.add(new Asteroid(1, 3));
            ship.shieldTime += 4000;
        }
        if(levelNumber == 4) {
            for(int i = 0; i < 2; i++) {
                asteroids.add(new Asteroid(2, 3));
            }
            asteroids.add(new Asteroid(1, 3));
            ship.shieldTime += 4000;
        }
        if(levelNumber == 5) {
            for (int i = 0; i < 5; i++) {
                asteroids.add(new Asteroid(1, 3));
            }
            ship.shieldTime += 10000;
        }
        if(levelNumber == 6) {
            for(int i = 0; i < 10; i++) {
                asteroids.add(new Asteroid(3, 1));
            }
            saucers.add(new Saucer());
            ship.shieldTime += 4000;
        }
        if(levelNumber == 7) {
            for(int i = 0; i < 4; i++) {
                asteroids.add(new Asteroid(2, 2));
            }
            for(int i = 0; i < 4; i++) {
                asteroids.add(new Asteroid(3, 2));
            }
            asteroids.add(new Asteroid(3,3));
            ship.shieldTime += 4000;
        }
        if(levelNumber == 8) {
            for(int i = 0; i < 4; i++) {
                asteroids.add(new Asteroid(2, 3));
            }
            asteroids.add(new Asteroid(3,3));
            saucers.add(new Saucer());
            saucers.add(new Saucer());
            ship.shieldTime += 10000;
        }
        if(levelNumber == 9) {
            for(int i = 0; i < 4; i++) {
                asteroids.add(new Asteroid(3, 3));
            }
            saucers.add(new Saucer());
            ship.shieldTime += 4000;
        }
        if(levelNumber == 10) {
            for(int i = 0; i < 20; i++) {
                asteroids.add(new Asteroid(2, 2));
            }
            ship.shieldTime += 4000;
        }
        if(levelNumber == 11) {
            for(int i = 0; i < 5; i++) {
                saucers.add(new Saucer());
            }
            ship.shieldTime += 4000;
        }
        if(levelNumber == 12) {
            for(int i = 0; i < 3; i++) {
                saucers.add(new Saucer());
                asteroids.add(new Asteroid(2, 2));
                asteroids.add(new Asteroid(3, 1));
            }
            ship.shieldTime += 4000;
        }
        if(levelNumber == 13) {
            for(int i = 0; i < 4; i++) {
                saucers.add(new Saucer());
                asteroids.add(new Asteroid(1, 1));
                asteroids.add(new Asteroid(2, 1));
                asteroids.add(new Asteroid(3, 1));
            }
            ship.shieldTime += 10000;
        }
        if(levelNumber == 14) {
            for(int i = 0; i < 4; i++) {
                saucers.add(new Saucer());
                asteroids.add(new Asteroid(1, 1));
                asteroids.add(new Asteroid(2, 1));
                asteroids.add(new Asteroid(3, 1));
            }
            for(int i = 0; i < 2; i++) {
                asteroids.add(new Asteroid(1, 3));
                asteroids.add(new Asteroid(2, 2));
                asteroids.add(new Asteroid(3, 2));
            }
            ship.shieldTime += 4000;
        }
        if(levelNumber == 15) {
            for(int i = 0; i < 4; i++) {
                saucers.add(new Saucer());
                asteroids.add(new Asteroid(3, 3));
            }
            for(int i = 0; i < 2; i++) {
                asteroids.add(new Asteroid(1, 3));
            }
            ship.shieldTime += 4000;
        }
        if(levelNumber == 16) {
            running = false;
        }
    }

    public void keyTyped(KeyEvent key) {}

    public void keyPressed(KeyEvent key) {
        int keyCode = key.getKeyCode();
        if(keyCode == KeyEvent.VK_LEFT) {
            ship.setLeft(true);
        }
        if(keyCode == KeyEvent.VK_RIGHT ) {
            ship.setRight(true);
        }
        if(keyCode == KeyEvent.VK_UP) {
            ship.setUp(true);
        }
        if(keyCode == KeyEvent.VK_B) {
            ship.setShield(true);
        }
        if(keyCode == KeyEvent.VK_SPACE) {
            ship.setFiring(true);
        }
    }

    public void keyReleased(KeyEvent key) {
        int keyCode = key.getKeyCode();
        if(keyCode == KeyEvent.VK_LEFT) {
            ship.setLeft(false);
        }
        if(keyCode == KeyEvent.VK_RIGHT ) {
            ship.setRight(false);
        }
        if(keyCode == KeyEvent.VK_UP) {
            ship.setUp(false);
        }
//        if(keyCode == KeyEvent.VK_B) {
//            ship.setShield(false);
//        }
        if(keyCode == KeyEvent.VK_SPACE) {
            ship.setFiring(false);
        }
    }
}
