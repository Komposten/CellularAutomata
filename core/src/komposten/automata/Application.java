package komposten.automata;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.profiling.GL30Profiler;

import komposten.automata.backend.Engine;
import komposten.automata.backend.ShaderFactory;


public class Application extends ApplicationAdapter
{
	private Camera camera;
	private SpriteBatch batch;
	
	private BitmapFont font;
	
	private Engine engine;
	
	private double timer;
	private double gcTimer;
	
	private boolean debug;
	
	@Override
	public void create()
	{
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(width, height);
		camera.translate(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		camera.update();
		
		ShaderFactory.initialise(camera);
		
		engine = new Engine(width, height);
		batch = new SpriteBatch();
		font = new BitmapFont();
		
		Gdx.input.setInputProcessor(processor);
	}


	@Override
	public void render()
	{
		timer += Gdx.graphics.getDeltaTime();
		gcTimer += Gdx.graphics.getDeltaTime();
		
		if (gcTimer > 60)
		{
			runGC();
			gcTimer = 0;
		}
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
		
		engine.update();
		engine.render();
		
		batch.begin();
		int x = 10;
		int y = Gdx.graphics.getHeight() - 10;
		font.draw(batch, "Automata: " + engine.getCurrentAutomata().getName(), x, y);
		if (debug)
		{
			drawDebug(x, y - 30);
		}
		engine.renderText(font, batch);
		batch.end();
	}


	private void runGC()
	{
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				System.gc();
			}
		});
		thread.start();
	}


	private void drawDebug(int x, int y)
	{
		String fps = "FPS: " + Gdx.graphics.getFramesPerSecond();
		String drawCalls = "Draw calls: " + GL30Profiler.drawCalls;
		String shaderSwitches = "Shader switches: " + GL30Profiler.shaderSwitches;
		String textureBinds = "Texture bindings: " + GL30Profiler.textureBindings;
		String time = "Timer: " + formatTime((long)(timer*1E9));
		
		font.draw(batch, fps, x, y);
		font.draw(batch, drawCalls, x, y - 20);
		font.draw(batch, shaderSwitches, x, y - 40);
		font.draw(batch, textureBinds, x, y - 60);
		font.draw(batch, time, x, y - 80);
		font.getCache().clear();

		GL30Profiler.reset();
	}


	private String formatTime(long nanoTime)
	{
		int minutes = (int) (nanoTime / 60E9d);
		int seconds = (int) (nanoTime / 1E9d) - (minutes * 60);
		int hundreds = (int) (((nanoTime / 1E9d) % 1) * 100);
		return String.format("%1$02d:%2$02d:%3$02d", minutes, seconds, hundreds);
	}
	/*
	 * x = 1234567890
	 * minutes = x / 60E9 = 0
	 * seconds = x / 1E9 - 0 = 1.23 = 1
	 * hundreds = x / 1E7 - 100 = 123 - 100 = 23
	 */
	
	
	private void toggleDebug()
	{
		debug = !debug;
		
		if (debug)
			GL30Profiler.enable();
		else
			GL30Profiler.disable();
	}


	@Override
	public void dispose()
	{
		engine.dispose();
	}
	
	
	private InputProcessor processor = new InputAdapter()
	{
		@Override
		public boolean keyUp(int keycode)
		{
			if (keycode == Input.Keys.F1)
			{
				toggleDebug();
				return true;
			}
			else if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.P)
			{
				engine.togglePaused();
				return true;
			}
			else if (keycode >= Input.Keys.NUM_1 && keycode <= Input.Keys.NUM_9)
			{
				engine.setAutomataIndex(keycode - Input.Keys.NUM_1);
			}
			
			return false;
		}
		
		
		@Override
		public boolean keyTyped(char character)
		{
			return false;
		}
		
		
		@Override
		public boolean keyDown(int keycode)
		{
			return false;
		}
	};
}
