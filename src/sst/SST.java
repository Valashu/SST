package sst;

import com.sun.javafx.geom.Line2D;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * This is the main class of the application Super Star Trek
 * @author Marvin Reinold, Janek Bobst
 */
public class SST extends Application {

    public final int WINDOW_HEIGHT = 800;
    public final int WINDOW_WIDTH = 1000;
    public Pane root;
    public Random rand = new Random();
    public ActiveState state = new ActiveState();
    public Line velocityLine; //Debug line, visible when in paused state
    public Rectangle lifeBar;
    public Line accelerationLine; //Debug line, visible when in paused state
    public Line nearestEnemyLine; //Debug line, visible when in paused state
    public GameObject stabilizer;
    public GameObject torpedoSignifier;
    public GameObject laserSignifier;
    public int numberOfEnemies = 20; //Adjustable for difficulty, TODO: put in a config file
    public ArrayList<GameObject> objList = new ArrayList<>();
    public ArrayList<GameObject> tempObjList = new ArrayList<>();
   
    private Player player;

    /**
     * Creates the main pane and fills it with all the elements that exist at game start
     * 
     * @return Root element filled with GameObject children
     */
    private Parent createContent() {
        root = new Pane();
        root.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        
        
        
        //Background layer
        Image BI = new Image("img/starfield.jpg");
        GameObject Background = new GameObject(new ImageView(BI));
        Background.setScrollScale(0.01);
        addGameObject(Background, -(BI.getWidth()/2), -(BI.getHeight()/2));
        
        //Decorative elements, nebula and space dust with parralax scrolling. TODO: potentially add more
        Image nebula = new Image("img/nebula.png");
        GameObject neb = new GameObject(new ImageView(nebula));
        neb.setScrollScale(0.5);
        addGameObject(neb, WINDOW_WIDTH/2, WINDOW_HEIGHT/2);
        
        Image dustImg = new Image("img/dust.png");
        GameObject dust = new GameObject(new ImageView(dustImg));
        dust.setScrollScale(0.05);
        addGameObject(dust, -(dustImg.getWidth()/2), -(dustImg.getHeight()/2));
        
        
        //Generates enemies far enough away from the player
        for (int i = 0; i < numberOfEnemies; i++) {
            Image enemyImg = new Image("img/enemy.gif", 50, 44, true, true);
            Enemy e = new Enemy(new ImageView(enemyImg));
            Point2D enemypos = new Point2D(rand.nextInt((int)BI.getWidth())+Background.getView().getTranslateX(), rand.nextInt((int)BI.getHeight())+Background.getView().getTranslateY());
            while (enemypos.distance(WINDOW_WIDTH/2, WINDOW_HEIGHT/2)< 1000) {                
                enemypos = new Point2D(rand.nextInt((int)BI.getWidth())+Background.getView().getTranslateX(), rand.nextInt((int)BI.getHeight())+Background.getView().getTranslateY());
            }
            addGameObject(e, enemypos.getX(), enemypos.getY());
        }
        
        //Player Object
        player = new Player();
        addGameObject(player, WINDOW_WIDTH/2, WINDOW_HEIGHT/2);
        
        //Another decorative layer, in front of the "camera"
        Image dustFieldImg = new Image("img/dustfield.png");
        GameObject dustField = new GameObject(new Rectangle(10000, 10000, new ImagePattern(dustFieldImg)));
        dustField.setScrollScale(2);
        addGameObject(dustField, -(dustFieldImg.getWidth()/2), -(dustFieldImg.getHeight()/2));
        
        //UI elements
        stabilizer = new GameObject(new Circle(20, Color.RED));
        stabilizer.setScrollScale(0);
        addGameObject(stabilizer, 100, WINDOW_HEIGHT-100);
        stabilizer.view.setVisible(player.stabilizer);
        
        torpedoSignifier = new GameObject(new Circle(20, Color.BLUE));
        torpedoSignifier.setScrollScale(0);
        addGameObject(torpedoSignifier, 150, WINDOW_HEIGHT-100);
        torpedoSignifier.view.setVisible(true);
        
        laserSignifier = new GameObject(new Rectangle(40, 20, Color.BLUE));
        laserSignifier.setScrollScale(0);
        addGameObject(laserSignifier, 130, WINDOW_HEIGHT-150);
        laserSignifier.view.setVisible(true);
        
        lifeBar = new Rectangle(100, 20);
        lifeBar.setFill(Color.DARKGREEN);
        GameObject lifeBarObj = new GameObject(lifeBar);
        lifeBarObj.setScrollScale(0);
        addGameObject(lifeBarObj, 50, WINDOW_HEIGHT-50);
        
        //Debug Lines
        velocityLine = new Line(0, 0, 0,0);
        velocityLine.setStroke(Color.RED);
        root.getChildren().add(velocityLine);
        accelerationLine = new Line(0, 0, 0,0);
        accelerationLine.setStroke(Color.BLUE);
        root.getChildren().add(accelerationLine);
        nearestEnemyLine = new Line(0, 0, 0,0);
        nearestEnemyLine.setStroke(Color.GREEN);
        root.getChildren().add(nearestEnemyLine);
        
        
        //Text elements
        Text HPtext = new Text(50, WINDOW_HEIGHT-30, "100");
        HPtext.setFont(Font.font("Verdana", 20));
        HPtext.setFill(Color.RED);
        root.getChildren().add(HPtext);
        
        Text wonText = new Text(100, 100, "You Won!");
        wonText.setFont(Font.font("Verdana", 100));
        wonText.setFill(Color.RED);
        root.getChildren().add(wonText);
        wonText.setVisible(false);
        
        Text EnemiesRemaining = new Text(100, 100, "Enemies Remaining: "+numberOfEnemies);
        EnemiesRemaining.setFont(Font.font("Verdana", 20));
        EnemiesRemaining.setFill(Color.RED);
        root.getChildren().add(EnemiesRemaining);
        
        
        //Declaration of main game loop called each frame
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                //switching the active game state
                if (null != state.getState()) switch (state.getState()) {
                    case PLAYING:
                        HPtext.setText(Integer.toString(player.health));
                        onUpdatePlaying(root);
                        break;
                    case PAUSED:
                        onUpdatePaused(root);
                        break;
                    case WON:
                        wonText.setVisible(true);
                        EnemiesRemaining.setVisible(false);
                        for (GameObject obj : objList) {
                            obj.setAlive(false);
                        }   nearestEnemyLine.setVisible(false);
                        accelerationLine.setVisible(false);
                        velocityLine.setVisible(false);
                        HPtext.setVisible(false);
                        break;
                    default:
                        break;
                }
                //deleting dead object
                for (GameObject obj : objList) {
                    if (obj.isDead()) {
                        root.getChildren().remove(obj.view);
                    }
                }
                objList.removeIf(GameObject::isDead);
                for (GameObject obj : tempObjList) {
                    objList.add(obj);
                }
                tempObjList.clear();
                
                
                //Checking if game is won
                int enemies = 0;
                for (GameObject obj : objList) {
                    if (obj.getClass() == Enemy.class) {
                        enemies++;
                    }
                }
                if (enemies == 0) {
                    state.setState(ActiveState.state.WON);
                }
                numberOfEnemies = enemies;
                EnemiesRemaining.setText("Enemies Remaining: "+numberOfEnemies);
            }
        };
        timer.start();

        return root;
    }
/**
 * Adds a GameObject to the masterlist of all active gameObjects
 * 
 * @param object    The object that should be added to the list of game Objects
 * @param x         X-coordinate of Object
 * @param y         Y-coordinate of Object
 */
    private void addGameObject(GameObject object, double x, double y) {
        object.getView().setTranslateX(x);
        object.getView().setTranslateY(y);
        objList.add(object);
        root.getChildren().add(object.getView());
    }
    /**
     * The TempObjList is only used to add objects that are created while iterating over the normal objList
     * the list gets cleared every update, all objects on it transferred to the normal objList
     * 
     * @param object    The object that should be added to the list of game Objects
     * @param x         X-coordinate of Object
     * @param y         Y-coordinate of Object
     */
    private void addTempGameObject(GameObject object, double x, double y) {
        object.getView().setTranslateX(x);
        object.getView().setTranslateY(y);
        tempObjList.add(object);
        root.getChildren().add(object.getView());
    }
/**
 * Game Logic while game is neither won, lost nor paused. Moves all elements and checks collisions
 * 
 * @param root Root Pane with child objects 
 */
    private void onUpdatePlaying(Pane root) {
        player.update(root);
        //drawLines(root); //DEBUG
        
        //Player cooldowns ticking down
        if (player.torpedoCooldown > 0) {
            player.torpedoCooldown--;
        }
        else{
            torpedoSignifier.getView().setVisible(true);
        }
        if (player.laserCooldown > 0) {
            player.laserCooldown--;
        }
        else{
            laserSignifier.getView().setVisible(true);
        }
        
        
        createAsteroid();
        
        
        for (GameObject obj : objList) {
            if (obj != player) {
                if (player.inbound == false) {
                    //Moves objects around the player if he reached the view boundaries
                    obj.view.setTranslateX(obj.view.getTranslateX()-(player.velocity.getX()*obj.getScrollScale()));
                    obj.view.setTranslateY(obj.view.getTranslateY()-(player.velocity.getY()*obj.getScrollScale()));
                }
                
                
                obj.update(root);
            }
            if (obj.getClass() == Enemy.class) {
                //Enemy ship movement and firing
                    ((Enemy)obj).move(player);
                    if (((Enemy)obj).fireTorpedo(player)) {
                       ((Enemy)obj).cooldown = 1000;
                        fireTorpedo(obj, 25, 22, obj.getRotate()-90, false);
                    }
                }
            if (obj.getCollider()) {
                /**
                 * Collision handling
                 * Colliding elements are:
                 * Player
                 * Enemy
                 * Asteroid
                 * Laser
                 * Torpedo(both enemy and friendly)
                 * 
                 * The following collisions are unhandled, because they either do nothing by design(1), can't happen(2) or are yet to be implemented(3):
                 * Player/Player    (2)
                 * Player/Laser     (1)
                 * Player/Friendly Torpedo  (1)
                 * Laser/Friendly Torpedo   (1)
                 * Laser/Laser      (2)
                 * Friendly/Friendly and Enemy/Enemy Torpedo    (1)
                 * Enemy Torpedo/Enemy Ship (1)
                 * Enemy Ship / Enemy Ship (3)
                 */
                if(player.isColliding(obj)&&obj.getClass() == Asteroid.class){
                    //Player hits asteroid
                    obj.setAlive(false);
                    obj.view.setVisible(false);
                    player.health -= (5+rand.nextInt(5));
                    if (player.health < 0) {
                        player.health = 0;
                        state.setState(ActiveState.state.DEAD);
                        player.view.setVisible(false);
                    }
                    if (obj.health < 0) {
                     obj.alive = false;   
                    }
                    lifeBar.setWidth(player.health);
                }
                else if(player.isColliding(obj)&&obj.getClass() == Enemy.class){
                    //Player Hits enemy
                    obj.health -= (5+rand.nextInt(5));
                    player.health -= (5+rand.nextInt(5));
                    if (player.health < 0) {
                        player.health = 0;
                        state.setState(ActiveState.state.DEAD);
                        player.view.setVisible(false);
                    }
                    lifeBar.setWidth(player.health);
                }
                else if(player.isColliding(obj)&&obj.getClass() == Torpedo.class){
                    if (!((Torpedo)obj).friendly) {
                        //Player hits enemy torpedo
                        player.health -= (10+rand.nextInt(15));
                        obj.setAlive(false);
                        obj.view.setVisible(false);
                        if (player.health < 0) {
                            player.health = 0;
                            state.setState(ActiveState.state.DEAD);
                            player.view.setVisible(false);
                        }
                        lifeBar.setWidth(player.health);
                    }
                }
                if (obj.getClass() == Torpedo.class) {
                    for (GameObject obj2 : objList) {
                        if (obj != obj2 &&  obj2.getCollider() && (obj2.getClass() == Laser.class)) {
                            if (!((Torpedo)obj).friendly) {
                                //Laser Hits Enemy torpedo
                                boolean col = checkLineIntersect(((Laser)obj2).lineInternal, obj);
                                if (col) {
                                    obj.setAlive(false);
                                    obj.view.setVisible(false);
                                }
                            }
                        }
                        else if (obj != obj2 &&  obj2.getCollider() && (obj2.getClass() == Torpedo.class)) {
                            if (obj.isColliding(obj2)&&((Torpedo)obj).friendly != ((Torpedo)obj2).friendly) {
                                //Enemy Torpedo hits Friendly torpedo
                                obj.setAlive(false);
                                obj.view.setVisible(false);
                                obj2.setAlive(false);
                                obj2.view.setVisible(false);
                            }
                        }
                    }
                }
                if (obj.getClass() == Enemy.class) {
                    for (GameObject obj2 : objList) {
                        if (obj != obj2 &&  obj2.getCollider() && (obj2.getClass() == Laser.class)) {
                            //Laser hits Enemy
                            boolean col = checkLineIntersect(((Laser)obj2).lineInternal, obj);
                            if (col) {
                                obj.health -= 80+rand.nextInt(50);
                            }
                        }
                        else if (obj != obj2 &&  obj2.getCollider() && (obj2.getClass() == Torpedo.class)) {
                            if (obj.isColliding(obj2)&&((Torpedo)obj2).friendly) {
                                //Friendly Torpedo hits Enemy
                                obj.setAlive(false);
                                obj.health = 0;
                                obj.view.setVisible(false);
                                obj2.setAlive(false);
                                obj2.view.setVisible(false);
                            }
                        }
                    }
                }
                if (obj.getClass() == Asteroid.class) {
                    for (GameObject obj2 : objList) {
                        if (obj != obj2 &&  obj2.getCollider() && (obj2.getClass() == Asteroid.class||obj2.getClass() == Torpedo.class)) {
                            if (obj.isColliding(obj2)) {
                                //Torpedo hits Asteroid
                                obj.setAlive(false);
                                obj.view.setVisible(false);
                                obj2.setAlive(false);
                                obj2.view.setVisible(false);
                            }
                        }
                        else if (obj != obj2 && obj2.getCollider() && (obj2.getClass() == Laser.class)) {
                            boolean col = checkLineIntersect(((Laser)obj2).lineInternal, obj);
                            //Laser Hits Asteroid
                            if (col) {
                                obj.setAlive(false);
                                obj.view.setVisible(false);
                            }
                            
                        }
                        else if (obj != obj2 &&  obj2.getCollider() && (obj2.getClass() == Enemy.class)) {
                            if (obj.isColliding(obj2)) {
                                //Enemy hits Asteroid
                                obj.setAlive(false);
                                obj.view.setVisible(false);
                                obj2.health -=rand.nextInt(5);
                            }
                        }
                    }
                }
            }
        }
        
    }
    /**
     * Game Logic when game is Paused, shows debug Lines TODO: Implement more tactical features for pause screen
     * @param root Root Pane with child objects
     */
    private void onUpdatePaused(Pane root) {
        drawLines();
        for (GameObject obj : objList) {
            if (obj != player) {
                obj.drawVelocityLine(root);
            }
        }
        
    }
    /**
     * //Draws Debug and Help Lines

     */
    private void drawLines(){
        
        velocityLine.setStartX(player.getView().getTranslateX()+25);
        velocityLine.setStartY(player.getView().getTranslateY()+11);
        velocityLine.setEndX(velocityLine.getStartX()+(player.getVelocity().getX())*10);
        velocityLine.setEndY(velocityLine.getStartY()+(player.getVelocity().getY())*10);
        velocityLine.setVisible(true);
        accelerationLine.setStartX(velocityLine.getEndX());
        accelerationLine.setStartY(velocityLine.getEndY());
        accelerationLine.setEndX(accelerationLine.getStartX()+(player.getAcceleration().getX())*10);
        accelerationLine.setEndY(accelerationLine.getStartY()+(player.getAcceleration().getY())*10);
        accelerationLine.setVisible(true);
        double dist = 0;
        Point2D end = Point2D.ZERO;
        for (GameObject obj : objList) {
            if (obj.getClass()==Enemy.class) {
                if (dist == 0) {
                    dist = player.positionToPoint().distance(obj.positionToPoint());
                    end = obj.positionToPoint();
                }
                else if (player.positionToPoint().distance(obj.positionToPoint())<dist) {
                    dist = player.positionToPoint().distance(obj.positionToPoint());
                    end = obj.positionToPoint();
                }
            }
        }
        if(end != Point2D.ZERO){
            nearestEnemyLine.setStartX(player.getView().getTranslateX()+25);
            nearestEnemyLine.setStartY(player.getView().getTranslateY()+11);
            nearestEnemyLine.setEndX(end.getX());
            nearestEnemyLine.setEndY(end.getY());
            nearestEnemyLine.setVisible(true);
        }
        
        
    }
/**
 * Start method, initializes scene and create InputHandler
 * @param stage Main Stage
 */
    @Override
    public void start(Stage stage) throws Exception {
        stage.setResizable(false);
        stage.setScene(new Scene(createContent()));
        stage.getScene().setOnKeyPressed(e -> {
            if (state.getState() == ActiveState.state.PLAYING ) {
                //Key Handling in playing state
                if (e.getCode() == KeyCode.LEFT) {
                  player.rotateLeft();
                }
                else if (e.getCode() == KeyCode.A) {
                  player.boostLeft();
                }
                else if (e.getCode() == KeyCode.D) {
                  player.boostRight();
                }
                else if (e.getCode() == KeyCode.X) {
                    fireTorpedo(player,25,11, player.getRotate(), true);
                    torpedoSignifier.getView().setVisible(false);
                    player.torpedoCooldown = 300;
                }
                else if (e.getCode() == KeyCode.C) {
                  fireLaser();
                }
                else if (e.getCode() == KeyCode.RIGHT) {
                  player.rotateRight();
                }
                else if (e.getCode() == KeyCode.UP) {
                  player.boostForward();
                }
                else if (e.getCode() == KeyCode.DOWN) {
                  player.boostBackward();
                }
                else if (e.getCode() == KeyCode.SPACE) {
                    state.setState(ActiveState.state.PAUSED);
                }
                else if (e.getCode() == KeyCode.T) {
                    player.stabilizer = !player.stabilizer;
                    stabilizer.view.setVisible(player.stabilizer);
                    
                }
                else{
                }
            }
            else if (state.getState() == ActiveState.state.PAUSED) {
                //Key Handling in paused state
                if (e.getCode() == KeyCode.SPACE) {
                    state.setState(ActiveState.state.PLAYING);
                    velocityLine.setVisible(false);
                    accelerationLine.setVisible(false);
                    nearestEnemyLine.setVisible(false);
                    for (GameObject obj : objList) {
                        obj.clearVelocityLine();
                    }
                }
            }
            
        });
        stage.show();
    }
/**
 * Main Method, needed to launch game
 * @param args 
 */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Fires a Torpedo from source, used by both player and enemies
     * @param source    GameObject that fires off the torpedo, used get position and retain speed
     * @param offx      Offset used to center firing point
     * @param offy      Offset used to center firing point
     * @param rotation  Rotation of Torpedo, used for firing direction
     * @param friendly  Boolean that designates torpedo as friendly to player or not
     */
    private void fireTorpedo(GameObject source, int offx, int offy, double rotation, boolean friendly) {
        if (player.torpedoCooldown == 0) {
            Circle torCircle = new Circle(15,new ImagePattern(new Image("img/torpedo.png")));
            Torpedo torpedo = new Torpedo(torCircle);
            addTempGameObject(torpedo, source.getView().getTranslateX()+offx,source.getView().getTranslateY()+offy);
            torpedo.getView().setRotate(rotation);
            torpedo.setAcceleration(torpedo.getDirectionNormal().multiply(0.01));
            torpedo.setVelocity(source.velocity);
            torpedo.setCollider(true);
            torpedo.friendly = friendly;
            
        }
            
    }
    /**
     * Creates an asteroid based on chance. Asteroids are passive obstacle for the player
     */
    private void createAsteroid() {
        int  n = rand.nextInt(100) + 1;
        if (n == 5) {
            n = rand.nextInt(4) + 1;
            Circle astCircle = new Circle(15,new ImagePattern(new Image("img/asteroid1.png")));
            Asteroid asteroid = new Asteroid(astCircle);
            if (n == 1) {
                addGameObject(asteroid, rand.nextInt(WINDOW_WIDTH), 0);
                asteroid.addVelocity(new Point2D(rand.nextFloat()-0.5, 0.5));  
            }
            if (n == 2) {
                addGameObject(asteroid, rand.nextInt(WINDOW_WIDTH), WINDOW_HEIGHT);
                asteroid.addVelocity(new Point2D(rand.nextFloat()-0.5, -0.5));  
            }
            if (n == 3) {
                addGameObject(asteroid, WINDOW_WIDTH, rand.nextInt(WINDOW_HEIGHT));
                asteroid.addVelocity(new Point2D(-0.5, rand.nextFloat()-0.5));  
            }
            if (n == 4) {
                addGameObject(asteroid, 0, rand.nextInt(WINDOW_HEIGHT));
                asteroid.addVelocity(new Point2D(0.5, rand.nextFloat()-0.5));  
            }
            asteroid.setCollider(true);
            asteroid.getView().setRotate(rand.nextInt(360));
            objList.add(asteroid);
        }
    }

    /**
     * Fires the laser of the player if the cooldown has ticked down. The laser affects asteroids, Enemy Ships and Torpedos in a straight line.
     */
    private void fireLaser() {
        if (player.laserCooldown == 0) {
            Line laserLine = new Line();
            laserLine.setStroke(Color.WHEAT);
            laserLine.setStrokeWidth(2);
            
            Laser laser = new Laser(laserLine);
            addGameObject(laser, 0,0);
            Point2D dir = player.getDirectionNormal();
            laserLine.setStartX(player.getView().getTranslateX()+25);
            laserLine.setStartY(player.getView().getTranslateY()+11);
            laserLine.setEndX(laserLine.getStartX()+(dir.getX())*400);
            laserLine.setEndY(laserLine.getStartY()+(dir.getY())*400);
            laser.setCollider(true);
            laser.lineInternal = new Line2D((float) laserLine.getStartX(), (float) laserLine.getStartY(), (float) laserLine.getEndX(), (float) laserLine.getEndY());
            objList.add(laser);
            laserSignifier.getView().setVisible(false);
            player.laserCooldown = 100;
        }
    }
    
    /**
     * Checks if a line interects a boundary box of a game Object. Used for Laser collisions
     * @param l1    Line object to test intersection
     * @param obj   GameObject to test intersection
     * @return boolean, true for intersection of l1 and obj
     */
    private boolean checkLineIntersect(Line2D l1, GameObject obj){
        boolean col = false;
        if (l1.intersectsLine((float)obj.getView().getBoundsInParent().getMaxX(), (float)obj.getView().getBoundsInParent().getMaxY(), (float)(obj.getView().getBoundsInParent().getMaxX()), (float)(obj.getView().getBoundsInParent().getMinY()))) {
            //check right side
            col = true;
        }
        else if (l1.intersectsLine((float)(obj.getView().getBoundsInParent().getMinX()), (float)(obj.getView().getBoundsInParent().getMaxY()), (float)(obj.getView().getBoundsInParent().getMaxX()), (float)(obj.getView().getBoundsInParent().getMaxY()))) {
            //check lower side
            col = true;
        }
        else if (l1.intersectsLine((float)(obj.getView().getBoundsInParent().getMinX()), (float)(obj.getView().getBoundsInParent().getMinY()), (float)(obj.getView().getBoundsInParent().getMinX()), (float)(obj.getView().getBoundsInParent().getMaxY()))) {
            //check left side
            col = true;
        }
        else if (l1.intersectsLine((float)(obj.getView().getBoundsInParent().getMinX()), (float)(obj.getView().getBoundsInParent().getMinY()), (float)(obj.getView().getBoundsInParent().getMaxX()), (float)(obj.getView().getBoundsInParent().getMinY()))) {
            //check upper side
            col = true;
        }
        return col;
    }
}