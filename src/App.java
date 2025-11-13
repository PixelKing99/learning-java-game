
import game.input.UserInput;
import render.Panel;
import game.Server;
import render.Render;

import javax.swing.*;
import java.io.IOException;
import java.util.zip.DataFormatException;

public class App {
	
//	gonna need to move all of the frame stuff etc somewhere else cause i should just start the render and server loop here
	public static void main(String[] args) throws DataFormatException, IOException {
		
		JFrame frame = new JFrame("Very Fun Game");
		frame.setLocationRelativeTo(null);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	
		Server server = new Server();
		
		Render render = new Render(server);
		Panel panel = new Panel(render);
		
		UserInput.init(panel);
		
		render.initializeRenderers();
		
		frame.add(panel);
		frame.pack();
		panel.requestFocus();
		
		
//		by default the thread is paused so have to resume
//		the server stays paused until starting the game
		panel.start();
		
		
		
		frame.setVisible(true);
		
	}
}