package sst;

/**
 * State Class, used to switch between game states
 * @author Marvin Reinold, Janek Bobst
 */

public class ActiveState {

    
     public enum state {
        PAUSED,
        PLAYING,
        DEAD,
        WON
        
     }
     private state activeState;
     
        public ActiveState() {
           this.activeState = state.PAUSED;
        }
        public ActiveState(state s) {
            this.activeState = s;
        }
        public void setState(state s){
            this.activeState = s;
        }
        public state getState(){
            return this.activeState;
        }

}
