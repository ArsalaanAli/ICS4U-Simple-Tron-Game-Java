import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;

public class TronMerge {

    public TronMerge() {

    }

    public static void main(String [] args){
    	Tron_Menu frame = new Tron_Menu();
    }
}

class Tron_Menu extends JFrame implements ActionListener {
    JButton startButton, insButton, start2Button;
    Image TitleScreen = new ImageIcon("Images/Title Screen.png").getImage();
	int page = 0;
	JPanel instuctions;
    public Tron_Menu() {

    	super("TRON MENU");
    	Container pane = getContentPane();
    	pane.setLayout(null);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        repaint();
    	startButton = new JButton("1 Player");
    	startButton.setSize(150, 50);
    	startButton.setLocation(125, 240);
    	startButton.addActionListener(this);
        start2Button = new JButton("2 Players");
        start2Button.setSize(150, 50);
    	start2Button.setLocation(325, 240);
    	start2Button.addActionListener(this);
        insButton = new JButton("INSTRUCTIONS");
    	insButton.setSize(150, 50);
    	insButton.setLocation(525, 240);
    	insButton.addActionListener(this);
    	pane.add(insButton);
        pane.add(startButton);
        pane.add(start2Button);
        
        setVisible(true);

    }
    public void paint(Graphics g){
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, 850, 650);
        g.drawImage(TitleScreen, 10, 0, null);
    }

    public void actionPerformed(ActionEvent ev){
    	if(ev.getSource() == startButton){
    		page = 1;
    		setVisible(false);
    		Tron gameFrame = new Tron(1);
        }
        if(ev.getSource() == start2Button){
    		page = 1;
    		setVisible(false);
    		Tron gameFrame = new Tron(2);
        }
    	else if(ev.getSource() == insButton){
    		page = 2;
    		//instuctions.setVisible(true);
    	}
    }
}

class Tron extends JFrame{
    javax.swing.Timer myTimer;
    GamePanel1 game;
    private int players;

    public Tron(int players) {
		super("(T)ron");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800,650);
        this.players = players;
		javax.swing.Timer myTimer = new javax.swing.Timer(15, new TickListener());
        myTimer.start();
		game = new GamePanel1(players);
		add(game);
		setResizable(false);
		setVisible(true);
    }

	class TickListener implements ActionListener{
        public static final int WIN = 1, LOSE = 2, TIE = 3;
        private boolean running = true;
        public int state = 0;
		public void actionPerformed(ActionEvent evt){
			if(game!= null && game.ready){
                game.timer();
                if(running){
                    // game.move();//function for moving player
                    if(players == 1){
                        game.enemyAI();//enemy ai is run if selected 1-player option
                    }
                    else{
                        game.enemyMove();//else keyboard control is given to second bike
                    }
                    game.repaint();
                    if(game.checkFinished() != 0){//IF A GAME FINISHING EVENT HAS OCCURED
                        state = game.checkFinished();
                        if(state == WIN){
                            System.out.println("WIN");
                        }
                        if(state == LOSE){
                            System.out.println("LOSE");          
                        }
                        if(state == TIE){
                            System.out.println("TIE");         
                        }
                        running = false;
                    }
                }
			}
		}
	}

}
class GamePanel1 extends JPanel{
    public static final int LEFT = 1, RIGHT = 2, UP = 3, DOWN =4;//final ints so no magic numbers
    public static final int WIN = 1, LOSE = 2, TIE = 3;
    public static final int playerTrail = 5, enemyTrail = 6;
    private int backDelay = 0;
    public final int width = 800, height = 600;
    private int[][] board = new int[170][150];//board used for collision points
    private boolean []keys;
    private boolean playerDead = false, enemyDead = false, tie = false;
    private boolean drawBackground = true;
    private bike player, enemy;
    private int time = 1, ms = 0, randTimeLow = 5, randTimeHigh = 15;
    private int timeChangeDir = randint(15, 20);//timer that randomly goes off
    public boolean ready = false;
    private int[] otherTurns = {0, RIGHT, LEFT, DOWN, UP};//array with the opposite turn for each index of turns
    public GamePanel1(int players){
        player = new bike(50, 320, RIGHT);//initializes bikes for player and enemy
        enemy = new bike(650, 320, LEFT);
        keys = new boolean[KeyEvent.KEY_LAST+1];
        addKeyListener(new moveListener());
        setSize(800,600);
    }
    public void addNotify() {
        super.addNotify();
        requestFocus();
        ready = true;
    }
    public void move(){
       if(keys[KeyEvent.VK_S]){//setting player's direction based on input
           player.addDir(DOWN);
       }
       if(keys[KeyEvent.VK_A]){
        player.addDir(LEFT);
        }
        if(keys[KeyEvent.VK_W]){
            player.addDir(UP);
        }
        if(keys[KeyEvent.VK_D]){
            player.addDir(RIGHT);
       }
       player.setDir();//repeadetly checks if player can turn
       player.move();//moves the rect of the player
       if (player.hitEdgde()){
           playerDead = true;//checks if player has gone off screen
       }
       if(collide(player, enemyTrail)){//checks for collision with enemy
           if(tie()){
               tie = true;
           }
           else{
               playerDead = true;
           }
        }
       if(collide(player, playerTrail)){//checks for collision with self
            playerDead = true;
       }
       fillBoard(player, playerTrail);//adds players position to collision board
    }
    public void enemyMove(){//same as player move
        if(keys[KeyEvent.VK_DOWN]){
            enemy.addDir(DOWN);
        }
        if(keys[KeyEvent.VK_LEFT]){
         enemy.addDir(LEFT);
         }
         if(keys[KeyEvent.VK_UP]){
            enemy.addDir(UP);
         }
         if(keys[KeyEvent.VK_RIGHT]){
            enemy.addDir(RIGHT);
        }
        enemy.setDir();
        enemy.move();
        if (enemy.hitEdgde()){
            enemyDead = true;
        }
        if(collide(enemy, playerTrail)){
            if(tie()){
                tie = true;
            }
            else{
                enemyDead = true;
            }
         }
        if(collide(enemy, enemyTrail)){
             enemyDead = true;
        }
        fillBoard(enemy, enemyTrail);
     }
    public void enemyAI(){//ai function
        enemyDetection();//checks to see if path is clear, or turn needs to be made
        enemy.closeToBorder();//checks to see if player is getting too close to border, if so turn is made
        if(time >= timeChangeDir){//when random turn timer goes off
            if(enemy.onBorder()){//checks to see if enemy is travelling along border
                System.out.println("BORDER TURN");
                int dc = enemy.borderTurn();//turns away from border
                System.out.println(dc);
                enemy.addDir(dc);
                time = 0;
                timeChangeDir = randint(randTimeLow, randTimeHigh);
            }
            else{
                System.out.println("Random turn");
                int dc = enemy.randomDir();//if not on border, then randomly turns
                if (safeToTurn(dc)){
                    enemy.addDir(dc);
                }
                time = 0;
                timeChangeDir = randint(randTimeLow, randTimeHigh);//reset random turn timer
            }
        }
        enemy.setDir();//repeadetly checks if player can turn
        enemy.move();//moves the rect of the enemy
        if(collide(enemy, playerTrail)){//checks to see if enemy has collided with player
            if(tie()){
                tie = true;
            }
            else{
                enemyDead = true;
            }
         }
        if(collide(enemy, enemyTrail)){//checks if enemy has collided with self
             enemyDead = true;
        }
        if (enemy.hitEdgde()){//checks in enemy has gone of screen
            enemyDead = true;
        } 
        fillBoard(enemy, enemyTrail);//fills collision board with player position
    }

    public void paint(Graphics g){
        if(drawBackground){//draws background
            g.setColor(new Color(0, 0, 45));//background colour
            g.fillRect(0, 0, width + 30, height + 30);
            if(backDelay>2){//this delay was needed or sometimes the background wouldn't blit
                drawBackground = false;//after background is blit, it doesn't need to be  blit anymore
            }
            backDelay++;
        }
        g.setColor(new Color(187,155,252));
        player.draw(g);//drawing player
        g.setColor(new Color(253, 255,161));
        enemy.draw(g);//drawing enemy
    }
    public void timer(){
        ms++;
        if (ms%10==0){
            time++;//timer used for game time
        }
    }
    public void fillBoard(bike b, int trail){
        board[b.getX()/5][b.getY()/5] = trail;//fills board with bike's trail at bike's position
    }
    public boolean collide(bike b, int otherTrail){
        if(board[b.getX()/5][b.getY()/5] == otherTrail){//checks if bike has run into trial
            return true;
        }
        return false;
    }
    public boolean tie(){
        if(player.getX()/5 == enemy.getX()/5 && player.getY()/5 == enemy.getY()/5){//checks in player runs head on into enemy
            return true;
        }
        return false;
    }
    public int checkFinished(){//checks if game finishing event has occured
        if(tie){
            return TIE;
        }
        else if(playerDead){
            return LOSE;
        }
        else if(enemyDead){
            return WIN;
        }
        return 0;
    }
    public void enemyDetection(){
        if(board[(enemy.getX()+enemy.getxDir()*4)/5][(enemy.getY()+enemy.getyDir()*4)/5] != 0){//looks ahead 4 points to see if path is clear
            int dc = enemy.randomDir();//gets randome direction
                if (safeToTurn(dc)){
                    enemy.addDir(dc);//if safe to turn, turns in that direction
                }
                else{
                    enemy.addDir(otherTurns[dc]);//turns in other direciton if not safe
                }
        }
        else if( (enemy.getX()+enemy.getxDir()*5)/5 < 5 || (enemy.getX()+enemy.getxDir()*5)/5 > 165){
            int dc = enemy.randomDir();//same as code before, but this time checking for boundaries
                if (safeToTurn(dc)){
                    enemy.addDir(dc);
                }
                else{
                    enemy.addDir(otherTurns[dc]);
                }
        }
    }
    public boolean safeToTurn(int dc){//checks if direction being tunred to is safe
        boolean stt = true;
        int potentialX = xSpeed(dc);
        int potentialY = ySpeed(dc);//gets xdir and ydir for dc
        for(int i = 1; i<=4; i++){
            if(board[(enemy.getX()+potentialX*i)/5][(enemy.getY()+potentialY*i)/5] != 0){//checks if 4 squares in that direction are safe 
                stt = false;
                break;
            }
        }

        return stt;
    }
    public int xSpeed(int dc){//returns x speed in certain direction
        if(dc == LEFT){
            return -5;
        }
        else if (dc == RIGHT){
            return 5;
        }
        return 0;
    }
    public int ySpeed(int dc){//returns y speed in certain direction
        if(dc == UP){
            return -5;
        }
        else if (dc == DOWN){
            return 5;
        }
        return 0;
    }
    public static int randint(int low, int high){
		return (int)(Math.random()*(high-low+1)+low);
	}
    class moveListener implements KeyListener{
	    public void keyTyped(KeyEvent e) {}

	    public void keyPressed(KeyEvent e) {
	        keys[e.getKeyCode()] = true;
	    }

	    public void keyReleased(KeyEvent e) {
	        keys[e.getKeyCode()] = false;
	    }
    }
}
class bike{//CLASS MADE------------------------------------------------------------------------------------------------
    public static final int LEFT = 1, RIGHT = 2, UP = 3, DOWN =4;
    public static final int pixelOffset = 10;
    public static final int speed = 5;
    public final int width = 800, height = 600;
    private int x, y, xdir, ydir;
    private int dir;
    private int leftBorderEdge = 30, rightBorderEdge = 30, topBorderEdge = 30, botBorderEdge = 30;
    private boolean yBorder = false, xBorder = true;
    // private Queue<Integer> queuedMoves = new LinkedList<>();
    private int moveFrame = 0;
    private int curMove = 0;

    public bike(int x, int y, int dir){//creates bike at certain position, starting in certain direction
        this.x = x;
        this.y = y;
        if(dir == LEFT){
            this.dir = LEFT;
            this.xdir = -speed;
        }
        if(dir == RIGHT){
            this.dir = RIGHT;
            this.xdir = speed;
        }
    }
    public void move(){//increases bike's position by speed in that direction
        x+=xdir;
        y+=ydir;
        System.out.println(x);
        
    }
    public void addDir(int d){//used to prompt change in direction
        curMove = d;
    }
    public void setDir(){
        if(curMove == 0){
            return;//if no direction to be moved in, do nothing
        }
        if(curMove == LEFT || curMove == RIGHT){//these check if bike is at proper position to turn, if so the bike is turned
            if(y%pixelOffset==0){
                dirChange(curMove);
                curMove = 0;
            }
        }
        if(curMove == UP || curMove == DOWN){
            if(x%pixelOffset == 0){
                dirChange(curMove);
                curMove = 0;
            }
        }
    }
    public void dirChange(int d){//turns the bike to direction passed through
        if(d==LEFT && dir != RIGHT){
            xdir = -speed;
            ydir = 0;
            dir = LEFT;
        }
        else if(d==RIGHT && dir != LEFT){
            xdir = speed;
            ydir = 0;
            dir = RIGHT;
        }
        else if(d==UP && dir != DOWN){
            ydir = -speed;
            xdir = 0;
            dir = UP;
        }
        else if(d==DOWN && dir != UP){
            ydir = speed;
            xdir = 0;
            dir = DOWN;
        }
    }
    public int randomDir(){//returns random direction to turn to
        int ch = randint(0, 1);
        int dchange = 0;
        if(dir == UP || dir == DOWN){
            dchange = (ch == 0) ? LEFT:RIGHT;
        }
        else if(dir == LEFT || dir == RIGHT){
            dchange = (ch == 0) ? UP:DOWN;
        }
        return dchange;
    }
    public void closeToBorder(){//checks if on or past edge borders of game
        if(xBorder){
            if(x < width - rightBorderEdge && x > leftBorderEdge){//if previously was on border, and now is not, the border boolean is changed to false
                xBorder = false;
            }
        }
        if(yBorder){
            if(y < height - botBorderEdge && y > topBorderEdge){
                yBorder = false;
            }
        }
        if((x >= width - rightBorderEdge-10 || x <= leftBorderEdge+10) && !xBorder){//checks if on border of screen, if so turns the bike
            System.out.println("border turn2");
            if(y < height / 2){  
                addDir(DOWN);
            }
            else{
                addDir(UP);
            }
            xBorder = true;
        }
        else if(y >= height - botBorderEdge || y <= topBorderEdge && !yBorder){
            System.out.println("border turn2");
            if(x<width/2){
                addDir(RIGHT);
            }
            else{
                addDir(LEFT);
            }
            yBorder = true;
        }
    }
    public boolean onBorder(){//returns true if bike is on a border of the screen
        if(x>= width-leftBorderEdge-5 || x<=rightBorderEdge+5 || y>=height-botBorderEdge-5|| y<=topBorderEdge+5){
            return true;
        }
        return false;
    }
    public int borderTurn(){//returns direction to turn if on border
        System.out.println(x + " " + (width - rightBorderEdge) + " " + leftBorderEdge);
        if((x >= width - rightBorderEdge || x <= leftBorderEdge) && !xBorder){//checks if on border of screen, if so turns the bike
            System.out.println("border turn2");
            if(y < height / 2){  
                return DOWN;
            }
            else{
                return UP;
            }
        }
        else if(y >= height - botBorderEdge || y <= topBorderEdge && !yBorder){
            System.out.println("border turn2");
            if(x<width/2){
                return RIGHT;
            }
            else{
                return LEFT;
            }
        }
        return 0;
    }
    public boolean hitEdgde(){//returns if bike has hit the edge of the screen
        if(x<5 || x> width-5 || y<5 || y>height-5){
            return true;
        }
        return false;
    }
    //GETTER AND SETTERS
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getxDir(){
        return xdir;
    }
    public int getyDir(){
        return ydir;
    }
    
    public void draw(Graphics g){//draws bike rectangle
        g.fillRect(x, y, 10, 10);
    }
    public static int randint(int low, int high){//randint function
		return (int)(Math.random()*(high-low+1)+low);
	}
}
