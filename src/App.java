
import render.Panel;
import game.Server;
import render.Render;
import util.ConcurrentRateLoop;

import javax.swing.*;
import java.io.IOException;
import java.util.zip.DataFormatException;

public class App {
	
	public static void main(String[] args) throws DataFormatException, IOException {
		
		JFrame frame = new JFrame("Very Fun Game");
		frame.setLocationRelativeTo(null);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//		annoying order and stuff cause Server needs UserInput which needs Panel
		Server server = new Server();
		
		Panel panel = new Panel(server);
		
		server.initializeUserInput(panel.getUserInput());
		
		
		
		ConcurrentRateLoop serverLoop = new ConcurrentRateLoop(Server.DEFAULT_TPS, server, "serverThread");
		ConcurrentRateLoop renderLoop = new ConcurrentRateLoop(Render.DEFAULT_FPS, panel, "renderThread"); // this could probably be in the main thread but idk
		
		panel.initializePerformanceGetters(renderLoop::getDebugData, serverLoop::getDebugData);
		
		
		frame.add(panel);
		frame.pack();
		panel.requestFocus();
		frame.setVisible(true);
	}
}