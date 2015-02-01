import com.nukethemoon.tools.ani.BaseAnimation;
import javax.swing.*;

/**
 * This is an example that animates a JFrame from Java Swing.
 * Simply run the main method.
 */
public class SwingExample {

	/**
	 * Moves an assigned JFrame from startX to endX as a sinus waveform.
	 */
	public static class JFrameAnimation extends BaseAnimation {

		private JFrame frame;
		private int startX;
		private int endX;

		private final int yPositio = 200;
		private final int yAmplitude = 20;
		private final float yFrequence = 5.0f;

		public JFrameAnimation(JFrame pFrame, int pStartX, int pEndX) {
			super(2000); // duration in millisecons
			frame = pFrame;
			startX = pStartX;
			endX = pEndX;
		}

		@Override
		protected void onProgress(float pProgress) {
			float currentDistance = (float) (endX - startX) * pProgress;
			int x = startX + (int) currentDistance;
			int y = y + (int) (Math.sin(pProgress * Math.PI * yFrequence) * yAmplitude);
			frame.setLocation(x, y);
		}

	}

	/**
	 * Initializes the JFrame and the animaiton.
	 */
	private static void createAndShowGUI() {
		// init swing components
		JFrame frame = new JFrame("Ani Swing Example");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel label = new JLabel(" Ani ");
		frame.getContentPane().add(label);
		frame.pack();
		frame.setVisible(true);
		frame.setLocation(10, 55);

		// init animation
		Ani controller = new Ani(50); // update rate in milliseconds
		controller.add(new JFrameAnimation(frame, 100, 400));

	}

	/**
	 * Main entry point.
	 * @param args
	 */
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}