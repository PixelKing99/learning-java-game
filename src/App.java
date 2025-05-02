import javax.swing.JFrame;
import java.awt.*;

public class App {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Test");
		frame.setSize(500, 500);
		frame.setLocationRelativeTo(null);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Game testGame = new Game();
		frame.add(testGame);
		frame.pack();
		frame.setVisible(true);
	}
}
