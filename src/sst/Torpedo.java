package sst;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 *  Torpedo Class, Age attribute removes old Torpedos
 * @author Marvin Reinold, Janek Bobst
 */
public class Torpedo extends GameObject{
    
    public boolean friendly = false;
    public int age = 2000;
    
    public Torpedo(Node view) {
        super(view);
    }

    
    @Override
    public void update(Pane root) {
        age--;
        if(age <= 0){
            this.setAlive(false);
        }
        
        setAcceleration(acceleration.multiply(1.02));
       super.update(root);
        
    }
    
    public void onCollision(){
        this.setAlive(false);
    }

  
    
}
