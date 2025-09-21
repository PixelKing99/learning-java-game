
import javax.swing.*;
import java.io.IOException;
import java.util.zip.DataFormatException;

public class App {
	
	public static void main(String[] args) throws IOException, DataFormatException {
		
		JFrame frame = new JFrame("Test");
		frame.setLocationRelativeTo(null);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Game game = new Game();
		frame.add(game);
		frame.pack();
		game.requestFocus();
		frame.setVisible(true);

	
	}


}
