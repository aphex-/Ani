import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nukethemoon.tools.ani.Ani;
import com.nukethemoon.tools.ani.BaseAnimation;


public class AndroidExample extends Activity {

	/**
	 * Animation class to animate a view.
	 */
	public static class ViewAnimation extends BaseAnimation {

		private View view;

		public ViewAnimation(View pView) {
			super(3000);
			view = pView;
		}

		@Override
		protected void onProgress(float v) {
			view.setX(300 * v);
			view.setY(300 * v);
			view.setRotation(360 * v * 3);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// init android views
		TextView textView = initTextView();

		// start the animation
		Ani controller = new Ani(10);
		controller.add(new ViewAnimation(textView));
	}

	/**
	 * Android content view initialization.
	 * @return A text view that is added to the content view.
	 */
	private TextView initTextView() {
		LinearLayout mainLayout = new LinearLayout(this);
		TextView textView = new TextView(this);

		textView.setText("Ani Android Example");
		textView.setBackgroundColor(Color.GRAY);

		textView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		mainLayout.addView(textView);

		setContentView(mainLayout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		return textView;
	}
}
