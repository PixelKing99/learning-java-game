package render;

import game.Server;
import game.input.UserInput;
import render.Render;
import util.ConcurrentRateLoop;
import util.DynamicString;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;


public class Panel extends JPanel implements Runnable {
	
	private Render render;
	
	private ConcurrentRateLoop renderLoop;
	
	public Panel(Render render) {
		
		this.render = render;
		
		setPreferredSize(new Dimension(700, 700));
		setBackground(new Color(0, 0, 0));
		setFocusable(true);
		
		
		renderLoop = new ConcurrentRateLoop<>(Render.DEFAULT_FPS, this, "renderThread"); // this could probably be in the main thread but idk
		
		Frame.hud.add(new DynamicString("fps: ").add(renderLoop::getDebugData));
	}
	
	public void resume() {
		renderLoop.resume();
	}
	


//	i think the way paintComponent and actionPerformed were intended to work (by the tutorial i watched) is
//	basically my renderLoop calls actionPerformed on a loop which then calls the repaint method with is defined in JPanel
//	the repaint method is similar to my rendering method but it also handles stuff like the background,
//	the repaint method also calls the paint component method which is the intended place to put all of my drawing code

//	so basically run just calls my render method but a bit more complicated
//	i might be completely wrong but if so i have no clue how this works
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		render.render(g, getWidth(), getHeight());
	}
	
	
	@Override
	public void run() {
		repaint();
	}
}
