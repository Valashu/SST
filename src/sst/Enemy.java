package sst;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * Enemy Ship, have own movement and weapons.
 * @author Marvin Reinold, Janek Bobst
 */
public class Enemy extends GameObject{
    
    int cooldown = 0; // Torpedo cooldown

    public Enemy(Node view) {
        
        super(view);
        this.health = 100;
        this.collider = true;
    }

    
    @Override
    public void update(Pane root) {
        if (health <= 0) {
            this.alive = false;
            this.view.setVisible(false);
        }
       super.update(root);
        
    }
    /**
     * Moves the Ship closer to the player if player is within 1000px.
     * Stops 300px away from player and backs up if player comes within 200px
     * @param player Used for target position
     */
    public void move(Player player){
        Point2D playerPos = new Point2D(player.getView().getTranslateX(), player.getView().getTranslateY());
        Point2D shipPos = new Point2D(this.getView().getTranslateX(), this.getView().getTranslateY());
        if (shipPos.distance(playerPos)> 300 && shipPos.distance(playerPos)< 1000) {
            this.velocity = new Point2D(player.getView().getTranslateX()-this.getView().getTranslateX(), player.getView().getTranslateY()-this.getView().getTranslateY()).normalize().multiply(2);
        }
        else if (shipPos.distance(playerPos)< 200) {
            this.velocity = new Point2D(player.getView().getTranslateX()-this.getView().getTranslateX(), player.getView().getTranslateY()-this.getView().getTranslateY()).normalize().multiply(-1);
        }
        else{
            this.velocity = Point2D.ZERO;
        }
     
     
        if (player.getView().getTranslateX() < this.getView().getTranslateX()) {
            this.getView().setRotate(270+Math.toDegrees(Math.atan((player.getView().getTranslateY()-this.getView().getTranslateY())/(player.getView().getTranslateX()-this.getView().getTranslateX()))));
        }
        else{
            this.getView().setRotate(90+Math.toDegrees(Math.atan((player.getView().getTranslateY()-this.getView().getTranslateY())/(player.getView().getTranslateX()-this.getView().getTranslateX()))));
        }
    }
    
    /**
     * Checks if possible to fire Torpedo
     * To Fire: cooldown must be 0
     * Player needs to be within 800px
     * @param player used to get position of player.
     * @return true if firing is possible
     */
    public boolean fireTorpedo(Player player){
        if (cooldown >0) {
            cooldown--;
        }
        else {
           Point2D playerPos = new Point2D(player.getView().getTranslateX(), player.getView().getTranslateY());
            Point2D shipPos = new Point2D(this.getView().getTranslateX(), this.getView().getTranslateY());
            if (shipPos.distance(playerPos)< 800) {
                return true;
            } 
        }
        
        
        return false;
    }
    

  
    
}
