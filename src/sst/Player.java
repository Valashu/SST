package sst;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 *  Extension of GameObject class, used solely for player ship
 * @author Marvin Reinold, Janek Bobst
 */
public class Player extends GameObject{
    
    public boolean stabilizer = false;
    public boolean inbound = true;
    public int torpedoCooldown = 0;
    public int laserCooldown = 0;
    public int health = 100;
    /**
     * Constructor, other constructor used at the moment
     * @param view 
     */
    public Player(Node view) {
        super(view);
    }
    /**
     * Constructor, generates Player with correct ImageView
     */
    public Player() {
        super(new ImageView(new Image("img/ship.png", 50, 22, true, true)));
    }
    /**
     * Update Method, moves on screen while in bounds. 
     * @param root Root Pane with child objects
     */
    @Override
    public void update(Pane root) {
        double mag = velocity.magnitude();
        addVelocity(acceleration);
        if (velocity.magnitude() > 10) {
            setVelocity(velocity.normalize().multiply(mag));
        }
        if(this.inbound == true){
            view.setTranslateX(view.getTranslateX() + velocity.getX());
            view.setTranslateY(view.getTranslateY() + velocity.getY());
            if(view.getTranslateX() + velocity.getX()>root.getWidth()-300||view.getTranslateX() + velocity.getX() < 300 || view.getTranslateY() + velocity.getY() > root.getHeight()-300|| view.getTranslateY() + velocity.getY() < 300  ){
                this.inbound = false;
            } 
        }   
        else{           
            if(velocity.magnitude() < 5){
                if(!(view.getTranslateX() + velocity.getX()>root.getWidth()-300||view.getTranslateX() + velocity.getX() < 300 || view.getTranslateY() + velocity.getY() > root.getHeight()-300|| view.getTranslateY() + velocity.getY() < 300  )){
                    this.inbound = true;
                } 
            }
            
        }
        if (stabilizer) {
            //bigger Decay when stabilizer active
            setVelocity(velocity.multiply(0.995));
            setAcceleration(acceleration.multiply(0.95));
        }
        else {
            //Speed Decay without stabilizer
            setVelocity(velocity.multiply(0.999));
            setAcceleration(acceleration.multiply(0.99));
        }
        

        
    }
    
    @Override
    public boolean isColliding(GameObject other) {
        boolean col = false;
        if (getView().getBoundsInParent().intersects(other.getView().getBoundsInParent())) {
            col = true;
        }
        return col;
    }
  
    
}
