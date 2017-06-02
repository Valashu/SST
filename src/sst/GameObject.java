package sst;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * GameObject base class for all elements in game. Class is extended for specific elements such as Player
 * @author Marvin Reinold, Janek Bobst
 */
public class GameObject {

    Node view;
    protected Point2D velocity = new Point2D(0, 0);
    protected Point2D acceleration = new Point2D(0, 0);
    protected boolean alive = true;     //Dead objects get deleted from list and should be removed by garbage collector
    protected double scrollScale = 1;   //Used for parallax scrolling effect
    protected boolean collider = false; 
    protected Line velocityLine;
    public int health = 0;

    /**
     * Constructor, creates GameObject out of Node element
     * @param view A Javafx node, mainly ImageViews
     */
    public GameObject(Node view) {
        this.view = view;
        velocityLine = new Line(0, 0, 0,0);
        velocityLine.setStroke(Color.RED);
    }
    /**
     * Getter Scrollscale
     * @return Scrollscale used for parallax scrolling
     */
    public double getScrollScale(){
        return scrollScale;
    }
    
    /**
     * Setter Scrollscale
     * @param ss Sets object scrollscale to ss
     */
    public void setScrollScale(double ss){
        this.scrollScale = ss;
    }
    
    /**
     * Update method, moves object around canvas based on velocity and acceleration.
     * Velocity changes by acceleration, position changes by velocity
     * both velocity and acceleration decay slightly.
     */
    public void update(Pane root) {
        double mag = velocity.magnitude();
        addVelocity(acceleration);
        if (velocity.magnitude() > 50) {
            setVelocity(velocity.normalize().multiply(mag));
        }
        view.setTranslateX(view.getTranslateX() + velocity.getX());
        view.setTranslateY(view.getTranslateY() + velocity.getY());
        setVelocity(velocity.multiply(0.999));
        setAcceleration(acceleration.multiply(0.99));
    }
/**
 * Setter Velocity
 * @param velocity Sets momentary Velocity of object 
 */
    public void setVelocity(Point2D velocity) {
        this.velocity = velocity;
    }
    
    /**
     * Setter Collider
     * @param col Toggles Collider of object on or off
     */
    public void setCollider(boolean col) {
        this.collider = col;
    }

    /**
     * Getter Collider
     * @return Boolean, true if object can collide with other objects 
     */
    public boolean getCollider() {
        return this.collider;
    }
    /**
     * Setter Acceleration
     * @param acceleration Sets Object acceleration 
     */
    public void setAcceleration(Point2D acceleration) {
        this.acceleration = acceleration;
    }

    /**
     * Getter Velocity
     * @return Momentary Velocity of object
     */
    public Point2D getVelocity() {
        return velocity;
    }
    
    /**
     * Getter Acceöeration
     * @return Object acceleration
     */
    public Point2D getAcceleration() {
        return acceleration;
    }

    /**
     * Getter of Object View
     * @return View element, Most likely ImageView
     */
    public Node getView() {
        return view;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isDead() {
        return !alive;
    }

    /**
     * Setter Alive, Dead objects are collected and removed from lists
     * @param alive sets alive status of object
     */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     * Getter Rotation of Object View
     * @return Rotation in degrees.
     */
    public double getRotate() {
        return view.getRotate();
    }

    /**
     * Rotates Object to the right by 2 degrees
     */
    public void rotateRight() {
        view.setRotate(view.getRotate() + 2);
        
    }

    /**
     * Rotates Object to the left by 2 degrees
     */
    public void rotateLeft() {
        view.setRotate(view.getRotate() - 2);
    }

    /**
     * Checks for collision of Bounding boxes between this and object other
     * @param other another GameObject
     * @return true if Bounding boxes collide
     */
    public boolean isColliding(GameObject other) {
        return getView().getBoundsInParent().intersects(other.getView().getBoundsInParent());
    }
    

    /**
     * Gives active direction as vector with lenght of 1
     * @return facing direction scaled to vector with magnitude 1
     */
    public Point2D getDirectionNormal(){
        return new Point2D(Math.cos(Math.toRadians(getRotate())),Math.sin(Math.toRadians(getRotate()))).normalize();
    }
    /**
     * Gives Object acceleration to the left of the active direction
     */
    void boostLeft() {
        double mag = acceleration.magnitude();
       Point2D newDirAcc = new Point2D(Math.sin(Math.toRadians(getRotate())),Math.cos(Math.toRadians(getRotate()))*-1).multiply(0.001);
       setAcceleration(acceleration.add(newDirAcc));
        if (acceleration.magnitude() > 1) {
            setAcceleration(acceleration.normalize().multiply(mag));
        }
    }
    /**
     * Gives Object acceleration to the right of the active direction
     */
    void boostRight() {
        double mag = acceleration.magnitude();
       Point2D newDirAcc = new Point2D(Math.sin(Math.toRadians(getRotate()))*-1,Math.cos(Math.toRadians(getRotate()))).multiply(0.001);
       setAcceleration(acceleration.add(newDirAcc));
        if (acceleration.magnitude() >  0.5) {
            setAcceleration(acceleration.normalize().multiply(mag));
        }
    }
    /**
     * Increases acceleration in direction of rotation of object
     */
    void boostForward() {
        double mag = acceleration.magnitude();
        Point2D newDirAcc = new Point2D(Math.cos(Math.toRadians(getRotate())), Math.sin(Math.toRadians(getRotate()))).normalize().multiply(0.002);
        setAcceleration(acceleration.add(newDirAcc));
        if (acceleration.magnitude() > 5) {
            setAcceleration(acceleration.normalize().multiply(mag));
        }
        
        
    }
    /**
     * increases acceleration in the other direction of the object
     */
    void boostBackward() {
        double mag = acceleration.magnitude();
        Point2D newDirAcc = new Point2D(Math.cos(Math.toRadians(getRotate())), Math.sin(Math.toRadians(getRotate()))).normalize().multiply(0.001);
        setAcceleration(acceleration.add(newDirAcc.multiply(-1)));
        if (acceleration.magnitude() > 1) {
            setAcceleration(acceleration.normalize().multiply(mag));
        }
    }
    /**
     * Adds p to velocity
     * @param p value to add to velocity 
     */
    void addVelocity(Point2D p) {
        setVelocity(velocity.add(p));
        
    }
    /**
     * Subtracts p from velocity
     * @param p value to subtract from velocity 
     */
    void subVelocity(Point2D p) {
        setVelocity(velocity.add(p));
    }
    
    /**
     * Draws Debug Velocity line of this Object, visible while paused
     * @param root Root Pane with Child objects
     */
    public void drawVelocityLine(Pane root){
        if(this.getVelocity().magnitude() != 0){
           if (!root.getChildren().contains(this.velocityLine)) {
           root.getChildren().add(this.velocityLine); 
        }
        velocityLine.setVisible(true);
        velocityLine.setStartX(this.getView().getTranslateX());
        velocityLine.setStartY(this.getView().getTranslateY());
        velocityLine.setEndX(velocityLine.getStartX()+(this.getVelocity().getX())*10);
        velocityLine.setEndY(velocityLine.getStartY()+(this.getVelocity().getY())*10); 
        }
        
    }
    /**
     * Hides Debug Velocity line until shown again with drawVelocityLine
     */
    public void clearVelocityLine(){
        velocityLine.setVisible(false);
    }
    /**
     * Delets view to prepare this object for deletion
     */
    void deleteView() {
        this.view = null;
    }
    
    /**
     * Returns active positiopn as Point2d value
     * @return Position as Point2d
     */
    public Point2D positionToPoint(){
        return new Point2D(this.getView().getTranslateX(), this.getView().getTranslateY());
    }

    

    
}