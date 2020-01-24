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
	JButton startButton, insButton;
	int page = 0;
	JPanel instuctions;
    public Tron_Menu() {
    	super("TRON MENU");
    	Container pane = getContentPane();
    	pane.setLayout(null);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    	setSize(800, 600);
    	startButton = new JButton("START");
    	startButton.setSize(150, 50);
    	startButton.setLocation(325, 175);
    	startButton.addActionListener(this);
    	insButton = new JButton("INSTRUCTIONS");
    	insButton.setSize(150, 50);
    	insButton.setLocation(325, 275);
    	insButton.addActionListener(this);

    	pane.add(insButton);
    	pane.add(startButton);

    	setVisible(true);
    }

    public void actionPerformed(ActionEvent ev){
    	if(ev.getSource() == startButton){
    		page = 1;
    		setVisible(false);
    		Tron gameFrame = new Tron();
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

    public Tron() {
		super("Tron");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,650);
		javax.swing.Timer myTimer = new javax.swing.Timer(10, new TickListener());	 // trigger every 100 ms
		myTimer.start();
		game = new GamePanel1();
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
                    // game.move();//GAME FUNCTIONS ARE CALLED
                    game.enemyAI();
                    game.repaint();
                    if(game.checkFinished() != 0){
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
    public static final int LEFT = 1, RIGHT = 2, UP = 3, DOWN =4;
    public static final int WIN = 1, LOSE = 2, TIE = 3;
    public static final int playerTrail = 5, enemyTrail = 6;
    private int backDelay = 0;
    public final int width = 800, height = 600;
    private int[][] board = new int[170][150];
    private boolean []keys;
    private boolean playerDead = false, enemyDead = false, tie = false;
    private boolean drawBackground = true;
    private bike player, enemy;
    private int time = 1, ms = 0, randTimeLow = 15, randTimeHigh = 17;
    private int timeChangeDir = randint(15, 20);
    public boolean ready = false;
    private int[] otherTurns = {0, RIGHT, LEFT, DOWN, UP};
    public GamePanel1(){
        player = new bike(50, 320, RIGHT);
        enemy = new bike(600, 320, LEFT);
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
       if(keys[KeyEvent.VK_S]){
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
       player.setDir();
       player.move();
       if (player.hitEdgde()){
           playerDead = true;
       }
       if(collide(player, enemyTrail)){
           if(tie()){
               tie = true;
           }
           else{
               playerDead = true;
           }
        }
       if(collide(player, playerTrail)){
            playerDead = true;
       }
       fillBoard(player, playerTrail);
    }
    public void enemyAI(){
        enemyDetection();
        enemy.closeToBorder();
        if(time >= timeChangeDir){
            if(enemy.onBorder()){
                int dc = enemy.borderTurn();
                enemy.addDir(dc);
                time = 0;
                timeChangeDir = randint(randTimeLow, randTimeHigh);
            }
            else{
                int dc = enemy.changeDir();
                if (safeToTurn(dc)){
                    enemy.addDir(dc);
                }
                time = 0;
                timeChangeDir = randint(randTimeLow, randTimeHigh);
            }
        }
        enemy.setDir();
        enemy.move();
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
        if (enemy.hitEdgde()){
            enemyDead = true;
        } 
        fillBoard(enemy, enemyTrail);
    }


    public void paint(Graphics g){
        if(drawBackground){
            g.setColor(new Color(0, 0, 45));
            g.fillRect(0, 0, width + 30, height + 30);
            if(backDelay>2){
                drawBackground = false;
            }
            backDelay++;
        }
        g.setColor(new Color(187,155,252));
        player.draw(g);
        g.setColor(new Color(253, 255,161));
        enemy.draw(g);
    }
    public void draw(Graphics g){
        g.setColor(new Color(0, 0, 45));
        g.fillRect(0, 0, width+30, height+30);
    }
    public void timer(){
        ms++;
        if (ms%10==0){
            time++;
        }
    }
    public void fillBoard(bike b, int trail){
        board[b.getX()/5][b.getY()/5] = trail;
    }
    public boolean collide(bike b, int otherTrail){
        if(board[b.getX()/5][b.getY()/5] == otherTrail){
            return true;
        }
        return false;
    }
    public boolean tie(){
        if(player.getX()/5 == enemy.getX()/5 && player.getY()/5 == enemy.getY()/5){
            return true;
        }
        return false;
    }
    public int checkFinished(){
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
        if(board[(enemy.getX()+enemy.getxDir()*4)/5][(enemy.getY()+enemy.getyDir()*4)/5] != 0){
            int dc = enemy.changeDir();
                if (safeToTurn(dc)){
                    enemy.addDir(dc);
                }
                else{
                    enemy.addDir(otherTurns[dc]);
                }
        }
        else if( (enemy.getX()+enemy.getxDir()*5)/5 < 5 || (enemy.getX()+enemy.getxDir()*5)/5 > 165){
            int dc = enemy.changeDir();
                if (safeToTurn(dc)){
                    enemy.addDir(dc);
                }
                else{
                    enemy.addDir(otherTurns[dc]);
                }
        }
    }
    public boolean safeToTurn(int dc){
        boolean stt = true;
        // System.out.println(dc +" TURNING");
        //FINISH: SEE IF SAFE TO RANDOMLY TURN
        int potentialX = xSpeed(dc);
        int potentialY = ySpeed(dc);
        for(int i = 1; i<=4; i++){
            // System.out.println((enemy.getX()+potentialX*i)/5 + " " + (enemy.getY()+potentialY*i)/5);
            if(board[(enemy.getX()+potentialX*i)/5][(enemy.getY()+potentialY*i)/5] != 0){
                stt = false;
                break;
            }
        }

        return stt;
    }
    public int xSpeed(int dc){
        if(dc == LEFT){
            return -5;
        }
        else if (dc == RIGHT){
            return 5;
        }
        return 0;
    }
    public int ySpeed(int dc){
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
    private int leftBorderEdge = 20, rightBorderEdge = 20, topBorderEdge = 20, botBorderEdge = 20;
    private boolean yBorder = false, xBorder = true;
    // private Queue<Integer> queuedMoves = new LinkedList<>();
    private int moveFrame = 0;
    private int curMove = 0;

    public bike(int x, int y, int dir){
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
    public void move(){    
        x+=xdir;
        y+=ydir;
        
    }
    public void addDir(int d){
        curMove = d;
    }
    public void setDir(){//CHANGEED MOVEMENTS (MERGE)
        // if(curMove == 0){
        //     if(queuedMoves.size()>0){
        //         curMove = queuedMoves.remove();
        //     }
        //     else{
        //         return;
        //     }
        // }
        System.out.println(curMove);
        if(curMove == 0){
            return;
        }

        if(curMove == LEFT || curMove == RIGHT){
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
    public void dirChange(int d){
        System.out.println(d);
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
        else if(d ==-1){
            // System.out.println("ERROR, UNABLE TO GET WHICH BORDER THE ENEMY IS ON");
        }
    }
    public int changeDir(){
        int ch = randint(0, 1);
        int dchange = 0;
        if(dir == UP || dir == DOWN){
            dchange = (ch == 0) ? LEFT:RIGHT;
        }
        else if(dir == LEFT || dir == RIGHT){
            dchange = (ch == 0) ? UP:DOWN;
        }
        if(dchange == 0){
            // System.out.println("ERROR, UNABLE TO GET WHICH DIRECTION THE ENEMY IS ON");
        }
        return dchange;
    }
    public void closeToBorder(){
        if(xBorder){
            if(x < width - rightBorderEdge && x > leftBorderEdge){
                xBorder = false;
            }
        }
        if(yBorder){
            if(y < height - botBorderEdge && y > topBorderEdge){
                yBorder = false;
            }
        }
        if((x >= width - rightBorderEdge || x <= leftBorderEdge) && !xBorder){
            System.out.println("BORDER TURN");
            if(y < height / 2){  
                addDir(DOWN);
                System.out.println("TURNING DOWN");
            }
            else{
                addDir(UP);
                System.out.println("TURNING UP");
            }
            xBorder = true;
        }
        // System.out.println(y + " " + (height-botBorderEdge));
        else if(y >= height - botBorderEdge || y <= topBorderEdge && !yBorder){
            if(x<width/2){
                addDir(RIGHT);
                System.out.println("TURNING RIGHT");
            }
            else{
                System.out.println("TURNING LEFT");
                addDir(LEFT);
            }
            yBorder = true;
        }
    }
    public boolean onBorder(){
        if(x>= width-leftBorderEdge || x<=rightBorderEdge){
            System.out.println("X BORDER");
            return true;
        }
        if(y>=height-botBorderEdge|| y<=topBorderEdge){
            System.out.println("Y BORDER");
            return true;
        }
        return false;
    }
    public int borderTurn(){
        if(x>= width-20){
            return LEFT;
        }
        else if(x<=20){
            return RIGHT;
        }
        else if(y>=height-20){
            return UP;
        }
        else if(y<=20){
            return DOWN;
        }
        return -1;
    }
    public void printDir(){
        if(dir == LEFT){
            System.out.println("LEFT");
        }
        if(dir == RIGHT){
            System.out.println("RIGHT");
        }
        if(dir == UP){
            System.out.println("UP");
        }
        if(dir == DOWN){
            System.out.println("DOWN");
        }
    }
    //GETTER AND SETTERS(NEED TO BE MERGED)
    //public int[] getArea(){

    //}
    public boolean hitEdgde(){
        System.out.println(x);
        if(x<5 || x> width-5 || y<5 || y>height-5){
            return true;
        }
        return false;
    }
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
    public void draw(Graphics g){
        g.fillRect(x, y, 10, 10);
    }
    public static int randint(int low, int high){
		return (int)(Math.random()*(high-low+1)+low);
	}
}
