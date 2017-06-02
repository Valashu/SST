package sst;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 *  Asteroid Class, age attribute removes element after it has existed for long enough to prevent memory leaks.
 * @author Marvin Reinold, Janek Bobst
 */
public class Asteroid extends GameObject{
    
    public int health = 100;
    public int age = 2000;
    
    public Asteroid(Node view) {
        super(view);
    }

    
    @Override
    public void update(Pane root) {
        age--;
        if(age <= 0){
            this.setAlive(false);
        }
       super.update(root);
        
    }
    

  
    
}
