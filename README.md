# Asteroids Game

## Getting Started
Download the files, external libraries are not used.

## Game instructions:
The game can be run by running the main class – GameMain.

Controls:
The player can move and maneuver their ship by pressing the arrow keys. The up arrow increases the velocity of the ship, the left and right arrows change the direction of the ship respectively. 
The player shoots by pressing or holding the space key.
The protective shield can be activated by pressing the “B” key, provided there is enough energy (more on that below).

## Design and Implementation:
### Special Power-Ups:
There are 5 unique power-ups the players can acquire.
An energy shield, which when activated it prevents the ship from taking any damage. (Key “B”)
Every game level gives a 4 seconds worth of protective shield, with the exception of level 5, 8 and 13, which give 10 seconds. Once the shield is activated, it cannot be turned off until the total time for the shield has been used. Players should strategically use the shield on difficult for them levels.
The other 4 power-ups are quite similar to each other.
They all have a chance to drop from asteroids when destroyed. They are in the shape of a rectangle and when they appear, they slowly go down until they are off screen and removed. Therefore, to increase their chances of catching up a power-up, players should try to destroy asteroids while they are on the top side of the screen. The 4 power-up are as follow:
Type 1 is extremely rare and gives the players an extra life. The color is pink.
Type 2 increases the power of the ship by 1. The color is yellow.
Type 3 increases the power of the ship by 2. Again yellow, but larger rectangle.
Type 4 slows down the time – the asteroids move and rotate with 30% speed for 6 seconds (the saucers are not affected). 
Not exactly a power-up. When the ship is hit by an enemy, it will enter into recovering mode, which prevents it from taking any further damage for 2 seconds (exactly like the energy shield).

### Visual Effects:
Whenever a power-up is collected a text on the position where the power-up was, will appear and show the name of the power-up.
When the ship enters recovery mode, or the player activates the energy shield, the ship will change color to red.
 The asteroids are rotating with random value. Does not affect gameplay, it is just there for the more pleasing look. Also, asteroids have different polygon shape from one another.
Picking up the slow down time power-up, changes the background brightness of the game screen. A timer is also provided for the duration of the power-up.
Asteroids come in different shapes, colors, types and ranks.
Destroying asteroid causes and explosion in the shape of a circle to pop up and fade away.
When an asteroid has more than 1 health and it is not destroyed from the bullet hit, it will blink into white color for a moment to show that it actually took damage.

### Sound Effects:
The sound manager class is used for shooting, thrusting, asteroid explosion and saucer shooting.

### Software Implementation, Interface and More Design:
The player’s ship can get up to power level 4. When a new level is acquired, it appears as an empty yellow bar. When it is filed up, the power is increased. Level 2, the ships shoots 2 bullets at once, 3 bullets at level 3 and 4 bullets for level 4.
Auto-shooting (holding down space) has a small delay to prevent it from being too strong.
Depending on the type and rank, asteroids will spawn new smaller asteroids on the position they were destroyed, which in result can spawn even smaller asteroids.
The flying enemy ship (saucer) always aims at the player first, then shoots randomly


## Author
* Stefan Mirchev
