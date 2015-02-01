import javax.swing.*;

public class SwingExample {

	/**
	 * Moves an assigned JFrame from startX to endX as a sinus waveform.
	 */
	public static class JFrameAnimation extends AbstractAnimation {

		private JFrame frame;
		private int startX;
		private int endX;

		private final int y = 200;
		private final int yAmplitude = 20;
		private final float yFrequence = 5.0f;

		public JFrameAnimation(JFrame pFrame, int pStartX, int pEndX) {
			super(2000);
			frame = pFrame;
			startX = pStartX;
			endX = pEndX;
		}

		@Override
		protected void onProgress(float pProgress) {
			startX++;
			float currentDistance = (float) (endX - startX) * pProgress;
			frame.setLocation(
					startX + (int) currentDistance,
					y + (int) (Math.sin(pProgress * Math.PI * yFrequence) * yAmplitude));
		}

		@Override
		protected void onFinish() {
			frame.setLocation(endX, y);
		}

		@Override
		protected void onStart() {
			frame.setLocation(startX, y);
		}

		@Override
		protected void onLoopStart(int pLoopIndex) {

		}
	}

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
		AnimationController controller = new AnimationController(50);
		controller.addAnimation(new JFrameAnimation(frame, 100, 400));

	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}