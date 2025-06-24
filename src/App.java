import javax.swing.JFrame;
import java.awt.*;
import java.sql.Time;

public class App {
	public static void main(String[] args) {
		
		JFrame frame = new JFrame("Test");
		frame.setLocationRelativeTo(null);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Game testGame = new Game();
		frame.add(testGame);
		frame.pack();
		testGame.requestFocus();
		frame.setVisible(true);
	}
}
