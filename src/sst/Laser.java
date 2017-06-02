package sst;

import com.sun.javafx.geom.Line2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 *  Laser Class, Laser survive for less than a second, but need to be shown for more than a single frame. Laser is only active for the first 2 frames, the other 18 are visual only.
 * @author Marvin Reinold, Janek Bobst
 */
public class Laser extends GameObject{
    

    public int age = 20;
    public Line2D lineInternal;
    public Laser(Node view) {
        super(view);
    }

    
    @Override
    public void update(Pane root) {
        age--;
        if(age <= 0){
            this.setAlive(false);
        }
        else if(age < 18){
            this.collider = false;
        }
        
       super.update(root);
        
    }

  
    
}
