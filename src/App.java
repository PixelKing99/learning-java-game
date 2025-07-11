import javax.swing.JFrame;
import java.awt.*;
import java.sql.Time;

public class App {
	public static void main(String[] args) {
		
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
