package render;

import game.Server;
import game.input.UserInput;
import render.Render;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;


public class Panel extends JPanel implements Runnable {
	
	private Render render;
	private UserInput userInput;
	
	public Supplier<String> getFps;
	public Supplier<String> getTps;
	
	public Panel(Server server) {
		
		setPreferredSize(new Dimension(700, 700));
		setBackground(new Color(0, 0, 0));
		setFocusable(true);
		
		
		this.userInput = new UserInput(this::getMousePosition);
		this.render = new Render(server, userInput, this);
		
		
		addKeyListener(userInput);
		addMouseListener(userInput);
	}
	
	
	
//	idk what a better name for this would be
	public void initializePerformanceGetters(Supplier<String> getFps, Supplier<String> getTps) {
		this.getFps = getFps;
		this.getTps = getTps;
	}
	


//	i think the way paintComponent and actionPerformed were intended to work (by the tutorial i watched) is
//	basically my renderLoop calls actionPerformed on a loop which then calls the repaint method with is defined in JPanel
//	the repaint method is similar to my rendering method but it also handles stuff like the background,
//	the repaint method also calls the paint component method which is the intended place to put all of my drawing code

//	so basically run just calls my render method but a bit more complicated
//	i might be completely wrong but if so i have no clue how this works
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		render.render(g);
	}
	
	public UserInput getUserInput() {
		return userInput;
	}
	
	@Override
	public void run() {
		repaint();
	}
}
