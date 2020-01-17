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
		public void actionPerformed(ActionEvent evt){
			if(game!= null && game.ready){
                game.timer();
                game.move();//GAME FUNCTIONS ARE CALLED
                game.enemyAI();
                game.repaint();
			}
		}
	}

}
class GamePanel1 extends JPanel{
    public static final int LEFT = 1, RIGHT = 2, UP = 3, DOWN =4;
    public static final int playerTrail = 5, enemyTrail = 6;
    public final int width = 800, height = 600;
    private int[][] board = new int[170][150];
    private boolean []keys;
    private boolean drawBackground = true;
    private bike player, enemy;
    private int time = 1, ms = 0, randTimeLow = 4, randTimeHigh = 15;
    private int timeChangeDir = randint(randTimeLow, randTimeHigh);
    public boolean ready = false;
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
       player.move();
       player.setDir();
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
       if(collide(player, enemyTrail)){
           if(tie()){
               System.out.println("YOU TIED");//FIX TIE DOESNT WORK=====================================================================
           }
           else{
               System.out.println("YOU DIED");
           }
        }
       if(collide(player, playerTrail)){
            System.out.println("YOU KYS");
       }
       fillBoard(player, playerTrail);
    }
    public void enemyAI(){
        enemy.move();
        enemy.setDir();
        enemy.closeToBorder();
        if(time >= timeChangeDir){
            if(enemy.onBorder()){
                enemy.addDir(enemy.borderTurn());
                time = 0;
                timeChangeDir = randint(randTimeLow, randTimeHigh);
            }
            else{
                enemy.changeDir();
                time = 0;
                timeChangeDir = randint(randTimeLow, randTimeHigh);
            }
        }
        fillBoard(enemy, enemyTrail);
    }
    public void paint(Graphics g){
        if(drawBackground){
            g.setColor(new Color(0, 0, 45));
            g.fillRect(0, 0, width + 30, height + 30);//HOW TO CALL THIS ONLY ONCE????
            if(time>1){
                drawBackground = false;
            }
        //BLACK BACKGROUND ADD
        }
        g.setColor(new Color(187,155,252));
        player.draw(g);
        g.setColor(new Color(253,255,161));
        enemy.draw(g);
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
    public final int width = 800, height = 600;
    private int x, y, xdir, ydir;
    private int dir;
    private int leftBorderEdge = 30, rightBorderEdge = 10, topBorderEdge = 10, botBorderEdge = 5;
    private boolean yBorder = false, xBorder = true;
    private Queue<Integer> queuedMoves = new LinkedList<>();
    int curMove = 0;
    public bike(int x, int y, int dir){
        this.x = x;
        this.y = y;
        if(dir == LEFT){
            this.dir = LEFT;
            this.xdir = -5;
        }
        if(dir == RIGHT){
            this.dir = RIGHT;
            this.xdir = 5;
        }
    }
    public void move(){
        x+=xdir;
        y+=ydir;
    }
    public void addDir(int d){
        if(queuedMoves.size()<2){
            queuedMoves.add(d);
        }
    }
    public void setDir(){
        if(curMove == 0){
            if(queuedMoves.size()>0){
                curMove = queuedMoves.remove();
            }
            else{
                return;
            }
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
        if(d==LEFT && dir != RIGHT){
            xdir = -5;
            ydir = 0;
            dir = LEFT;
        }
        else if(d==RIGHT && dir != LEFT){
            xdir = 5;
            ydir = 0;
            dir = RIGHT;
        }
        else if(d==UP && dir != DOWN){
            ydir = -5;
            xdir = 0;
            dir = UP;
        }
        else if(d==DOWN && dir != UP){
            ydir = 5;
            xdir = 0;
            dir = DOWN;
        }
        else if(d ==-1){
            System.out.println("ERROR, UNABLE TO GET WHICH BORDER THE ENEMY IS ON");
        }
    }
    public void changeDir(){
        int ch = randint(0, 1);
        if(dir == UP || dir == DOWN){
            dir = (ch == 0) ? LEFT:RIGHT;
        }
        else if(dir == LEFT || dir == RIGHT){
            dir = (ch == 0) ? UP:DOWN;
        }
        addDir(dir);
        printDir();
    }
    public void closeToBorder(){
        if(xBorder){
            if(x < width - leftBorderEdge && x > rightBorderEdge){
                xBorder = false;
            }
        }
        if(yBorder){
            if(y < height - botBorderEdge && y > topBorderEdge){
                yBorder = false;
            }
        }
        if((x >= width - leftBorderEdge || x <= rightBorderEdge) && !xBorder){//FIX BORDER SO ITS IN LINE WITH PLAYER
            if(y < height / 2){
                addDir(DOWN);
            }
            else{
                addDir(UP);
            }
            xBorder = true;
        }
        if(y >= height - botBorderEdge || y <= topBorderEdge && !yBorder){
            if(x<width/2){
                addDir(RIGHT);
            }
            else{
                addDir(LEFT);
            }
            yBorder = true;
        }
    }
    public boolean onBorder(){
        if(x>= width-leftBorderEdge || x<=rightBorderEdge){
            return true;
        }
        if(y>=height-botBorderEdge|| y<=topBorderEdge){
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
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public void draw(Graphics g){
        g.fillRect(x, y, 10, 10);
    }
    public static int randint(int low, int high){
		return (int)(Math.random()*(high-low+1)+low);
	}
}
