package assignment;

// mutable 2D vectors
public final class Vector2D {
    public double x, y;

    // constructor for zero vector
    public Vector2D() {
        x = 0;
        y = 0;
    }

    // constructor for vector with given coordinates
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // constructor that copies the argument vector
    public Vector2D(Vector2D v) {
        set(v);
    }

    // set coordinates
    public Vector2D set(double x, double y) {
        this.x = x;
        this.y = y;

        return this;
    }

    // set coordinates based on argument vector
    public Vector2D set(Vector2D v) {
        this.x = v.x;
        this.y = v.y;

        return this;
    }

    // compare for equality (note Object type argument)
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Vector2D) {
            Vector2D v = (Vector2D) o;
            return (x == v.x) && (y == v.y);
        }
        return false;
    }

    // String for displaying vector as text
    public String toString() {
        return "Vector2D[" + x + ", " + y + "]";
    }

    //  magnitude (= "length") of this vector
    public double mag() {
        return Math.sqrt(x * x + y * y);
    }

    // angle between vector and horizontal axis in radians in range [-PI,PI]
// can be calculated using Math.atan2
    public double angle() {
       return Math.atan2(y, x);
    }

    // angle between this vector and another vector in range [-PI,PI]
    public double angle(Vector2D other) {
        double dot = this.x * other.x + this.y * other.y;
        double det = this.x * other.y - this.y * other.x;

        return Math.atan2(det, dot);
    }

    // add argument vector
    public Vector2D add(Vector2D v) {
        this.x += v.x;
        this.y += v.y;
        return this;
    }

    // add values to coordinates
    public Vector2D add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    // weighted add - surprisingly useful
    public Vector2D addScaled(Vector2D v, double fac) {
        this.x += fac * v.x;
        this.y += fac * v.y;
        return this;
    }

    // subtract argument vector
    public Vector2D subtract(Vector2D v) {
        this.x -= v.x;
        this.y -= v.y;
        return this;
    }

    // subtract values from coordinates
    public Vector2D subtract(double x, double y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    // multiply with factor
    public Vector2D mult(double fac) {
        this.x *= fac;
        this.y *= fac;
        return this;
    }

    // rotate by angle given in radians
    public Vector2D rotate(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        double tempX = this.x * cos - this.y * sin;
        double tempY = this.x * sin + this.y * cos;

        this.x = tempX;
        this.y = tempY;

        return this;
    }

    // "dot product" ("scalar product") with argument vector
    public double dot(Vector2D v) {
        return (this.x * v.x + this.y * v.y);
    }

    // distance to argument vector
    public double dist(Vector2D v) {
        double vx = v.x - this.x;
        double vy = v.y - this.y;
        return Math.sqrt(vx * vx + vy * vy);
    }

    // normalise vector so that magnitude becomes 1
    public Vector2D normalise() {
        double magnitude = mag();
        this.x /= magnitude;
        this.y /= magnitude;
        return this;

    }

    // wrap-around operation, assumes w> 0 and h>0
// remember to manage negative values of the coordinates
    public Vector2D wrap(double w, double h) {
        this.x = (this.x + w) % w;
        this.y = (this.y + h) % h;

        return this;
    }

    // construct vector with given polar coordinates
    public static Vector2D polar(double angle, double mag) {
        return new Vector2D(mag * Math.cos(angle), mag * Math.sin(angle));
    }

}
