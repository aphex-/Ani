import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nukethemoon.tools.ani.BaseAnimation;
import com.nukethemoon.tools.ani.Ani;

/**
 * This is an example to use Ani with LibGDX.
 * You need to have the file 'badlogic.jpg' in your asset folder.
 * http://libgdx.badlogicgames.com
 */
public class LibGDXExample extends ApplicationAdapter {

	private Sprite sprite;
	private SpriteBatch batch;

	/**
	 * A class to animatie a sprite.
	 */
	private static final class SpriteAnimation extends BaseAnimation {

		private Sprite sprite;

		public SpriteAnimation(Sprite pSprite) {
			super(1500); // duration in milliseconds
			sprite = pSprite;
		}

		@Override
		protected void onProgress(float pProgress) {
			sprite.setAlpha(pProgress);
			sprite.setScale((float) Math.sin(
					pProgress * pProgress * Math.PI * 0.6f));
			sprite.setRotation(360f * pProgress);
		}
	}

	@Override
	public void create() {
		batch = new SpriteBatch();
		sprite = new Sprite(new Texture("badlogic.jpg"));
		Ani controller = new Ani(10); // update rate in milliseconds
		controller.add(new SpriteAnimation(sprite));
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		sprite.draw(batch);
		batch.end();
	}
}
