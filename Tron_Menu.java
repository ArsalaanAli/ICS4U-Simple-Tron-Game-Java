import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Tron_Menu extends JFrame implements ActionListener {
	JButton startButton, insButton;
	int page = 0;
	JPanel instuctions;
    public Tron_Menu() {
    	super("TRON MENU");
    	Container pane = getContentPane();
    	pane.setLayout(null);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    	setSize(800, 650);
    	startButton = new JButton("START");
    	startButton.setSize(150, 50);
    	startButton.setLocation(300, 200);
    	insButton = new JButton("INSTRUCTIONS");
    	insButton.setSize(150, 50);
    	insButton.setLocation(300, 300);

    	pane.add(insButton);
    	pane.add(startButton);

    	setVisible(true);
    }

    public void actionPerformed(ActionEvent ev){
    	if(ev.getSource() == startButton){
    		page = 1;
    		//ADD START GAME FUNCTION HERE
    	}
    	else if(ev.getSource() == insButton){
    		page = 2;
    		//instuctions.setVisible(true);
    	}
    }

	public static void main(String [] args){
    	Tron_Menu frame = new Tron_Menu();
    }
}
